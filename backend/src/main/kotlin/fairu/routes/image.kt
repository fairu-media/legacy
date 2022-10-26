package fairu.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import naibu.ext.koin.get
import org.noelware.remi.s3.S3StorageTrailer

val fileName = "file_name"

fun Route.image() = get("/{$fileName}") {
    val file = call.parameters[fileName]
        ?.let { get<S3StorageTrailer>().fetch(it) }
        ?: return@get call.respond(HttpStatusCode.NotFound)

    call.respond(file.toInputStream())
}
