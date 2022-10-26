package fairu.routes.users

import fairu.mongo.File
import fairu.mongo.User
import fairu.utils.Snowflake
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import naibu.ext.into
import naibu.serialization.DefaultFormats
import naibu.serialization.json.Json
import naibu.serialization.json.toJsonObject
import org.litote.kmongo.eq

val UserKey = AttributeKey<User>("User")

fun User.toJson() = DefaultFormats.Json.encodeToJsonElement(this)
    .into<JsonObject>()
    .filterNot { it.key == "password" }
    .toJsonObject()

fun Route.users() = route("/users") {
    // GET /
    get {
        call.respond(mapOf("message" to "Welcome to /users"))
    }

    // PUT /
    new()

    // GET /:user_id
    user()
}
