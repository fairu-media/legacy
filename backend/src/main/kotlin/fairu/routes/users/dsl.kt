package fairu.routes.users

import fairu.user.User
import fairu.utils.ext.respond
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import naibu.ext.into
import naibu.serialization.DefaultFormats
import naibu.serialization.json.Json
import naibu.serialization.json.toJsonObject

val PipelineContext<Unit, ApplicationCall>.logPrefix
    get() = "User[${call.user.username}: ${call.user.id}]"

fun User.toJson() = DefaultFormats.Json.encodeToJsonElement(this)
    .into<JsonObject>()
    .filterNot { it.key == "password" }
    .toJsonObject()

@Serializable
data class GetUsersResponse(@SerialName("total_users") val totalUsers: Long)

fun Route.users() = route("/users") {
    // GET /
    get {
        val users = User.collection.countDocuments()
        respond(GetUsersResponse(users))
    }

    // PUT /
    new()

    // GET /:user_id
    user()
}
