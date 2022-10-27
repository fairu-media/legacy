package fairu.routes.users.tokens

import fairu.exception.failure
import fairu.user.access.AccessToken
import fairu.utils.Snowflake
import fairu.utils.ext.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.util.*
import org.litote.kmongo.eq


private val AccessTokenKey = AttributeKey<AccessToken>("AccessToken")

fun Route.token() = route("/{token_id}") {
    intercept(ApplicationCallPipeline.Call) {
        val id = call.parameters["token_id"]?.toULongOrNull()
            ?.let { Snowflake(it) }
            ?: failure(HttpStatusCode.BadRequest, "Invalid or missing 'token_id' parameter.")

        /* fetch token from the database */
        val token = AccessToken.find(AccessToken::id eq id)
            ?: failure(HttpStatusCode.NotFound, "Token w/ ID '$id' could not be found.")

        call.attributes.put(AccessTokenKey, token)
    }

    get {
        respond(call.token.toJson())
    }
}

val ApplicationCall.token: AccessToken get() = attributes[AccessTokenKey]
