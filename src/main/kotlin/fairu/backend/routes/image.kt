package fairu.backend.routes

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.headObject
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.NoSuchKey
import aws.smithy.kotlin.runtime.InternalApi
import aws.smithy.kotlin.runtime.content.toByteArray
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import com.sksamuel.scrimage.nio.PngWriter
import fairu.backend.exception.failure
import fairu.shared.file.File
import fairu.shared.user.access.authenticatedUser
import fairu.backend.utils.Config
import fairu.backend.utils.awt.RUBIK_REGULAR
import fairu.backend.utils.awt.SOURCE_CODE_PRO_BOLD
import fairu.backend.utils.awt.Text
import fairu.backend.utils.awt.pxToPt
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import naibu.ext.awt.Color
import naibu.ext.koin.get
import org.litote.kmongo.eq
import java.awt.Color
import kotlin.coroutines.coroutineContext

private const val FILE_NAME = "file_name"

val STATUS_IMAGE_BASE: ImmutableImage = run {
    val troll = ImmutableImageLoader.create()
        .fromStream(File::class.java.classLoader.getResourceAsStream("assets/images/trol.png"))
        .scaleTo(120, 120)

    val card = ImmutableImage.create(1000, 300)
        .toCanvas()
        .image
        .overlay(troll, Position.CenterRight)

    ImmutableImage.create(1000, 300)
        .padRight(50)
        .fill(Color("#1e3a8a"))
        .overlay(card)
}

fun createBaseStatusImage(text: String): ImmutableImage {
    return STATUS_IMAGE_BASE.toCanvas()
        .draw(Text(text, 50, 300 / 2 + 15, Color.WHITE, RUBIK_REGULAR.deriveFont(60.pxToPt)))
        .image
}

val BASE_NOT_FOUND = createBaseStatusImage("This file doesn't exist.")
val BASE_MISSING_OBJECT = createBaseStatusImage("Couldn't find an S3 object for this file.")
val BASE_EMPTY = createBaseStatusImage("This file is empty.")

suspend fun PipelineContext<Unit, ApplicationCall>.respondStatusImage(base: ImmutableImage, fileName: String) {
    val image = base.toCanvas()
        .draw(Text(fileName, 50, 300 / 2 - 50, Color.WHITE, SOURCE_CODE_PRO_BOLD.deriveFont(60.pxToPt)))
        .image
        .forWriter(PngWriter())
        .stream()

    call.respond(object : OutgoingContent.ReadChannelContent() {
        override val contentType: ContentType = ContentType.Image.PNG

        override fun readFrom(): ByteReadChannel = image.toByteReadChannel()
    })
}

suspend fun callScope(): CoroutineScope =
    CoroutineScope(coroutineContext + SupervisorJob(coroutineContext.job))

@OptIn(InternalApi::class)
fun Route.image() = route("/{$FILE_NAME}") {
    install(PartialContent)

    val client = get<S3Client>()
    val config = get<Config.Fairu>()

    suspend fun File.contentLength(): Long {
        /* if the file has a content length, return it. */
        if (contentLength != null) {
            return contentLength
        }

        /* otherwise fetch it from S3: */
        val obj = client.headObject {
            bucket = config.s3.bucket
            key = fileName
        }

        return obj.contentLength
    }

    authenticate("session", "access_token", optional = true) {
        head {
            val fileName = call.parameters[FILE_NAME]
                ?: return@head call.response.status(HttpStatusCode.BadRequest)

            val file = File.find(File::fileName eq fileName)
                ?: return@head call.response.status(HttpStatusCode.NotFound)

            call.response.header(HttpHeaders.ContentType, file.contentType)

            call.response.header(HttpHeaders.ContentLength, file.contentLength())
        }

        get {
            val fileName = call.parameters[FILE_NAME]
                ?: failure(HttpStatusCode.NotFound, "Invalid or missing 'file_name' parameter.")

            val file = File.find(File::fileName eq fileName)
            if (file == null) {
                respondStatusImage(BASE_NOT_FOUND, fileName)
            } else try {
                // something to do with the ranges.
                val rangeHeader = call.request.ranges()
                if (rangeHeader != null) {
                    call.response.status(HttpStatusCode.PartialContent)
                }

                /* fetch file from the S3 bucket */
                client.getObject(GetObjectRequest {
                    bucket = config.s3.bucket
                    key = fileName
                    range = "$rangeHeader"
                }) { obj ->
                    obj.contentRange?.let {
                        call.response.header(HttpHeaders.ContentRange, it)
                    }

                    /* respond with file stream */
                    val body = obj.body
                    if (body == null) {
                        /* no point in having a blank file */
                        file.delete()
                        respondStatusImage(BASE_EMPTY, fileName)
                    } else {
                        // TODO: stream the body to the response. The s3 client reimplements Ktor IO & OkIO in the name of http library agnosticness so i have no fucking idea how i'm going to do that...
                        val ct = ContentType.parse(file.contentType)
                        call.respondBytes(body.toByteArray(), ct)

                        /* check if anonymous view or non-owner view */
                        val user = call.authenticatedUser
                        if (user == null || user.id != file.userId) {
                            /* increment file hit counter */
                            file.hit()
                        }
                    }
                }
            } catch (ex: NoSuchKey) {
                /* remove file from the database since an S3 object doesn't exist. */
                file.delete()
                respondStatusImage(BASE_MISSING_OBJECT, fileName)
            }
        }
    }
}
