package fairu.user.session

import fairu.user.UserReference
import fairu.utils.Snowflake
import fairu.utils.mongo.DocumentClass
import fairu.utils.mongo.SnowflakeDocument
import fairu.utils.mongo.collection
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.eq

@Serializable
data class UserSession(
    @SerialName("user_id")
    override val userId: Snowflake,
) : SnowflakeDocument, UserReference {
    companion object : DocumentClass<UserSession>(collection()) {
        suspend fun get(key: Snowflake): UserSession? = find(UserSession::id eq key)
    }

    /** ID of this user session. */
    override val id: Snowflake = Snowflake.generate()

    override suspend fun save() {
        save(this, UserSession::id eq id)
    }

    override suspend fun delete(): Boolean {
        return delete(UserSession::id eq id) == 1L
    }
}
