package fairu.frontend.routes.auth

import io.ktor.server.routing.*

fun Route.auth() = route("/auth") {
    login()
    
    signUp()
}