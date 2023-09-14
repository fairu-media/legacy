package fairu.pages.auth

import io.ktor.server.routing.*

fun Route.auth() = route("/auth") {
    loginForm.registerEndpoints(this)

    login()

    signUp()
}