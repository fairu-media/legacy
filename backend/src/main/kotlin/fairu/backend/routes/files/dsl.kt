package fairu.backend.routes.files

import com.mongodb.client.model.Accumulators
import com.mongodb.client.model.Projections
import fairu.shared.file.File
import fairu.backend.utils.ext.respond
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.BsonDocument
import org.bson.Document
import org.litote.kmongo.coroutine.aggregate
import org.litote.kmongo.group
import org.litote.kmongo.project

@Serializable
data class GetFilesResponse(
    @SerialName("total_files")
    val totalFiles: Long,
    @SerialName("total_hits")
    val totalHits: Long,
) {
    companion object {
        suspend fun fetch(): GetFilesResponse {
            // TODO: merge into a single aggregate call maybe?
            val files = File.collection.countDocuments()

            val hits = File.collection.aggregate<BsonDocument>(
                group("", Accumulators.sum("hits", "${'$'}hits")),
                project(Projections.excludeId(), Document("total_hits", "${'$'}hits")),
            ).toList().firstOrNull()

            return GetFilesResponse(files, hits?.get("total_hits")?.asInt64()?.value ?: 0)
        }
    }
}

fun Route.files() = route("/files") {
    get {
        respond(GetFilesResponse.fetch())
    }

    file()

    upload()
}
