package fairu.frontend.components

import fairu.frontend.utils.htmx
import kotlinx.html.FlowContent
import kotlinx.html.TagConsumer
import kotlinx.html.div

fun FlowContent.link(
    name: String,
    href: String,
    classes: String = buttonStyles(ButtonVariant.Ghost, ButtonSize.Small)
) = consumer.link(name, href, classes)

fun TagConsumer<*>.link(
    name: String,
    href: String,
    classes: String = buttonStyles(ButtonVariant.Ghost, ButtonSize.Small)
) = div(classes = classes) {
    // TODO: maybe use hx-boost instead?
    htmx {
        pushUrl = "true"
        target = "#content"
        swap = "outerHTML"
        get = href
    }

    attributes["role"] = "link"

    +name
}
