package fairu.routes.files

import fairu.file.File
import fairu.utils.ext.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetFilesResponse(
    val message: String,
    @SerialName("file_count")
    val fileCount: Long
)

fun Route.files() = route("/files") {
    get {
        val files = File.collection.countDocuments()
        respond(
            GetFilesResponse("Fairu currently serves $files file${if (files == 1L) "" else "s"}", files)
        )
    }

    file()

    upload()
}
