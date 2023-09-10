package fairu.backend.routes.users.tokens

import fairu.backend.user.access.AccessToken
import fairu.backend.user.access.authenticatedUser
import fairu.backend.utils.ext.respond
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.litote.kmongo.eq

fun Route.tokens() = route("/tokens") {
    authenticate("session") {
        get {
            val tokens = AccessToken.collection
                .find(AccessToken::userId eq call.authenticatedUser?.id)
                .toList()

            respond(tokens)
        }
    }

    new()

    token()
}

