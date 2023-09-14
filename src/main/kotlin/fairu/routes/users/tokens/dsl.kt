package fairu.routes.users.tokens

import fairu.utils.ext.respond
import fairu.user.access.AccessToken
import fairu.user.access.authenticatedUser
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

