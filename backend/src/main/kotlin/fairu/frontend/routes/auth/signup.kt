package fairu.frontend.routes.auth

import fairu.frontend.layout.rootLayout
import fairu.frontend.utils.respondHTML
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.signUp() = route("/sign-up") {
    get {
        call.respondHTML {
            rootLayout(call) {
                +"bruh"
            }
        }
    }
}
