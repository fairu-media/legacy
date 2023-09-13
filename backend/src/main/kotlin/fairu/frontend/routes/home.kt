package fairu.frontend.routes

import fairu.backend.routes.files.GetFilesResponse
import fairu.frontend.components.server.serverComponent
import fairu.frontend.components.spinner
import fairu.frontend.layout.rootLayout
import fairu.frontend.utils.respondHTML
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.html.*

val statisticsComponent = serverComponent("statistics") {
    handle {
        val (files, hits) = GetFilesResponse.fetch()
        div(classes = "flex flex-col") {
            span {
                +"$files total file(s)"
            }

            span {
                +"$hits total hit(s)"
            }
        }
    }
    
    load {
        div(classes = "flex flex-col") {
            span {
                +"~ total file(s)"
            }

            span {
                +"~ total hit(s)"
            }
        }
    }
}

fun Route.home() = get("/home") {
    call.respondHTML {
        rootLayout(call) {
            h1(classes = "font-bold text-xl") {
                +"Fairu, a painless image-hosting solution"
            }
            
            statisticsComponent.render(this)
        }
    }
}
