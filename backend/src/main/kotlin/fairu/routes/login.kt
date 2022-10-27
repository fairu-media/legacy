package fairu.routes

import fairu.exception.failure
import fairu.user.Session
import fairu.user.User
import fairu.user.session.UserSession
import fairu.utils.auth.Credentials
import fairu.utils.auth.Hash
import fairu.utils.ext.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.litote.kmongo.eq

// TODO: replace this with like api tokens or something

fun Route.login() = post("/login") {
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
