package fairu.routes

import fairu.utils.auth.Credentials
import fairu.utils.auth.Password
import fairu.utils.auth.Token
import fairu.mongo.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.eq

// TODO: replace this with like api tokens or something

fun Route.login() = post("/login") {
    val creds = call.receive<Credentials>()

    /* find user with supplied username in db. */
    val user = User.find(User::username eq creds.username)
        ?: return@post call.respond(HttpStatusCode.Unauthorized)

    /* verify passwords */
    val verified = Password.verify(
        user.password,
        creds.password.toCharArray()
    )

    if (!verified) {
        return@post call.respond(HttpStatusCode.Unauthorized)
    }

    call.respond(mapOf("token" to Token.create(user)))
}
