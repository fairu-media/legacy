package fairu.backend.routes.users

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(val username: String)

fun Route.resetPassword() = authenticate("session") {
    post("/reset-password") {

    }
}
