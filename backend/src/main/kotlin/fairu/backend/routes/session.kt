package fairu.backend.routes

import fairu.backend.exception.failure
import fairu.backend.user.Session
import fairu.backend.user.User
import fairu.backend.user.UserPrincipal
import fairu.backend.user.session.UserSession
import fairu.backend.utils.auth.Credentials
import fairu.backend.utils.auth.Hash
import fairu.backend.utils.ext.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.litote.kmongo.eq

fun Route.session() = route("/session") {
    authenticate("session") {
        delete {
            val principal = call.principal<UserPrincipal.Session>()
                ?: failure(HttpStatusCode.NotFound, "No session is currently present.")

            /* delete session */
            principal.session.delete()

            /* finish up */
            call.sessions.clear<Session>()
            respond(mapOf("message" to "Sorry to see you go ${principal.user.username} :/"))
        }
    }

    post {
        val principal = call.principal<UserPrincipal.Session>()
        if (principal != null) {
            failure(HttpStatusCode.NotAcceptable, "A session is already present.")
        }

        val creds = call.receive<Credentials>()

        /* find user with supplied username in db. */
        val user = User.find(User::username eq creds.username)
            ?: failure(HttpStatusCode.Unauthorized, "Invalid username or password.")

        /* verify passwords */
        val verified = Hash.verify(
            user.password,
            creds.password.toCharArray()
        )

        if (!verified) {
            failure(HttpStatusCode.Unauthorized, "Invalid username or password.")
        }

        /* create new user session and save it to the database. */
        val userSession = UserSession(user.id)
        userSession.save()

        /* finish up */
        call.sessions.set(Session(userSession.id))
        respond(mapOf("message" to "Welcome back ${user.username}!"))
    }
}
