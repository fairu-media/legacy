package fairu.routes.files

import fairu.mongo.File
import fairu.utils.Snowflake
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import naibu.common.generateUniqueId
import naibu.ext.koin.get
import org.noelware.remi.s3.S3StorageTrailer

fun Route.upload() = put {
    val principal = call.principal<JWTPrincipal>()!!

    /* receive first file part */
    val filePart = call.receiveMultipart()
        .readAllParts()
        .filterIsInstance<PartData.FileItem>()
        .firstOrNull()
        ?: return@put call.respond(HttpStatusCode.BadRequest, "No content")

    /* create a unique file name */
    val name = generateUniqueId()
        .drop(6)
        .take(8) +
        (filePart.originalFileName?.substringAfterLast('.')?.let { ".$it" } ?: "")

    /* create new file entry in the database */
    val file = File(
        name,
        Snowflake(principal.payload.subject)
    )

    file.save()

    // request input stream for Remi
    val stream = filePart.streamProvider()

    // upload to remi
    get<S3StorageTrailer>().upload(name, stream, filePart.contentType?.toString() ?: "*/*")

    call.respond(file)
}
