package fairu.frontend.routes.auth

import fairu.frontend.utils.buildForm
import kotlinx.html.InputType

val accountForm = buildForm("/-/auth/login") {
    field {
        name = "username"
        type = InputType.text
        placeholder = "john_doe"

        validate {
            when {
                it.length < 3 -> if (it.isEmpty()) {
                    "a username is required"
                } else {
                    "username must be at-least 3 characters"
                }

                it.length > 20 ->
                    "username must be at-most 20 characters"

                else -> null
            }
        }
    }

    field {
        name = "password"
        type = InputType.password
        placeholder = "v3ry_s3cur3_p@33w0rd"

        validate {
            when {
                it.length < 8 -> if (it.isNotEmpty()) {
                    "Password must be at-least 8 characters"
                } else {
                    "A password is required"
                }

                else -> null
            }
        }
    }
}