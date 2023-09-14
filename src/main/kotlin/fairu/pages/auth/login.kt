package fairu.pages.auth

import fairu.components.ButtonVariant
import fairu.components.buttonStyles
import fairu.components.layout.rootLayout
import fairu.routes.login
import fairu.user.access.ps
import fairu.utils.auth.receiveCredentials
import fairu.utils.web.buildForm
import fairu.utils.web.htmx
import fairu.utils.web.redirect
import fairu.utils.web.respondHTML
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.html.*

val loginForm = buildForm("auth@login") {
    handle { call ->
        val credentials = call.receiveCredentials()
        if (credentials.login(call)) {
            call.redirect("/-/@me")
        } else {
            call.respondHTML {
                div(classes = "bg-red-400/30 text-center font-bold mb-6 rounded px-4 py-2.5 shadow") {
                    id = "error"
                    +"Invalid Username or Password"
                }
            }
        }
    }

    accountFields()
}

fun Route.login() = get("/login") {
    if (call.ps != null) {
        return@get call.redirect("/-/@me")
    }

    call.respondHTML {
        rootLayout(call) {
            div {
                id = "error"
            }

            div(classes = "flex flex-col pb-4 mb-4 border-b-zinc-700 border-b") {
                span(classes = "font-bold text-xl") {
                    +"Login"
                }

                p(classes = "text-md") {
                    +"Make sure to complete the required fields."
                }
            }

            form(classes = "flex flex-col space-y-4") {
                htmx {
                    target = "#error"
                    post = "/-/auth/login"
                    swap = "morph:outerHTML"
                }

                loginForm.render(this)

                div(classes = "flex justify-between") {
                    button(type = ButtonType.submit, classes = buttonStyles()) {
                        +"Login"
                    }

                    link("Forgot Password", "/-/auth/forgot-password", buttonStyles(ButtonVariant.Warning))

                    //                        link("Register", "/-/auth/sign-up", buttonStyles(ButtonVariant.Ghost))
                }
            }
        }
    }
}
