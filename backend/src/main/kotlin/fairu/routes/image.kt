package fairu.routes

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import com.sksamuel.scrimage.nio.PngWriter
import fairu.exception.failure
import fairu.file.File
import fairu.utils.awt.RUBIK_REGULAR
import fairu.utils.awt.SOURCE_CODE_PRO_BOLD
import fairu.utils.awt.Text
import fairu.utils.awt.pxToPt
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.jvm.javaio.*
import naibu.ext.awt.Color
import naibu.ext.koin.get
import org.litote.kmongo.eq
import org.litote.kmongo.inc
import org.noelware.remi.s3.S3StorageTrailer
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import java.awt.Color

private const val FILE_NAME = "file_name"

val trol by lazy {
    ImmutableImageLoader.create()
        .fromStream(File::class.java.classLoader.getResourceAsStream("assets/images/trol.png"))
        .scaleTo(120, 120)
}

val missingBase: ImmutableImage = run {
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

    get {
        val fileName = call.parameters[FILE_NAME]
            ?: failure(HttpStatusCode.NotFound, "Invalid or missing 'file_name' parameter.")

        val file = File.find(File::fileName eq fileName)
        if (file == null) {
            val image = missingBase.toCanvas()
                .draw(Text(fileName, 50, 300 / 2 - 50, Color.WHITE, SOURCE_CODE_PRO_BOLD.deriveFont(60.pxToPt)))
                .image

            call.respondBytesWriter(ContentType.Image.PNG) {
                PngWriter().write(image, image.metadata, toOutputStream())
            }
        } else {
            val trailer = get<S3StorageTrailer>()

            /* fetch file from the S3 bucket */
            val s3obj = try {
                trailer.fetch(file.fileName)
            } catch (ex: NoSuchKeyException) {
                null
            }

            if (s3obj == null) {
                /* remove file from the database if its S3 object has been deleted. */
                file.delete()
                failure(HttpStatusCode.NotFound, "Missing an S3 object for file '$file'.")
            }

            /* increment file hit counter */
            File.collection.findOneAndUpdate(
                File::id eq file.id,
                inc(File::hits, 1)
            )

            /* respond with file stream */
            call.response.header(
                HttpHeaders.ContentType,
                file.contentType
            )

            call.respond(s3obj.toInputStream())
        }
    }
}
