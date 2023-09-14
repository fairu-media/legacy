package fairu.user

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer
import naibu.ext.into
import naibu.serialization.json.toJsonObject

object CensoringUserSerializer : JsonTransformingSerializer<User>(User.serializer()) {
    override fun transformSerialize(element: JsonElement): JsonElement {
        return element.into<JsonObject>()
            .filterKeys { it != "password" }
            .toJsonObject()
    }
}

typealias CensoredUser = @Serializable(CensoringUserSerializer::class) User
