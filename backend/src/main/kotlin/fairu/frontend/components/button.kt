package fairu.frontend.components

import kotlinx.html.FlowContent

enum class ButtonVariant(baseClasses: String) {
    Primary("bg-fuchsia-300 hover:bg-fuchsia-200 active:bg-fuchsia-300 text-zinc-800 font-bold shadow"),
    Warning("bg-amber-300 hover:bg-amber-200 active:bg-amber-300 text-zinc-800 font-bold shadow"),
    Ghost("hover:bg-zinc-700/30 active:bg-zinc-700/20");

    val classes: String = "$baseClasses transition transition-colors"
}

enum class ButtonSize(val classes: String) {
    Default("px-4 py-2"),
    Small("px-3 py-1.5 text-sm");
}

fun buttonStyles(variant: ButtonVariant = ButtonVariant.Primary, size: ButtonSize = ButtonSize.Default, classes: String = "") =
    "${variant.classes} ${size.classes} rounded inline-block $classes".trim()
