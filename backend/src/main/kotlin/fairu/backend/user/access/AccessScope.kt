package fairu.backend.user.access

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = AccessScope.Serializer::class)
enum class AccessScope(val key: String) {
    /* file */
    ReadFiles ("read.files"),  // read all user uploaded files
    FileUpload("file.upload"), // upload files on behalf of the user
    FileDelete("file.delete"), // delete uploaded files

    /* user */
    ReadUser("read.user"), // read all user data

    /* other */
    ;

    companion object Serializer : KSerializer<AccessScope> {
        override val descriptor: SerialDescriptor
            get() = String.serializer().descriptor

        override fun deserialize(decoder: Decoder): AccessScope {
            val key = decoder.decodeInline(descriptor).decodeString()
            return values().find { it.key == key }!!
        }

        override fun serialize(encoder: Encoder, value: AccessScope) {
            encoder.encodeInline(descriptor).encodeString(value.key)
        }
    }
}
