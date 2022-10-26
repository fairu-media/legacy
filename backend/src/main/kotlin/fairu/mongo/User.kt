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
data class User(val username: String, val password: String) : Document {
    companion object : DocumentClass<User>(get<CoroutineDatabase>().getCollection())

    /** ID of this Snowflake */
    val id: Snowflake = Snowflake(now())

    /** Date of last update */
    @SerialName("last_updated_at")
    var lastUpdatedAt: Instant = now()

    /**
     * Save this File to the database.
     */
    suspend fun save(): User {
        lastUpdatedAt = now()
        save(this, User::id eq id)

        return this
    }
}
