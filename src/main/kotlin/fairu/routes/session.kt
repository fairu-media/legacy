package fairu.routes

import fairu.utils.exception.failure
import fairu.user.CensoredUser
import fairu.user.Session
import fairu.user.User
import fairu.user.UserPrincipal
import fairu.user.session.UserSession
import fairu.utils.auth.Credentials
import fairu.utils.auth.Hash
import fairu.utils.ext.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.*
import org.litote.kmongo.eq

@Serializable
data class GetSessionResponse(
    @SerialName("logged_in")
    val loggedIn: Boolean,
    val user: CensoredUser?
)

suspend fun Credentials.login(call: ApplicationCall): Boolean {
    /* find user with supplied username in db. */
    val user = User.find(User::username eq username)
        ?: return false

    /* verify passwords */
    val verified = Hash.verify(
        user.password,
        password.toCharArray()
    )

    if (!verified) {
        return false
    }

    /* create a new user session and save it to the database. */
    val userSession = UserSession(user.id)
    userSession.save()

    /* finish up */
    call.sessions.set(Session(userSession.id))
    return true
}

fun Route.session() = route("/session") {
    authenticate("session", optional = true) {
        get {
            val principal = call.principal<UserPrincipal.Session>()
            respond(GetSessionResponse(principal != null, principal?.user))
        }
    }

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
        if (creds.login(call)) {
            respond(mapOf("message" to "Welcome back ${creds.username}!"))
        } else {
            failure(HttpStatusCode.Unauthorized, "Invalid username or password.")
        }
    }
}
