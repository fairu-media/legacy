package fairu.components.server

import fairu.utils.exception.failure
import fairu.utils.web.HTMLBuilder
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.html.FlowContent
import java.util.concurrent.ConcurrentHashMap
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

val serverComponents = ConcurrentHashMap<String, ServerComponent>()

typealias ServerComponentHandler = suspend HTMLBuilder.(ApplicationCall) -> Unit
typealias ServerComponentLoading = FlowContent.() -> Unit
typealias ServerComponentCatcher = suspend HTMLBuilder.(ApplicationCall, Throwable) -> Unit

fun Routing.serverComponents() {
    get("/~/hsc/{name}") {
        call.parameters["name"]
            ?.let(serverComponents::get)
            ?.handle(call)
            ?: failure(HttpStatusCode.BadRequest, "unknown server component")
    }
}

@OptIn(ExperimentalContracts::class)
inline fun serverComponent(
    name: String,
    register: Boolean = true,
    block: ServerComponent.Builder.() -> Unit,
): ServerComponent {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val component = ServerComponent.Builder(name)
        .apply(block)
        .build()

    if (register) {
        serverComponents[name] = component
    }

    return component
}