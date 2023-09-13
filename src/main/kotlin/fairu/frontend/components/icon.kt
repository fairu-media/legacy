package fairu.frontend.components

import io.ktor.client.*
import kotlinx.html.*
import fairu.frontend.utils.*
import naibu.ext.koin.get
import naibu.ext.ktor.client.request.requestContent
import java.util.concurrent.ConcurrentHashMap

val iconCache = ConcurrentHashMap<Icon, String>()

data class Icon(val set: String, val name: String) {
    val url: String get() = "https://api.iconify.design/$set/$name.svg"

    val svg: String? get() = iconCache[this]
    
    suspend fun fetchSVG(): String = iconCache.bruh(this) {
        get<HttpClient>().requestContent<String>(url)
    }
    
    fun render(to: FlowContent) {
        svg?.let {
            to.consumer.onTagContentUnsafe { +it }
        } ?: to.div {
            htmx {
                trigger = "load"    
                target = "this"
                swap = "outerHTML"
                get = "/~/icon/${set}/$name.svg"
            }
        }
    }
    
    companion object {
        inline fun <K, V> MutableMap<K, V>.bruh(key: K, factory: (K) -> V): V {
            val v = get(key)
            if (v == null) {
                val newValue = factory(key)
                if (newValue != null) {
                    put(key, newValue)
                    return newValue
                }
            }

            return v!!
        }

    }
}

fun FlowContent.icon(set: String, name: String) {
    Icon(set, name).render(this)
}
