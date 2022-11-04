package fairu.backend.routes.files

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.putObject
import aws.smithy.kotlin.runtime.content.ByteStream
import fairu.backend.exception.failure
import fairu.backend.file.File
import fairu.backend.user.access.AccessScope
import fairu.backend.user.access.authenticatedUser
import fairu.backend.user.access.scopedAccess
import fairu.backend.utils.Config
import fairu.backend.utils.ext.respond
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
import naibu.ext.koin.get
import org.overviewproject.mime_types.MimeTypeDetector

fun Route.upload() = scopedAccess(AccessScope.FileUpload) {
    val s3 = get<S3Client>()
    put {
        val bodyParts = call.receiveMultipart()
            .readAllParts()

        /* receive file items */
        val fileParts = bodyParts.filterIsInstance<PartData.FileItem>()
        if (fileParts.isEmpty()) {
            failure(HttpStatusCode.BadRequest, "Missing File Parts")
        }

        /* upload all file items. */
        val files = mutableListOf<UploadedFile>()
        for (filePart in fileParts) {
            /* create a unique file name */
            val name = generateUniqueId()
                .drop(6)
                .take(8) +
                (filePart.originalFileName?.substringAfterLast('.')?.let { ".$it" } ?: "")

            val ogFileName = filePart.originalFileName ?: name

            /* read file bytes. */
            val content     = filePart.provider().readBytes()
            val contentType = get<MimeTypeDetector>().detectMimeType(ogFileName) { content }

            /* create new file entry in the database */
            val file = File(
                name,
                call.authenticatedUser!!.id,
                contentType
            )

            file.save()

            // create a new S3 object
            s3.putObject {
                key    = name
                bucket = get<Config.Fairu>().s3.bucket
                body   = ByteStream.fromBytes(content)

                this.contentType = contentType
            }

            files += UploadedFile(ogFileName, file)
        }

        respond(Files(files.size, files))
    }
}

@Serializable
data class Files(
    @SerialName("total_uploaded")
    val totalUploaded: Int,
    val files: List<UploadedFile>
)

@Serializable
data class UploadedFile(
    @SerialName("original_file_name")
    val originalFileName: String,
    val document: File
)
