package fairu.frontend.components

import fairu.frontend.layout.containerClasses
import kotlinx.html.*

fun BODY.footer() =
    footer(classes = "py-3 text-zinc-200/70 text-xs") {
        div(classes = "$containerClasses flex items-center justify-between") {
            span("hover:text-zinc-100/90 flex items-center space-x-1") {
                span { +"made w/" }
                span(classes = "text-red-300 text-lg") { icon("ri", "heart-3-fill") }
                span { +"(kotlin, hyperscript, and htmx)" }
//                +"made w/ love (kotlin, hyperscript, and htmx)"
            }

            div(classes = "flex items-center space-x-2 text-xl transition-colors") {
                a(href = "https://discord.gg/8R4d8RydT4", classes = "hover:text-zinc-100/90") {
                    title = "discord server"
                    icon("ri", "discord-fill")
                }

                a(href = "https://github.com/melike2d/fairu", classes = "hover:text-zinc-100/90") {
                    title = "github repo"
                    icon("ri", "github-fill")
                }
            }
        }
    }