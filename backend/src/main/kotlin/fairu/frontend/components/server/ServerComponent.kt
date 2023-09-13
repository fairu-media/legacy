package fairu.frontend.components.server

import fairu.frontend.utils.htmx
import fairu.frontend.utils.respondHTML
import io.ktor.server.application.*
import io.ktor.server.routing.*
import kotlinx.html.*

data class ServerComponent(
    val name: String,
    val handler: ServerComponentHandler,
    val loading: ServerComponentLoading,
    val catcher: ServerComponentCatcher,
) {
    suspend fun handle(call: ApplicationCall) {
        call.respondHTML {
            try {
                handler(call)
            } catch (ex: Throwable) {
                catcher(call, ex)
            }
        }
    }

    fun render(to: FlowContent) = render(to.consumer)

    fun render(to: TagConsumer<*>) = to.div {
        htmx {
            trigger = "load"
            target = "this"
            swap = "outerHTML"
            get = "/~/hsc/$name"
        }

        loading()
    }

    class Builder(val name: String) {
        lateinit var handler: ServerComponentHandler
        lateinit var loading: ServerComponentLoading

        var catcher: ServerComponentCatcher = { _, ex ->
            span {
                +(ex.message ?: "ran into an exception")
            }
        }

        fun handle(block: ServerComponentHandler) {
            handler = block
        }

        fun load(block: ServerComponentLoading) {
            loading = block
        }

        fun catch(block: ServerComponentCatcher) {
            catcher = block
        }

        fun build() = ServerComponent(name, handler, loading, catcher)
    }
}
