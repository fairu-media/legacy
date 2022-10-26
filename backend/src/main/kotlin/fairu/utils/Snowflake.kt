package fairu.utils

/**
 * Written by the Kord Team, licensed under MIT
 *
 * https://github.com/kordlib/kord
 */

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = Snowflake.Serializer::class)
class Snowflake : Comparable<Snowflake> {
    val value: ULong

    constructor(value: ULong) {
        this.value = value.coerceIn(validValues)
    }

    constructor(value: String) : this(value.toULong())

    constructor(timestamp: Instant) : this(
        timestamp.toEpochMilliseconds()
            .coerceAtLeast(DISCORD_EPOCH_LONG) // time before is unknown to Snowflakes
            .minus(DISCORD_EPOCH_LONG)
            .toULong()
            .coerceAtMost(maxMillisecondsSinceDiscordEpoch) // time after is unknown to Snowflakes
            .shl(TIMESTAMP_SHIFT)
    )

    private inline val millisecondsSinceDiscordEpoch get() = value shr TIMESTAMP_SHIFT

    val timestamp: Instant
        get() = Instant.fromEpochMilliseconds(DISCORD_EPOCH_LONG + millisecondsSinceDiscordEpoch.toLong())

    /**
     * Internal ID of the worker that generated this Snowflake ID.
     *
     * Only the 5 least significant bits are used. This value is therefore always in the range `0..31`.
     */
    val workerId: UByte
        get() = value.and(WORKER_MASK).shr(WORKER_SHIFT).toUByte()

    /**
     * Internal ID of the process that generated this Snowflake ID.
     *
     * Only the 5 least significant bits are used. This value is therefore always in the range `0..31`.
     */
    val processId: UByte
        get() = value.and(PROCESS_MASK).shr(PROCESS_SHIFT).toUByte()

    /**
     * Increment. For every Snowflake ID that is generated on a [process][processId], this number is incremented.
     *
     * Only the 12 least significant bits are used. This value is therefore always in the range `0..4095`.
     */
    val increment: UShort
        get() = value.and(INCREMENT_MASK).toUShort()


    /**
     * Returns [timestamp] for use in destructuring declarations.
     *
     * ```kotlin
     * val (timestamp, workerId, processId, increment) = snowflake
     * ```
     */
    operator fun component1(): Instant = timestamp

    /**
     * Returns [workerId] for use in destructuring declarations.
     *
     * ```kotlin
     * val (timestamp, workerId, processId, increment) = snowflake
     * ```
     */
    operator fun component2(): UByte = workerId

    /**
     * Returns [processId] for use in destructuring declarations.
     *
     * ```kotlin
     * val (timestamp, workerId, processId, increment) = snowflake
     * ```
     */
    operator fun component3(): UByte = processId

    /**
     * Returns [increment] for use in destructuring declarations.
     *
     * ```kotlin
     * val (timestamp, workerId, processId, increment) = snowflake
     * ```
     */
    operator fun component4(): UShort = increment


    override fun compareTo(other: Snowflake): Int =
        millisecondsSinceDiscordEpoch.compareTo(other.millisecondsSinceDiscordEpoch)

    /**
     * A [String] representation of this Snowflake's [value].
     */
    override fun toString(): String = value.toString()

    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean = other is Snowflake && this.value == other.value


    companion object {
        // see https://discord.com/developers/docs/reference#snowflakes-snowflake-id-format-structure-left-to-right

        private const val DISCORD_EPOCH_LONG = 1420070400000L // use custom epoch

        private const val TIMESTAMP_SHIFT = 22

        private const val WORKER_MASK = 0x3E0000uL
        private const val WORKER_SHIFT = 17

        private const val PROCESS_MASK = 0x1F000uL
        private const val PROCESS_SHIFT = 12

        private const val INCREMENT_MASK = 0xFFFuL

        val validValues: ULongRange = ULong.MIN_VALUE..Long.MAX_VALUE.toULong() // 0..9223372036854775807
        val max: Snowflake = Snowflake(validValues.last)

        private val maxMillisecondsSinceDiscordEpoch = max.millisecondsSinceDiscordEpoch
    }

    internal object Serializer : KSerializer<Snowflake> {
        override val descriptor: SerialDescriptor = ULong.serializer().descriptor

        override fun deserialize(decoder: Decoder): Snowflake =
            Snowflake(decoder.decodeString().toULong())

        override fun serialize(encoder: Encoder, value: Snowflake) {
            encoder.encodeString(value.toString())
        }
    }
}
