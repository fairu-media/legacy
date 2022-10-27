package fairu.routes.users.tokens

import fairu.user.access.AccessToken
import io.ktor.server.routing.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import naibu.ext.into
import naibu.serialization.DefaultFormats
import naibu.serialization.json.Json
import naibu.serialization.json.toJsonObject

fun AccessToken.toJson() = DefaultFormats.Json.encodeToJsonElement(this)
    .into<JsonObject>()
    .filterNot { it.key == "hashed" }
    .toJsonObject()


fun Route.tokens() = route("/tokens") {
    new()

    token()
}

