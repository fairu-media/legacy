package fairu.frontend.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.html.TagConsumer
import kotlinx.html.stream.appendHTML
import java.io.Writer

typealias HTMLBuilder = TagConsumer<Writer>

suspend inline fun ApplicationCall.respondHTML(
    status: HttpStatusCode? = null,
    crossinline block: suspend HTMLBuilder.() -> Unit
) =
    respondTextWriter(
        contentType = ContentType.Text.Html,
        status = status
    ) {
        appendHTML().block()
    }
