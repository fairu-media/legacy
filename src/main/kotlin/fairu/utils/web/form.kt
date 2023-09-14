package fairu.utils.web

import fairu.utils.exception.failure
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.html.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

val forms = ConcurrentHashMap<String, Form>()

@OptIn(ExperimentalContracts::class)
inline fun buildForm(name: String, register: Boolean = true, block: Form.Builder.() -> Unit): Form {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val form = Form.Builder(name)
        .apply(block)
        .build()

    if (register) {
        forms[name] = form
    }

    return form
}

fun Routing.formRoutes() = route("/~/forms/{form_name}") {
    /* form submission */
    post {
        if (!call.isHTMX) failure(HttpStatusCode.Forbidden, "what")

        val form = call.parameters["form_name"]
            ?.let(forms::get)
            ?: failure(HttpStatusCode.NotFound, "unknown form")

        form.submit(call)
    }

    /* field validator */
    post("/{field_name}") {
        if (!call.isHTMX) failure(HttpStatusCode.Forbidden, "what")

        val form = call.parameters["form_name"]
            ?.let(forms::get)
            ?: failure(HttpStatusCode.NotFound, "unknown form")

        val field = call.parameters["field_name"]
            ?.let(form.fields::get)
            ?: failure(HttpStatusCode.NotFound, "unknown form field")

        val values = call.receiveParameters()
        call.respondHTML {
            val statefulField = field.copy(value = values[field.name])
            statefulField.render(form, this)
        }
    }
}

data class Form(
    val fields: Map<String, Field>,
    val submit: suspend (ApplicationCall) -> Unit,
    val name: String,
    val styles: Styles,
) : Component {
    /**
     * Creates the HTMX endpoints required for this route.
     */
    fun registerEndpoints(route: Route) {
        val form = this
        route.post("/~/form") {
            if (!call.isHTMX) failure(HttpStatusCode.Forbidden, "what")
            submit(call)
        }

        for ((name, field) in fields) {
            route.post("/~/form/$name") {
                if (!call.isHTMX) {
                    failure(HttpStatusCode.Forbidden, "invalid")
                }

                val values = call.receiveParameters()
                call.respondHTML {
                    val statefulField = field.copy(value = values[name])
                    statefulField.render(form, this)
                }
            }
        }
    }

    override fun render(consumer: TagConsumer<*>) {
        for (field in fields.values) field.render(this, consumer)
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
        val value: String?,
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
                    +name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                }

                input(type = field.type, classes = form.styles.fieldInput) {
                    htmx.post = "/~/forms/${form.name}/${field.name}"

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

        class Builder(val name: String, val type: InputType) {
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
        lateinit var submit: suspend (ApplicationCall) -> Unit
        val fields = hashMapOf<String, Field>()
        var styles: Styles = Styles()

        fun handle(block: suspend (ApplicationCall) -> Unit) {
            submit = block
        }

        fun style(block: Styles.Builder.() -> Unit) {
            styles = Styles.Builder()
                .apply(block)
                .build()
        }

        fun field(name: String, type: InputType, block: Field.Builder.() -> Unit) {
            fields[name] = Field.Builder(name, type)
                .apply(block)
                .build()
        }

        fun build() = Form(fields, submit, basePath, styles)
    }
}