package fairu.frontend.utils

import fairu.backend.exception.failure
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.html.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun buildForm(basePath: String, block: Form.Builder.() -> Unit): Form {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    return Form.Builder(basePath)
        .apply(block)
        .build()
}

data class Form(val fields: List<Field>, val basePath: String, val styles: Styles) {
    /**
     * Creates the HTMX endpoints required for this route.
     */
    fun registerEndpoints(route: Route) {
        val form = this
        for (field in fields) {
            route.post("/~/${field.name}") {
                if (!call.isHTMX) {
                    failure(HttpStatusCode.Forbidden, "invalid")
                }

                val values = call.receiveParameters()
                call.respondHTML {
                    val statefulField = field.copy(value = values[field.name])
                    statefulField.render(form, this)
                }
            }
        }
    }

    fun render(content: FlowContent) = render(content.consumer)

    fun render(consumer: TagConsumer<*>) {
        for (field in fields) field.render(this, consumer)
    }

    data class Styles(
        val field: String,
        val fieldLabel: String,
        val fieldInput: String,
        val fieldError: String,
    ) {
        class Builder {
            var field: String = "flex flex-col space-y-1.5"
            var fieldLabel: String = "font-semibold text-sm text-zinc-100/80"
            var fieldInput: String = "bg-zinc-800/50 text-sm rounded px-2.5 py-1.5"
            var fieldError: String = "text-xs text-red-200 lowercase"

            fun build() = Styles(field, fieldLabel, fieldInput, fieldError)
        }

        companion object {
            operator fun invoke() = Builder().build()
        }
    }

    data class Field(
        val name: String,
        val type: InputType,
        val placeholder: String,
        val validator: (String) -> String?,
        val value: String?
    ) {
        fun render(form: Form, t: FlowContent) = render(form, t.consumer)

        fun render(form: Form, consumer: TagConsumer<*>) {
            val field = this
            consumer.div(classes = form.styles.field) {
                htmx {
                    target = "this"
                    swap = "outerHTML"
                }

                label(classes = form.styles.fieldLabel) {
                    htmlFor = name
                    +name.capitalize()
                }

                input(type = field.type, classes = form.styles.fieldInput) {
                    htmx {
                        post = "${form.basePath}/~/${field.name}"
                    }

                    name = field.name
                    placeholder = field.placeholder

                    if (field.value != null) value = field.value
                }

                field.value?.let(field.validator)?.let {
                    span(classes = form.styles.fieldError) {
                        +it
                    }
                }
            }
        }

        class Builder {
            lateinit var name: String
            lateinit var type: InputType
            lateinit var placeholder: String

            var validator: (value: String) -> String? = { null }

            fun validate(block: (String) -> String?) {
                validator = block
            }

            fun build(): Field =
                Field(name, type, placeholder, validator, null)
        }
    }

    class Builder(val basePath: String) {
        val fields = mutableListOf<Field>()
        var styles: Styles = Styles()

        fun style(block: Styles.Builder.() -> Unit) {
            styles = Styles.Builder()
                .apply(block)
                .build()
        }

        fun field(block: Field.Builder.() -> Unit) {
            fields += Field.Builder()
                .apply(block)
                .build()
        }

        fun build() = Form(fields.toList(), basePath, styles)
    }
}