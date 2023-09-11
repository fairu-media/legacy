package fairu.frontend.components

import fairu.backend.user.UserPrincipal
import fairu.frontend.layout.containerClasses
import fairu.frontend.utils.htmx
import fairu.frontend.utils.respondHTML
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay
import kotlinx.html.*
import kotlin.time.Duration.Companion.seconds

fun Route.navbarRoutes() = authenticate("session", optional = true) {
    get("/~/navbar/session") {
        call.respondHTML {
            val principal = call.principal<UserPrincipal.Session>()
            if (principal != null) dropdown {
                trigger {
                    +principal.user.username
                }

                menu {
                    val classes = "${buttonStyles(ButtonVariant.Ghost, ButtonSize.Small)} !block"
                    link("Profile", "/-/@me/profile", classes)
                    link("Settings", "/-/@me/settings", classes)
                    link("Files", "/-/@me/files", classes)
                }
            } else {
                link("Login", "/-/auth/login")
            }
        }
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
                    link("Home", "/-")
                }
            }

            div(classes = "flex items-center space-x-1.5") {
                div {
                    htmx.indicator = "#indicator"
                    htmx.trigger = "load"
                    htmx.swap = "outerHTML"
                    htmx.get = "/-/~/navbar/session"

                    spinner(classes = "h-6 w-6") {
                        id = "indicator"
                    }
                }
            }
        }
    }
}
