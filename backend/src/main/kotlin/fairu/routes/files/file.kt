package fairu.routes.files

import fairu.exception.failure
import fairu.file.File
import fairu.user.UserPrincipal
import fairu.user.access.AccessScope
import fairu.user.access.scopedAccess
import fairu.utils.Snowflake
import fairu.utils.ext.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.util.*
import org.litote.kmongo.eq

private val FileKey = AttributeKey<File>("File")

fun Route.file() = route("/{file}") {
    intercept(ApplicationCallPipeline.Call) {
        val fileKey = call.parameters["file"]
            ?: failure(HttpStatusCode.BadRequest, "Invalid or missing 'file' parameter.")

        /* fetch file from the database */
        val file = if (fileKey.toULongOrNull() != null) {
            File.find(File::id eq Snowflake(fileKey))
        } else {
            File.find(File::fileName eq fileKey)
        } ?: failure(HttpStatusCode.NotFound, "'$fileKey' is not a valid file name or id.")

        call.attributes.put(FileKey, file)
    }

    get {
        respond(call.file)
    }

    scopedAccess(AccessScope.FileDelete) {
        delete {
            val principal = call.principal<UserPrincipal>()
                ?: failure(HttpStatusCode.BadRequest, "Missing authorization, possible server failure. Try again?")

            if (call.file.userId != principal.user.id) {
                failure(HttpStatusCode.Forbidden, "You're not the original uploader of this image.")
            }

            call.file.delete()
            respond(call.file)
        }
    }
}

val ApplicationCall.file: File get() = attributes[FileKey]
