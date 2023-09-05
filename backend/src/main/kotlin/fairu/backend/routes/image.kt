package fairu.backend.routes

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.NoSuchKey
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.io.SdkByteReadChannel
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import com.sksamuel.scrimage.nio.PngWriter
import fairu.backend.exception.failure
import fairu.backend.file.File
import fairu.backend.user.access.authenticatedUser
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
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import naibu.ext.awt.Color
import naibu.ext.koin.get
import org.litote.kmongo.eq
import java.awt.Color
import java.nio.ByteBuffer

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

fun createBaseStatusImage(text: String):ImmutableImage {
    return STATUS_IMAGE_BASE.toCanvas()
        .draw(Text(text, 50, 300 / 2 + 15, Color.WHITE, RUBIK_REGULAR.deriveFont(60.pxToPt)))
        .image
}

val BASE_NOT_FOUND      = createBaseStatusImage("This file doesn't exist.")
val BASE_MISSING_OBJECT = createBaseStatusImage("Couldn't find an S3 object for this file.")
val BASE_EMPTY          = createBaseStatusImage("This file is empty.")

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

fun Route.image() = route("/{$FILE_NAME}") {
    install(PartialContent)

    val client = get<S3Client>()
    val config = get<Config.Fairu>()

    authenticate("session", "access_token", optional = true) {
        get {
            val fileName = call.parameters[FILE_NAME]
                ?: failure(HttpStatusCode.NotFound, "Invalid or missing 'file_name' parameter.")

            val file = File.find(File::fileName eq fileName)
            if (file == null) {
                respondStatusImage(BASE_NOT_FOUND, fileName)
            } else try {
                /* fetch file from the S3 bucket */
                client.getObject(GetObjectRequest {
                    bucket = config.s3.bucket
                    key    = fileName
                }) {
                    var hit = true
                    /* respond with file stream */
                    val ct = ContentType.parse(file.contentType)
                    when (val body = it.body) {
                        is ByteStream.Buffer -> call.respondBytes(body.bytes(), ct)

                        is ByteStream.OneShotStream -> call.respondBytesWriter(ct, contentLength = body.contentLength) {
                            body.readFrom().transferTo(this)
                        }

                        is ByteStream.ReplayableStream -> call.respondBytesWriter(ct, contentLength = body.contentLength) {
                            body.newReader().transferTo(this)
                        }

                        else -> {
                            hit = false

                            /* no point in having a blank file */
                            file.delete()
                            respondStatusImage(BASE_EMPTY, fileName)
                        }
                    }

                    /* check if anonymous view or non-owner view */
                    val user = call.authenticatedUser
                    if (hit && (user == null || user.id != file.userId)) {
                        /* increment file hit counter */
                        file.hit()
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

suspend fun SdkByteReadChannel.transferTo(channel: ByteWriteChannel) {
    val buffer = ByteBuffer.allocate(4096)
    while (!isClosedForRead) {
        while (readAvailable(buffer) != -1 && buffer.remaining() > 0) {}
        buffer.flip()

        channel.writeFully(buffer)
        channel.flush()
        buffer.clear()
    }
}
