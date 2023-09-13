package fairu.backend.utils.s3

import aws.sdk.kotlin.services.s3.model.BucketLocationConstraint
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias Region = @Serializable(RegionSerializer::class) BucketLocationConstraint

object RegionSerializer : KSerializer<BucketLocationConstraint> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("s3.Region", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Region =
        Region.fromValue(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: BucketLocationConstraint) {
        encoder.encodeString(value.value)
    }
}
