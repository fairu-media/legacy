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
import org.litote.kmongo.eq

fun Route.user() = route("/{user_id}") {
    intercept(ApplicationCallPipeline.Call) {
        var id = call.parameters["user_id"]
            ?: return@intercept call.respond(HttpStatusCode.BadRequest)

        if (id == "@me") {
            // check if logged in
            val principal = call.principal<JWTPrincipal>()
                ?: return@intercept call.respond(HttpStatusCode.FailedDependency)

            id = principal.payload.subject
        }

        val user = User.find(User::id eq Snowflake(id))
            ?: return@intercept call.respond(HttpStatusCode.NotFound)

        call.attributes.put(UserKey, user)
    }

    authenticate(optional = true) {
        get {
            call.respond(call.user.toJson())
        }
    }

    authenticate {
        get("/files") {
            // setup pagination
            val files = File.collection
                .find(File::userId eq call.user.id)
                .toList()

            call.respond(files)
        }

        // TODO: update user
    }
}

val ApplicationCall.user: User get() = attributes[UserKey]
