package fairu.frontend.routes.auth

import fairu.backend.routes.login
import fairu.backend.user.access.ps
import fairu.backend.utils.auth.receiveCredentials
import fairu.frontend.components.ButtonVariant
import fairu.frontend.components.buttonStyles
import fairu.frontend.components.link
import fairu.frontend.layout.rootLayout
import fairu.frontend.utils.htmx
import fairu.frontend.utils.respondHTML
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Route.login() = route("/login") {
    accountForm.registerEndpoints(this)

    post {
        val credentials = call.receiveCredentials()
        if (credentials.login(call)) {
            call.respondRedirect("/-/@me")
        } else {
            call.respondHTML {
                div(classes = "bg-red-400/30 text-center font-bold mb-6 rounded px-4 py-2.5 shadow") {
                    id = "error"
                    +"Invalid Username or Password"
                }
            }
        }
    }

    get {
        if (call.ps != null) {
            return@get call.respondRedirect("/-/@me")
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

                    accountForm.render(this)

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
}
