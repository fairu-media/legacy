package fairu.utils.serialization

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object InstantAsLongSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = Long.serializer().descriptor

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.fromEpochMilliseconds(decoder.decodeInline(descriptor).decodeLong())
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeInline(descriptor).encodeLong(value.toEpochMilliseconds())
    }
}
