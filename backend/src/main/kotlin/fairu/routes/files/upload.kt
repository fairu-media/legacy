package fairu.routes.files

import fairu.exception.failure
import fairu.file.File
import fairu.user.access.AccessScope
import fairu.user.access.authenticatedUser
import fairu.user.access.scopedAccess
import fairu.utils.ext.respond
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import naibu.common.generateUniqueId
import naibu.ext.into
import naibu.ext.koin.get
import naibu.serialization.DefaultFormats
import naibu.serialization.deserialize
import naibu.serialization.json.Json
import org.litote.kmongo.eq
import org.noelware.remi.core.figureContentType
import org.noelware.remi.s3.S3StorageTrailer
import software.amazon.awssdk.core.sync.RequestBody

@Serializable
data class PostFileRequest(
    @SerialName("file_name")
    val fileName: String? = null
)

fun Route.upload() = scopedAccess(AccessScope.FileUpload) {
    val trailer = get<S3StorageTrailer>()

    put {
        val bodyParts = call.receiveMultipart()
            .readAllParts()

        /* receive payload json */
        val json: PostFileRequest = bodyParts.find { it.name == "payload_json" }
            ?.into<PartData.FormItem>()?.value
            ?.deserialize(DefaultFormats.Json)
            ?: PostFileRequest()

        /* receive first file item */
        val filePart = bodyParts
            .filterIsInstance<PartData.FileItem>()
            .firstOrNull()
            ?: failure(HttpStatusCode.BadRequest, "Missing File Part.")

        /* create a unique file name */
        val name = if (json.fileName == null) {
            generateUniqueId()
                .drop(6)
                .take(8) +
                (filePart.originalFileName?.substringAfterLast('.')?.let { ".$it" } ?: "")
        } else {
            /* find conflict */
            val conflict = File.find(File::fileName eq json.fileName)
            if (conflict != null) {
                failure(HttpStatusCode.Conflict, "A file with this name has already been taken.")
            }

            json.fileName
        }

        /* create new file entry in the database */

        // request input stream for Remi
        val content       = filePart.provider()
        val contentSize   = content.remaining
        val contentStream = content.asStream()

        // figure out a content type.
        val contentType = withContext(Dispatchers.IO) {
            trailer.figureContentType(contentStream)
        }

        // create new database entry.
        val file = File(
            name,
            call.authenticatedUser!!.id,
            contentType
        )

        file.save()

        // create a new S3 object
        trailer.client.putObject({
            it.key(name)
            it.contentType(contentType)
            it.contentLength(contentSize)

            it.bucket(trailer.config.bucket)
            it.acl(trailer.config.defaultObjectAcl)
        }, RequestBody.fromInputStream(contentStream, contentSize))

        respond(file)
    }
}
