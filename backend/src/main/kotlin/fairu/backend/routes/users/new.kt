package fairu.backend.routes.users

import fairu.backend.exception.failure
import fairu.backend.user.User
import fairu.backend.utils.Config
import fairu.backend.utils.auth.Credentials
import fairu.backend.utils.auth.Hash
import fairu.backend.utils.ext.respond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import naibu.ext.koin.get
import org.litote.kmongo.eq

fun Route.new() {
    val config = get<Config.Fairu>()
    post {
        val info = call.receive<Credentials>()

        if (config.allowedUsernames != null && info.username !in config.allowedUsernames) {
            failure(HttpStatusCode.Forbidden, "Disallowed username.")
        }

        /* check if the supplied username has already been taken. */
        val conflictingUser = User.find(User::username eq info.username)
        if (conflictingUser != null) {
            failure(HttpStatusCode.Conflict, "Username '${info.username}' has already been taken.")
        }

        /* hash password and insert into db */
        val user = User(
            info.username,
            Hash.create(info.password.toCharArray())
        )

        user.save()

        /* respond w/ no content, user is required to log-in for a session */
        respond(user.toJson())
    }
}
