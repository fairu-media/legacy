package fairu.utils.web

import kotlinx.html.*

inline fun FlowContent.path(classes: String? = null, crossinline block: PATH.() -> Unit): Unit =
    PATH(attributesMapOf("class", classes), consumer).visit(block)

var SVG.viewBox: String
    get() = stringAttribute[this, "viewBox"]
    set(value) = stringAttribute.set(this, "viewBox", value)

var SVG.fill: String
    get() = stringAttribute[this, "fill"]
    set(value) = stringAttribute.set(this, "fill", value)

var SVG.width: String
    get() = stringAttribute[this, "width"]
    set(value) = stringAttribute.set(this, "width", value)

var SVG.height: String
    get() = stringAttribute[this, "height"]
    set(value) = stringAttribute.set(this, "height", value)

@Suppress("unused")
open class PATH(
    initialAttributes: Map<String, String>,
    override val consumer: TagConsumer<*>,
) : HTMLTag("PATH", consumer, initialAttributes, null, false, false), HtmlBlockInlineTag {
    var d: String
        get() = stringAttribute[this, "d"]
        set(value) = stringAttribute.set(this, "d", value)
    var fill: String
        get() = stringAttribute[this, "fill"]
        set(value) = stringAttribute.set(this, "fill", value)
}