package fairu.backend.routes.users

import fairu.backend.exception.failure
import fairu.backend.user.CensoringUserSerializer
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
import naibu.monads.Result
import naibu.monads.err
import naibu.monads.fold
import naibu.monads.ok
import org.litote.kmongo.eq

sealed class RegisterationFailedReason(val message: String) {
    data object NotWhitelisted : RegisterationFailedReason("Username has not been whitelisted.")

    data class ConflictingUsername(val username: String) :
        RegisterationFailedReason("Username '$username' has already been taken.")
}

suspend fun Credentials.register(): Result<User, RegisterationFailedReason> {
    val usernames = get<Config.Fairu>().management.allowedUsernames
    if (usernames != null && username !in usernames) {
        return RegisterationFailedReason.NotWhitelisted.err()
    }

    /* check if the supplied username has already been taken. */
    val conflictingUser = User.find(User::username eq username)
    if (conflictingUser != null) {
        return RegisterationFailedReason.ConflictingUsername(username).err()
    }

    /* hash password and insert into db */
    val user = User(
        username,
        Hash.create(password.toCharArray())
    )

    user.save()
    return user.ok()
}

fun Route.create() {
    post("/create") {
        call
            .receive<Credentials>()
            .register()
            .fold(
                /* respond w/ just user data, user is required to log-in for a session */
                { respond(CensoringUserSerializer, it) },
                /* uhoh */
                { failure(HttpStatusCode.BadRequest, it.message) }
            )
    }
}
