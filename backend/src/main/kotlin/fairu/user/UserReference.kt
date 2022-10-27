package fairu.user

import fairu.utils.Snowflake
import org.litote.kmongo.eq

interface UserReference {
    val userId: Snowflake

    suspend fun user(): User? = User.find(User::id eq  userId)
}
