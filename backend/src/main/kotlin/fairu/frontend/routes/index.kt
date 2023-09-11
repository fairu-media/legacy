package fairu.frontend.routes

import fairu.frontend.components.ButtonVariant
import fairu.frontend.components.buttonStyles
import fairu.frontend.components.navbarRoutes
import fairu.frontend.layout.rootLayout
import fairu.frontend.routes.auth.auth
import fairu.frontend.routes.me.me
import fairu.frontend.utils.hyperscript
import fairu.frontend.utils.respondHTML
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.util.*
import kotlinx.html.button
import kotlinx.html.p
import kotlinx.html.span

val IsFrontend = AttributeKey<Boolean>("Fairu-IsFrontend")

val ApplicationCall.isFrontend: Boolean
    get() = (attributes.getOrNull(IsFrontend) == true) || request.path().startsWith("/-")

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

        button(classes = buttonStyles(ButtonVariant.Ghost, classes = "cursor-pointer")) {
            hyperscript = "on click js window.history.back() end"
            +"Go Back"
        }
    }
}

fun Routing.index() {
    route("/-") {
        intercept(ApplicationCallPipeline.Setup) {
            call.attributes.put(IsFrontend, true)
        }

        staticResources("/static", "static", index = null) {
            preCompressed(CompressedFileType.GZIP)
        }

        navbarRoutes()

        get {
            call.respondHTML {
                rootLayout(call) {
                    // TODO: home page
                }
            }
        }

        me()

        authenticate("session", optional = true) {
            auth()
        }
    }
}
