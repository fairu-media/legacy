package fairu.user

import fairu.utils.Snowflake
import kotlinx.serialization.Serializable

@Serializable
data class Session(
    @Serializable(with = Snowflake.LongSerializer::class)
    val id: Snowflake,
)
