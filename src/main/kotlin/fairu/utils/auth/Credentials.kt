package fairu.utils.auth

import fairu.utils.exception.failure
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import kotlinx.serialization.Serializable

@Serializable
data class Credentials(val username: String, val password: String)

suspend fun ApplicationCall.receiveCredentials() = when (request.contentType()) {
    ContentType.Application.FormUrlEncoded -> {
        val params = receiveParameters()
        Credentials(
            params["username"] ?: failure(HttpStatusCode.BadRequest, "invalid body"),
            params["password"] ?: failure(HttpStatusCode.BadRequest, "invalid body"),
        )
    }

    else -> receive<Credentials>()
}
