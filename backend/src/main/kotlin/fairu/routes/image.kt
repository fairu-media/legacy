package fairu.routes

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.headObject
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.NotFound
import aws.sdk.kotlin.services.s3.model.S3Exception
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.io.SdkByteReadChannel
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import com.sksamuel.scrimage.nio.PngWriter
import fairu.exception.failure
import fairu.file.File
import fairu.user.access.authenticatedUser
import fairu.utils.Config
import fairu.utils.awt.RUBIK_REGULAR
import fairu.utils.awt.SOURCE_CODE_PRO_BOLD
import fairu.utils.awt.Text
import fairu.utils.awt.pxToPt
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import naibu.ext.awt.Color
import naibu.ext.koin.get
import org.litote.kmongo.eq
import org.litote.kmongo.inc
import java.awt.Color
import java.nio.ByteBuffer

private const val FILE_NAME = "file_name"

val missingBase: ImmutableImage = run {
    val trol = ImmutableImageLoader.create()
        .fromStream(File::class.java.classLoader.getResourceAsStream("assets/images/trol.png"))
        .scaleTo(120, 120)

    val card = ImmutableImage.create(1000, 300)
        .toCanvas()
        .draw(Text("This file doesn't exist!", 50, 300 / 2 + 15, Color.WHITE, RUBIK_REGULAR.deriveFont(60.pxToPt)))
        .image
        .overlay(trol, Position.CenterRight)

    ImmutableImage.create(1000, 300)
        .padRight(50)
        .fill(Color("#1e3a8a"))
        .overlay(card)
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
                val image = missingBase.toCanvas()
                    .draw(Text(fileName, 50, 300 / 2 - 50, Color.WHITE, SOURCE_CODE_PRO_BOLD.deriveFont(60.pxToPt)))
                    .image
                    .forWriter(PngWriter())
                    .stream()

                call.respond(object : OutgoingContent.ReadChannelContent() {
                    override val contentType: ContentType = ContentType.Image.PNG

                    override fun readFrom(): ByteReadChannel = image.toByteReadChannel()
                })
            } else {
                /* fetch file from the S3 bucket */
                val s3obj = try {
                    client.headObject {
                        bucket = config.s3.bucket
                        key    = fileName
                    }
                } catch (ex: NotFound) {
                    null
                }

                if (s3obj == null) {
                    /* remove file from the database if its S3 object has been deleted. */
                    file.delete()
                    failure(HttpStatusCode.NotFound, "Missing an S3 object for file '$fileName' (${file.id}).")
                }

                /* check if anonymous view or non-owner view */
                val user = call.authenticatedUser
                if (user == null || user.id != file.userId) {
                    /* increment file hit counter */
                    File.collection.findOneAndUpdate(
                        File::id eq file.id,
                        inc(File::hits, 1)
                    )
                }

                /* respond with file stream */
                val ct = ContentType.parse(file.contentType)
                client.getObject(GetObjectRequest {
                    bucket = config.s3.bucket
                    key    = fileName
                }) {
                    when (val body = it.body) {
                        is ByteStream.Buffer -> call.respondBytes(body.bytes(), ct)

                        is ByteStream.OneShotStream -> call.respondBytesWriter(ct) {
                            body.readFrom().transferTo(this)
                        }

                        is ByteStream.ReplayableStream -> call.respondBytesWriter(ct) {
                            body.newReader().transferTo(this)
                        }

                        else -> failure(HttpStatusCode.InternalServerError, "S3 did not return file stream")
                    }
                }
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
