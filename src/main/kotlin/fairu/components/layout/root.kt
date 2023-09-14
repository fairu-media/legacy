package fairu.components.layout

import fairu.components.footer
import fairu.components.navbar
import fairu.components.spinner
import fairu.utils.web.HTMLBuilder
import fairu.utils.web.htmx
import fairu.utils.web.isHTMX
import io.ktor.server.application.*
import kotlinx.html.*

val containerClasses = "container px-4 mx-auto max-w-[475px]"

inline fun HTMLBuilder.content(containerized: Boolean = false, crossinline block: DIV.() -> Unit) =
    div(classes = if (containerized) "py-6 $containerClasses" else null) {
        id = "content"
        block()
    }

inline fun HTMLBuilder.rootLayout(
    call: ApplicationCall,
    containerized: Boolean = true,
    crossinline block: DIV.() -> Unit,
) {
    if (call.isHTMX) {
        content(containerized, block)
        return
    }

    html {
        lang = "en"

        head {
            meta(name = "viewport", charset = "UTF-8", content = "width=device-width, initial-scale=1.0")
            title("Fairu")

            // HTMX, Hyperscript, and Idiomorph
            script(src = "https://unpkg.com/htmx.org@1.9.3") {}
            script(src = "https://unpkg.com/hyperscript.org@0.9.11") {}
            script(src = "https://unpkg.com/idiomorph/dist/idiomorph-ext.min.js") {}

            // TailwindCSS
            link(href = "/-/static/styles.css", rel = "stylesheet")

            // Inter
            link(rel = "preconnect", href = "https://fonts.googleapis.com")
            link(rel = "preconnect", href = "https://fonts.gstatic.com") {
                attributes["crossorigin"] = "true"
            }
            link(
                href = "https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700&display=swap",
                rel = "stylesheet"
            )
        }

        body(classes = "bg-zinc-900 text-zinc-100 h-screen") {
            htmx.ext = "morph"

            navbar()
            content(containerized, block)
            footer()

            div(classes = "absolute left-5 bottom-5 htmx-indicator") {
                id = "page-loading"
                spinner(classes = "h-4 w-4")
            }
        }
    }
}
