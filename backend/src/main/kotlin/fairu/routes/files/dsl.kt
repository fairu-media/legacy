package fairu.routes.files

import com.mongodb.client.model.Accumulators
import com.mongodb.client.model.BsonField
import com.mongodb.client.model.Projections
import fairu.file.File
import fairu.utils.ext.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.Document
import org.litote.kmongo.coroutine.aggregate
import org.litote.kmongo.group
import org.litote.kmongo.project

@Serializable
data class GetFilesResponse(
    @SerialName("file_count")
    val fileCount: Long,
    @SerialName("total_hits")
    val totalHits: Long,
)

@Serializable
data class TotalHits(
    @SerialName("total_hits")
    val totalHits: Long
)

fun Route.files() = route("/files") {
    get {
        // TODO: merge into a single aggregate call maybe?
        val files = File.collection.countDocuments()

        val hits = File.collection.aggregate<TotalHits>(
            group("", Accumulators.sum("hits", "${'$'}hits")),
            project(Projections.excludeId(), Document("total_hits", "${'$'}hits")),
        ).first()

        respond(GetFilesResponse(files, hits?.totalHits ?: -1))
    }

    file()

    upload()
}
