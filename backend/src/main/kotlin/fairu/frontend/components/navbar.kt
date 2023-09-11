package fairu.frontend.components

import fairu.backend.user.UserPrincipal
import fairu.frontend.layout.containerClasses
import io.ktor.server.application.*
import io.ktor.server.auth.*
import kotlinx.html.*

fun BODY.navbar(call: ApplicationCall) {
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
                    val principal = call.principal<UserPrincipal.Session>()
                    if (principal != null) {
                        +principal.user.username
                    } else {
                        link("Login", "/-/auth/login")
                    }
                }
            }
        }
    }
}
