package fairu.backend.utils.awt

import naibu.logging.logging
import java.awt.Font
import java.awt.Graphics
import java.awt.Point
import java.io.InputStream

object TextTools {
    private val log by logging { }

    fun loadFont(font: String): InputStream = this::class.java.classLoader.getResourceAsStream("assets/fonts/$font")
        ?: error("Unable to load font: $font")

    fun getWrappedLines(g: Graphics, text: String, x: Int, y: Int, maxWidth: Int, font: Font): List<Pair<String, Point>> {
        log.debug { "string length: ${text.length}" }
        log.debug { "string length per-line: $maxWidth" }

        g.font = font

        val width = g.fontMetrics.charsWidth(text.toCharArray(), 0, text.length)
        log.debug { "string width: $width" }

        val height = g.fontMetrics.height - 6
        log.debug { "string height: $height" }

        if (width > maxWidth) {
            var count = 0
            var countValue = 0

            val chars: CharArray = text.toCharArray()
            var charWidth: Int

            var line = 0
            val lines = mutableListOf<Pair<String, Point>>()
            for (i in chars.indices) {
                if (countValue > maxWidth) {
                    countValue = 0
                    lines.add(text.substring(count, i) to Point(x, y + height * line))
                    count = i
                    line++
                    continue
                }

                if (i == chars.size - 1) {
                    lines.add(text.substring(count, i + 1).trimStart() to Point(x, y + height * line))
                    continue
                }

                charWidth = g.fontMetrics.charWidth(chars[i])
                countValue += charWidth
            }

            return lines
        }

        return listOf(text to Point(x, y))
    }
}

