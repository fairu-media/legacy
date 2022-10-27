package fairu.utils.mongo

import fairu.utils.Snowflake
import kotlinx.datetime.Instant

interface SnowflakeDocument : Document {
    val id: Snowflake

    /** The creation date of this Document. */
    val createdAt: Instant get() = id.timestamp
}
