package fairu.frontend.components

import fairu.frontend.utils.hyperscript
import kotlinx.html.FlowContent
import kotlinx.html.button
import kotlinx.html.div

class DropdownBuilder {
    var trigger: FlowContent.() -> Unit = {}

    var menu: FlowContent.() -> Unit = {}

    fun trigger(block: FlowContent.() -> Unit) {
        trigger = block
    }

    fun menu(block: FlowContent.() -> Unit) {
        menu = block
    }
}

fun FlowContent.dropdown(block: DropdownBuilder.() -> Unit) {
    val dropdown = DropdownBuilder()
        .apply(block)

    // following dropdown code is from https://codepen.io/jreviews/pen/BaQMEOy by @alejandros7340 on Discord
    div(classes = "group relative") {
        hyperscript = """
        on click queue first
            if I do not match @data-active 
                set @data-active to true 
                send open to <div/> in me
            else remove @data-active 
                send close to <div/> in me 
            end
        end
        on keyup[key is 'Escape'] from <body/> or click elsewhere
            if I match @data-active remove @data-active 
                send close to <div/> in me 
            end
        end
        """.trimIndent()

        button(classes = buttonStyles(ButtonVariant.Ghost, ButtonSize.Small, "group-data-[active]:bg-zinc-700/30")) {
            dropdown.trigger.invoke(this)
        }

        div(classes = "absolute bg-zinc-800 p-1.5 border border-zinc-600 invisible rounded text-sm w-36 mt-0.5 origin-top-right right-0") {
            hyperscript = """|on open remove .invisible end
                             |on close add .invisible end""".trimMargin("|")

            dropdown.menu.invoke(this)
        }
    }
}
