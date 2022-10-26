package fairu.routes.users

import fairu.utils.auth.Credentials
import fairu.utils.auth.Password
import fairu.utils.Config
import fairu.mongo.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import naibu.ext.koin.get
import org.litote.kmongo.eq

fun Route.new() = post {
    val info = call.receive<Credentials>()

    if (info.username !in get<Config.Fairu>().allowedUsernames) {
        return@post call.respond(HttpStatusCode.Forbidden)
    }

    /* check if the supplied username has already been taken. */
    val conflictingUser = User.find(User::username eq info.username)
    if (conflictingUser != null) {
        return@post call.respond(HttpStatusCode.Conflict)
    }

    /* hash password and insert into db */
    val user = User(
        info.username,
        Password.hash(info.password.toCharArray())
    )

    user.save()

    /* respond w/ no content, user is required to log-in for a session */
    call.respond(user.toJson())
}
