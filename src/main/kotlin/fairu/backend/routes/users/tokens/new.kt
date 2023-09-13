package fairu.backend.routes.users.tokens

import fairu.backend.routes.users.logPrefix
import fairu.backend.routes.users.user
import fairu.shared.user.access.AccessScope
import fairu.shared.user.access.AccessToken
import fairu.backend.utils.auth.Token
import fairu.backend.utils.ext.log
import fairu.backend.utils.ext.respond
import fairu.backend.utils.serialization.InstantAsLongSerializer
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.serialization.Serializable
import java.util.*

fun Route.new() = post {
    val body = call.receive<NewTokenRequest>()

    /* create a new access token */
    val token = AccessToken(body.name, body.scopes, call.user.id, body.expiration)
    token.save()

    /* create jwt */
    val jwt = Token.create(call.user) {
        withExpiresAt(Date.from(body.expiration.toJavaInstant()))

        withClaim("token_id", token.id.toString())
    }

    /* respond with JWT content */
    log.info("$logPrefix Created token '${body.name}' w/ scopes ${body.scopes.joinToString(", ")}")
    respond(mapOf("access_token" to jwt, "id" to token.id.toString()))
}

@Serializable
data class NewTokenRequest(
    val name: String,
    val scopes: List<AccessScope>,
    @Serializable(InstantAsLongSerializer::class)
    val expiration: Instant,
)
