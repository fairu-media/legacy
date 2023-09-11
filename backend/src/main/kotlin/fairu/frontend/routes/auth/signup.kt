package fairu.frontend.routes.auth

import fairu.backend.routes.users.register
import fairu.backend.user.access.ps
import fairu.backend.utils.auth.receiveCredentials
import fairu.frontend.components.buttonStyles
import fairu.frontend.layout.rootLayout
import fairu.frontend.utils.htmx
import fairu.frontend.utils.redirect
import fairu.frontend.utils.respondHTML
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import kotlinx.html.*
import naibu.ext.print
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

                    accountForm.render(this)

                    button(type = ButtonType.submit, classes = buttonStyles()) {
                        +"Register"
                    }
                }
            }
        }
    }
}
