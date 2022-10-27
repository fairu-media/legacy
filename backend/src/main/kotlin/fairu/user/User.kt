package fairu.user

import fairu.utils.Snowflake
import fairu.utils.mongo.DocumentClass
import fairu.utils.mongo.SnowflakeDocument
import fairu.utils.mongo.collection
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import naibu.time.now
import org.litote.kmongo.eq

@Serializable
data class User(val username: String, val password: String) : SnowflakeDocument {
    companion object : DocumentClass<User>(collection())

    /** ID of this Snowflake */
    override val id: Snowflake = Snowflake.generate()

    /** Date of last update */
    @SerialName("last_updated_at")
    var lastUpdatedAt: Instant = now()

    override suspend fun save() {
        lastUpdatedAt = now()
        save(this, User::id eq id)
    }

    override suspend fun delete(): Boolean {
        return delete(User::id eq id) == 1L
    }
}
