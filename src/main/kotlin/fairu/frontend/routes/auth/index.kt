package fairu.frontend.routes.auth

import io.ktor.server.routing.*

fun Route.auth() = route("/auth") {
    accountForm.registerEndpoints(this)

    login()
    
    signUp()
}