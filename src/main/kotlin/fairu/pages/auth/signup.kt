package fairu.pages.auth

import fairu.components.buttonStyles
import fairu.components.layout.rootLayout
import fairu.routes.users.register
import fairu.user.access.ps
import fairu.utils.auth.receiveCredentials
import fairu.utils.web.htmx
import fairu.utils.web.redirect
import fairu.utils.web.respondHTML
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.html.*
import naibu.monads.isOk
import naibu.monads.unwrapErr

fun Route.signUp() = route("/sign-up") {
    post {
        val result = call
            .receiveCredentials()
            .register()

        if (result.isOk()) {
            call.redirect("/-/auth/login")
        } else {
            val reason = result.unwrapErr()
            call.respondHTML {
                div(classes = "bg-red-400/30 text-center font-bold mb-6 rounded px-4 py-2.5 shadow") {
                    id = "error"
                    +reason.message
                }
            }
        }
    }

    get {
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
                        +"Register an Account"
                    }

                    p(classes = "text-md") {
                        +"Make sure to complete the required fields."
                    }
                }

                form(classes = "flex flex-col space-y-4") {
                    htmx {
                        target = "#error"
                        post = "/-/auth/sign-up"
                        swap = "morph:outerHTML"
                    }

                    loginForm.render(this)

                    button(type = ButtonType.submit, classes = buttonStyles()) {
                        +"Register"
                    }
                }
            }
        }
    }
}
