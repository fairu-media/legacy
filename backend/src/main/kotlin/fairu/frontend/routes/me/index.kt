package fairu.frontend.routes.me

import fairu.frontend.layout.rootLayout
import fairu.frontend.utils.respondHTML
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.me() = route("/@me") {
    
    get {
        call.respondHTML {
            rootLayout(call) {
                +"bruh"
            }
        }
    }
}
