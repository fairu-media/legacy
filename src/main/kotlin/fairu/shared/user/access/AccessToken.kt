package fairu.shared.user.access

import fairu.shared.user.UserReference
import fairu.backend.utils.Snowflake
import fairu.backend.utils.mongo.DocumentClass
import fairu.backend.utils.mongo.SnowflakeDocument
import fairu.backend.utils.mongo.collection
import fairu.backend.utils.serialization.InstantAsLongSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import naibu.time.now
import org.litote.kmongo.eq

@Serializable
data class AccessToken(
    /** The user-identifiable name for this token */
    val name:   String,
    /** */
    val scopes: List<AccessScope>,
    /** ID of the user who this token belongs to. */
    @SerialName("user_id")
    override val userId: Snowflake,
    /** Date of expiration */
    @SerialName("expires_at")
    @Serializable(InstantAsLongSerializer::class)
    val expiresAt: Instant,
) : SnowflakeDocument, UserReference {
    companion object : DocumentClass<AccessToken>(collection())

    /** ID of this access token */
    override val id: Snowflake = Snowflake.generate()

    /** Date of last update */
    @SerialName("last_updated_at")
    @Serializable(with = InstantAsLongSerializer::class)
    var lastUpdatedAt: Instant? = null

    /** Whether this access token has expired. */
    val isExpired: Boolean
        get() = now() >= expiresAt

    override suspend fun save() {
        lastUpdatedAt = now()
        save(this, AccessToken::id eq id)
    }

    override suspend fun delete(): Boolean {
        return delete(AccessToken::id eq id) == 1L
    }
}
