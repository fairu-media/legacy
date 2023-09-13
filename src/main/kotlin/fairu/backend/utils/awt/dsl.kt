package fairu.backend.utils.awt

import naibu.ext.awt.applyDefaultHints
import naibu.ext.scrimage.drawables.EasyDrawable
import java.awt.Color
import java.awt.Font
import java.awt.GraphicsEnvironment

internal val Int.pxToPt: Float
    get() = (this * 0.75).toFloat()


val RUBIK_REGULAR: Font by lazy {
    Font.createFont(Font.TRUETYPE_FONT, TextTools.loadFont("Rubik-Regular.ttf"))
        .deriveFont(Font.PLAIN)
}

val RUBIK_BOLD: Font by lazy {
    Font.createFont(Font.TRUETYPE_FONT, TextTools.loadFont("Rubik-Bold.ttf"))
        .deriveFont(Font.BOLD)
}

val SOURCE_CODE_PRO_BOLD: Font by lazy {
    Font.createFont(Font.TRUETYPE_FONT, TextTools.loadFont("SourceCodePro-Bold.ttf"))
        .deriveFont(Font.BOLD)
}

fun registerFonts() {
    val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
    ge.registerFont(RUBIK_REGULAR)
    ge.registerFont(RUBIK_BOLD)
    ge.registerFont(SOURCE_CODE_PRO_BOLD)
}

fun Text(
    str: String,
    x: Int,
    y: Int,
    color: Color,
    font: Font,
    fix: Boolean = true,
): EasyDrawable = EasyDrawable {
    it.applyDefaultHints()
    it.color = color
    it.font = font
    it.drawString(str, x, if (fix) y + 2 + it.fontMetrics.ascent - it.fontMetrics.descent - it.fontMetrics.leading else y)
}
