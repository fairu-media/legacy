package fairu.backend.user

import kotlinx.serialization.*
import kotlinx.serialization.json.*
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
