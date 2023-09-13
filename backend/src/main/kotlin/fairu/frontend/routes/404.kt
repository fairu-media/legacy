package fairu.frontend.routes

import fairu.frontend.components.ButtonSize
import fairu.frontend.components.buttonStyles
import fairu.frontend.layout.rootLayout
import fairu.frontend.utils.hyperscript
import fairu.frontend.utils.respondHTML
import io.ktor.server.application.*
import io.ktor.server.request.*
import kotlinx.html.*

suspend fun ApplicationCall.notFound() = respondHTML {
    rootLayout(this@notFound) {
        span(classes = "font-mono font-bold") {
            +request
                .path()
                .removePrefix("/-")

            +" 404 NOT FOUND"
        }

        p(classes = "font-light") {
            +"Seems like you've hit an unknown page!"
        }

        button(classes = buttonStyles(size = ButtonSize.Small, classes = "mt-2")) {
            hyperscript = "on click js window.history.back() end"
            +"Go Back"
        }
    }
}
