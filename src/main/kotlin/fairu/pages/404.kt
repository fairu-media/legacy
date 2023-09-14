package fairu.pages

import fairu.components.ButtonSize
import fairu.components.buttonStyles
import fairu.components.layout.rootLayout
import fairu.utils.web.hyperscript
import fairu.utils.web.respondHTML
import io.ktor.server.application.*
import io.ktor.server.request.*
import kotlinx.html.button
import kotlinx.html.p
import kotlinx.html.span

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
