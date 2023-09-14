package fairu.pages

import fairu.components.layout.rootLayout
import fairu.components.server.serverComponent
import fairu.routes.files.GetFilesResponse
import fairu.utils.web.respondHTML
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.span

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
