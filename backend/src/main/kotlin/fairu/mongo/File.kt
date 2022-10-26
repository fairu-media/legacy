package fairu.mongo

import fairu.utils.Snowflake
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import naibu.ext.koin.get
import naibu.time.now
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

@Serializable
data class File(
    /** Key used to locate this file in the S3 bucket and Database. */
    @SerialName("file_name")
    val fileName: String,

    /** User that uploaded this file. */
    @SerialName("user_id")
    val userId: Snowflake
) : Document {
    companion object : DocumentClass<File>(get<CoroutineDatabase>().getCollection())

    /** ID of this Snowflake */
    val id: Snowflake = Snowflake(now())

    /** Number of hits this file has. */
    var hits: Long = 0

    /** Date of last update */
    @SerialName("last_updated_at")
    var lastUpdatedAt: Instant = now()

    /**
     * Save this File to the database.
     */
    suspend fun save(): File {
        lastUpdatedAt = now()
        save(this, File::id eq id)

        return this
    }
}
