package fairu.frontend.routes.me

import fairu.shared.user.access.ps
import io.ktor.server.routing.*
import fairu.frontend.layout.rootLayout
import fairu.frontend.utils.respondHTML
import io.ktor.server.application.*
import kotlinx.html.h1

fun Route.profile() = get("/profile") {
    call.respondHTML {
        rootLayout(call) {
            h1(classes = "text-xl font-bold") {
                +"Welcome back, ${call.ps!!.user.username}!"
            }
        }
    }
}

