package fairu.components.server

import fairu.utils.web.Component
import fairu.utils.web.htmx
import fairu.utils.web.respondHTML
import io.ktor.server.application.*
import kotlinx.html.FlowContent
import kotlinx.html.TagConsumer
import kotlinx.html.div
import kotlinx.html.span

data class ServerComponent(
    val name: String,
    val handler: ServerComponentHandler,
    val loading: ServerComponentLoading,
    val catcher: ServerComponentCatcher,
) : Component {
    suspend fun handle(call: ApplicationCall) {
        call.respondHTML {
            try {
                handler(call)
            } catch (ex: Throwable) {
                catcher(call, ex)
            }
        }
    }

    override fun render(to: TagConsumer<*>) {
        to.div {
            htmx {
                trigger = "load"
                target = "this"
                swap = "outerHTML"
                get = "/~/hsc/$name"
            }

            loading()
        }
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
