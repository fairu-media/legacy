package fairu.backend.user

import fairu.backend.utils.Snowflake
import kotlinx.serialization.Serializable

@Serializable
data class Session(
    @Serializable(with = Snowflake.LongSerializer::class)
    val id: Snowflake,
)
