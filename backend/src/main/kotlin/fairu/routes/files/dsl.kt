package fairu.routes.files

import fairu.mongo.File
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.eq

@Serializable
data class GetFilesResponse(
    val message: String,
    @SerialName("file_count")
    val fileCount: Long
)

fun Route.files() = route("/files") {
    get {
        val files = File.collection.countDocuments()
        call.respond(
            GetFilesResponse("Fairu currently serves $files file${if (files == 1L) "" else "s"}", files)
        )
    }

    get("/{key}") {
        val file = File.find(File::fileName eq call.parameters["key"]!!)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(file)
    }

    authenticate {
        upload()
    }
}
