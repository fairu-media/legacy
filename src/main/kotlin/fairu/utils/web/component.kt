package fairu.utils.web

import kotlinx.html.FlowContent
import kotlinx.html.TagConsumer

interface Component {
    /**
     *
     */
    fun render(to: FlowContent) {
        render(to.consumer)
    }

    /**
     *
     */
    fun render(to: TagConsumer<*>)

    operator fun FlowContent.invoke() = render(this)

    operator fun TagConsumer<*>.invoke() = render(this)
}
