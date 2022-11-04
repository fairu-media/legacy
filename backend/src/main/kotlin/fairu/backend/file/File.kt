package fairu.backend.file

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.deleteObject
import aws.sdk.kotlin.services.s3.model.NoSuchKey
import fairu.backend.utils.Config
import fairu.backend.utils.Snowflake
import fairu.backend.utils.mongo.DocumentClass
import fairu.backend.utils.mongo.SnowflakeDocument
import fairu.backend.utils.mongo.collection
import fairu.backend.utils.serialization.InstantAsLongSerializer
import io.ktor.http.*
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import naibu.ext.koin.get
import naibu.time.now
import org.litote.kmongo.eq
import org.litote.kmongo.inc

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class File(
    /** Key used to locate this file in the S3 bucket and Database. */
    @SerialName("file_name")
    val fileName: String,

    /** User that uploaded this file. */
    @SerialName("user_id")
    val userId: Snowflake,

    /** The type of content this file contains. */
    @SerialName("content_type")
    @JsonNames("content_type", "contentType")
    val contentType: String = "*/*"
) : SnowflakeDocument {
    companion object : DocumentClass<File>(collection())

    /** ID of this Snowflake */
    override val id: Snowflake = Snowflake(now())

    /** Number of hits this file has. */
    var hits: Long = 0

    /** Date of last update */
    @SerialName("last_updated_at")
    @Serializable(with = InstantAsLongSerializer::class)
    var lastUpdatedAt: Instant = now()

    suspend fun hit(): File {
        File.collection.findOneAndUpdate(File::id eq id, inc(File::hits, 1))
        return this
    }

    override suspend fun save() {
        save(this, File::id eq id)
    }

    override suspend fun delete(): Boolean {
        return delete(File::id eq id) == 1L
    }

    /** Deletes this file and the associated S3 object. */
    suspend fun deleteAll(): Boolean {
        try {
            val config = get<Config.Fairu>()
            get<S3Client>().deleteObject {
                bucket = config.s3.bucket
                key    = fileName
            }
        } catch (_: NoSuchKey) {
        }

        return delete()
    }
}
