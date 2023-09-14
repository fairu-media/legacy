package fairu.pages

import fairu.components.Icon
import fairu.components.navbarRoutes
import fairu.components.server.serverComponents
import fairu.pages.auth.auth
import fairu.pages.me.me
import fairu.utils.exception.failure
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*

val IsFrontend = AttributeKey<Boolean>("Fairu-IsFrontend")

val ApplicationCall.isFrontend: Boolean
    get() = (attributes.getOrNull(IsFrontend) == true) || request.path().startsWith("/-")

fun Routing.index() {
    navbarRoutes()
    serverComponents()

    // icon rendering
    get("/~/icon/{set}/{name}") {
        // get the icon set name.
        val set = call.parameters["set"]
            ?: failure(HttpStatusCode.BadRequest, "no icon set")

        // get the icon name.
        val name = call.parameters["name"]
            ?.substringBeforeLast('.')
            ?.takeUnless { it.isBlank() }
            ?: failure(HttpStatusCode.BadRequest, "no icon name")

        call.respondText(
            Icon(set, name).fetchSVG(),
            contentType = ContentType.Text.Html
        )
    }

    route("/-") {
        intercept(ApplicationCallPipeline.Setup) {
            call.attributes.put(IsFrontend, true)
        }

        staticResources("/static", "static", index = null) {
            preCompressed(CompressedFileType.GZIP)
        }

        get {
            call.respondRedirect("/-/home")
        }

        // pages
        home()

        authenticate("session", optional = true) {
            me()

            auth()
        }
    }
}
