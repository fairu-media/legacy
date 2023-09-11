package fairu.frontend.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.html.CommonAttributeGroupFacade
import kotlinx.html.attributes.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

val CommonAttributeGroupFacade.htmx: HTMX
    get() = HTMX(this)

inline fun CommonAttributeGroupFacade.htmx(crossinline block: HTMX.() -> Unit) {
    htmx.block()
}

suspend fun ApplicationCall.redirect(to: String) {
    if (isHTMX) {
        response.header("HX-Redirect", to)
        respond(HttpStatusCode.NoContent)
    } else {
        respondRedirect(to)
    }
}

val ApplicationCall.isHTMX: Boolean get() = request.headers["HX-Request"] == "true"

class HTMX(private val facade: CommonAttributeGroupFacade) {
    /**
     * The [**hx-boost**](https://htmx.org/attributes/hx-boost) attribute allows you to \"boost\" normal anchors and
     * form tags to use AJAX instead. This has the [nice fallback](https://en.wikipedia.org/wiki/Progressive_enhancement)
     * that, if the user does not have javascript enabled, the site will continue to work.
     */
    var boost: String by string()

    /**
     * The [**hx-confirm**](https://htmx.org/attributes/hx-confirm) attribute allows you to confirm an action before
     * issuing a request. This can be useful in cases where the action is destructive and you want to ensure that the
     * user really wants to do it.
     */
    var confirm: String by string()

    /**
     * The [**hx-delete**](https://htmx.org/attributes/hx-confirm) attribute will cause an element to issue a **DELETE**
     * to the specified URL and swap the HTML into the DOM using a swap strategy.
     */
    var delete: String by string()

    /**
     * The [**hx-disable**](https://htmx.org/attributes/hx-disable) attribute disables htmx processing for the given node
     * and any children nodes.
     */
    var disable: String by string()

    /**
     * The [**hx-encoding**](https://htmx.org/attributes/hx-encoding) attribute changes the request encoding type.
     */
    var encoding: String by string()

    /**
     * The [**hx-ext**](https://htmx.org/attributes/hx-ext) attribute enables extensions for an element
     */
    var ext: String by string()

    /**
     * The [**hx-get**](https://htmx.org/attributes/hx-get) attribute issues a `GET` to the specified URL.
     */
    var get: String by string()

    /**
     * The [**hx-headers**](https://htmx.org/attributes/hx-headers) attribute adds to the headers that will be submitted
     * with the request.
     */
    var headers: String by string()

    /**
     * The [**hx-history-elt**](https://htmx.org/attributes/hx-history-elt) attribute specifies the element to snapshot and
     * restore during history navigation.
     */
    var historyElt: String by string("history-elt")

    /**
     * The [**hx-include**](https://htmx.org/attributes/hx-include) attribute specifies additional values/inputs to include
     * in AJAX requests.
     */
    var include: String by string()

    /**
     * The [**hx-indicator**](https://htmx.org/attributes/hx-indicator) attribute specifies the element to put the `htmx-request`
     * class on during the AJAX request, displaying it as a request indicator.
     */
    var indicator: String by string()

    /**
     * The [**hx-disinherit**](https://htmx.org/attributes/hx-disinherit) attribute allows you to control and disable automatic
     * attribute inheritance for child nodes.
     */
    var disinherit: String by string()

    /**
     * The [**hx-params**](https://htmx.org/attributes/hx-params) attribute allows you filter the parameters that will be submitted
     * with a request.
     */
    var params: String by string()

    /**
     * The [**hx-patch**](https://htmx.org/attributes/hx-patch) attribute issues a `PATCH` to the specified URL.
     */
    var patch: String by string()

    /**
     * The [**hx-post**](https://htmx.org/attributes/hx-post) attribute issues a `POST` to the specified URL.
     */
    var post: String by string()

    /**
     * The [**hx-preserve**](https://htmx.org/attributes/hx-preserve) attribute preserves an element between requests (requires
     * the `id` be stable).
     */
    var preserve: String by string()

    /**
     * The [**hx-patch**](https://htmx.org/attributes/hx-patch) attribute issues a `PATCH` to the specified URL.
     */
    var prompt: String by string()

    /**
     * The [**hx-push-url**](https://htmx.org/attributes/hx-push-url) attribute pushes the URL
     * into the location bar, creating a new history entry.
     */
    var pushUrl: String by string("push-url")

    /**
     * The [**hx-put**](https://htmx.org/attributes/hx-request) attribute issues a `PUT`
     * to the specified URL.
     */
    var put: String by string()

    /**
     * The [**hx-request**](https://htmx.org/attributes/hx-request) attribute configures
     * various aspects of the request.
     */
    var request: String by string()

    /**
     * The [**hx-select**](https://htmx.org/attributes/hx-select) attribute selects a subset
     * of the server response to process.
     */
    var select: String by string()

    /**
     * The [**hx-sse**](https://htmx.org/attributes/hx-sse) attribute connects the DOM to
     * a SSE source.
     */
    var sse: String by string()

    /**
     * The [**hx-swap-oob**](https://htmx.org/attributes/hx-swap-oob) attribute marks content
     * in a response as being \"Out of Band\", i.e. swapped somewhere other than the target.
     */
    var swapOutOfBand: String by string("swap-oob")

    /**
     * The [**hx-swap**](https://htmx.org/attributes/hx-swap) attribute controls how the response
     * content is swapped into the DOM (e.g. 'outerHTML' or 'beforeend').
     */
    var swap: String by string()

    /**
     * The [**hx-sync**](https://htmx.org/attributes/hx-sync) attribute controls requests made by
     * different elements are synchronized with one another.
     */
    var sync: String by string()

    /**
     * The [**hx-target**](https://htmx.org/attributes/hx-target) attribute specifies the target
     * element to be swapped.
     */
    var target: String by string()

    /**
     * The [**hx-trigger**](https://htmx.org/attributes/hx-trigger) attribute specifies specifies
     * the event that triggers the request.
     */
    var trigger: String by string()

    /**
     * The [**hx-vals**](https://htmx.org/attributes/hx-vals) attribute specifies values to add to
     * the parameters that will be submitted with the request in JSON form.
     */
    var vals: String by string()

    /**
     * The [**hx-vars**]([Docs](https://htmx.org/attributes/hx-vars)) attribute specifies computed
     * values to add to the parameters that will be submitted with the request.
     */
    var vars: String by string()

    /**
     * The [**hx-ws**](https://htmx.org/attributes/hx-ws) attribute connects the DOM to a Web Socket source.
     */
    var ws: String by string()

    companion object {
        private fun <T> attr(name: String? = null, attribute: Attribute<T>): ReadWriteProperty<HTMX, T> =
            object : ReadWriteProperty<HTMX, T> {
                override fun getValue(thisRef: HTMX, property: KProperty<*>): T {
                    return attribute[thisRef.facade, "hx-${name ?: property.name}"]
                }

                override fun setValue(thisRef: HTMX, property: KProperty<*>, value: T) {
                    attribute.set(thisRef.facade, "hx-${name ?: property.name}", value)
                }

            }

        private fun string(name: String? = null): ReadWriteProperty<HTMX, String> = attr(name, stringAttribute)
    }
}
