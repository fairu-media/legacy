package fairu.routes.files

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.putObject
import aws.smithy.kotlin.runtime.content.ByteStream
import fairu.exception.failure
import fairu.file.File
import fairu.user.access.AccessScope
import fairu.user.access.authenticatedUser
import fairu.user.access.scopedAccess
import fairu.utils.Config
import fairu.utils.ext.respond
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.jvm.javaio.*
import io.ktor.utils.io.pool.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import naibu.common.generateUniqueId
import naibu.ext.into
import naibu.ext.koin.get
import naibu.serialization.DefaultFormats
import naibu.serialization.deserialize
import naibu.serialization.json.Json
import org.apache.tika.Tika
import org.litote.kmongo.eq

@Serializable
data class PostFileRequest(
    @SerialName("file_name")
    val fileName: String? = null
)

fun Route.upload() = scopedAccess(AccessScope.FileUpload) {
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

        /* read file bytes. */
        val content = filePart.provider().readBytes()
        val contentType = get<Tika>()
            .detect(content)
            ?: "application/octet-stream"

        /* create new file entry in the database */
        val file = File(
            name,
            call.authenticatedUser!!.id,
            contentType
        )

        file.save()

        // create a new S3 object
        get<S3Client>().putObject {
            key    = name
            bucket = get<Config.Fairu>().s3.bucket
            body   = ByteStream.fromBytes(content)

            this.contentType   = contentType
        }

        respond(file)
    }
}
