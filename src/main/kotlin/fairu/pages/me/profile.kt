package fairu.pages.me

import fairu.components.layout.rootLayout
import fairu.user.access.ps
import fairu.utils.web.respondHTML
import io.ktor.server.application.*
import io.ktor.server.routing.*
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

