package fairu.frontend.components

import fairu.shared.user.access.ps
import fairu.frontend.layout.containerClasses
import fairu.frontend.utils.htmx
import fairu.frontend.components.server.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.html.*

val navbarSession = serverComponent("navbar-session", register = false) {
    handle { call ->
        val principal = call.ps
        if (principal != null) dropdown {
            trigger {
                +principal.user.username
            }

            menu {
                link("Profile",  "/@me/profile",  Icon("ri", "user-3-fill"))
                link("Files",    "/@me/files",    Icon("ri", "file-3-fill"))
                link("Settings", "/@me/settings", Icon("ri", "settings-3-fill"))
            }
        } else {
            link("Login", "/auth/login")
        }
    }

    load {
        htmx.indicator = "#indicator"
        spinner(classes = "h-6 w-6") {
            id = "indicator"
        }
    }
}

fun Route.navbarRoutes() = authenticate("session", optional = true) {
    get("~/hsc/navbar-session") {
        navbarSession.handle(call)
    }
}

fun BODY.navbar() {
    nav(classes = "left-0 top-0 py-3 bg-zinc-800") {
        div(classes = "$containerClasses flex items-center justify-between") {
            div(classes = "flex items-center space-x-4") {
                h1(classes = "flex items-center space-x-1.5") {
                    span(classes = "font-bold text-md") {
                        +"Fairu"
                    }

                    span(classes = "py-0.5 px-2.5 bg-fuchsia-100 text-fuchsia-500 font-light text-xs rounded-full outline-1 outline-fuchsia-500") {
                        +"Alpha"
                    }
                }

                div(classes = "flex items-center justify-between space-x-1.5") {
                    link("Home", "/home")
                }
            }

            navbarSession.render(this)
        }
    }
}
