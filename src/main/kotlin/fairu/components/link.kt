package fairu.components

import fairu.utils.web.htmx
import kotlinx.html.*

fun FlowContent.link(
    name: String,
    href: String,
    icon: Icon? = null,
    classes: String = buttonStyles(ButtonVariant.Ghost, ButtonSize.Small),
) = consumer.link(name, href, icon, classes)

fun TagConsumer<*>.link(
    name: String,
    href: String,
    icon: Icon? = null,
    classes: String = buttonStyles(ButtonVariant.Ghost, ButtonSize.Small),
) = div(classes = classes) {

    // TODO: maybe use hx-boost instead?
    htmx {
        disinherit = "*"
        indicator = "#page-loading"
        pushUrl = "true"
        target = "#content"
        swap = "outerHTML"
        get = "/-$href"
    }

    attributes["role"] = "link"

    if (icon != null) {
        this.classes += "flex items-center space-x-1 t"
        icon.render(this)
        span { +name }
    } else {
        +name
    }
}
