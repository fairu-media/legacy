package fairu.routes.users

import fairu.utils.exception.failure
import fairu.file.File
import fairu.routes.users.tokens.tokens
import fairu.user.CensoringUserSerializer
import fairu.user.User
import fairu.user.UserPrincipal
import fairu.user.access.AccessScope
import fairu.user.access.scopedAccess
import fairu.utils.Snowflake
import fairu.utils.ext.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.util.*
import org.litote.kmongo.eq

private val UserKey = AttributeKey<User>("User")

fun Route.user() = route("/{user_id}") {
    intercept(ApplicationCallPipeline.Call) {
        val id = call.parameters["user_id"]
            ?: failure(HttpStatusCode.BadRequest, "Invalid or missing 'user_id' parameter.")

        val user = if (id == "@me") {
            // check if logged in
            val principal = call.principal<UserPrincipal>()
                ?.takeIf { it.canAccess(AccessScope.ReadUser) }
                ?: failure(HttpStatusCode.Forbidden, "Missing access.")

            principal.user
        } else {
            User.find(User::id eq Snowflake(id))
                ?: failure(HttpStatusCode.NotFound, "User w/ ID '$id' does not exist.")
        }

        call.attributes.put(UserKey, user)
    }

    scopedAccess(AccessScope.ReadUser, optional = true) {
        get {
            // TODO: omit data depending on access token scopes
            respond(CensoringUserSerializer, call.user)
        }
    }

    scopedAccess(AccessScope.ReadFiles) {
        get("/files") {
            // setup pagination
            val files = File.collection
                .find(File::userId eq call.user.id)
                .toList()

            respond(files)
        }
    }

    authenticate("session") {
        // TODO: update user
        tokens()
    }
}

val ApplicationCall.user: User get() = attributes[UserKey]
