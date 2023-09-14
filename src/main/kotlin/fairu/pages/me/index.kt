package fairu.pages.me

import fairu.user.access.ps
import fairu.utils.web.redirect
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.me() = route("/@me") {
    intercept(ApplicationCallPipeline.Call) {
        call.ps ?: call.redirect("/-/auth/login")
    }

    profile()

    get {
        call.redirect("/-/@me/profile")
    }
}
