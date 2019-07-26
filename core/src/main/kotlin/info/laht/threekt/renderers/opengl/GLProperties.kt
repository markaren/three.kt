package info.laht.threekt.renderers.opengl

import java.util.*
import kotlin.collections.HashMap

internal class GLProperties {

    private val properties = WeakHashMap<Any, Properties>()

    operator fun get(`object`: Any): Properties {
        return properties.computeIfAbsent(`object`) {
            Properties()
        }
    }

    fun remove(`object`: Any) {
        properties.remove(`object`)
    }

    fun update(`object`: Any, key: String, value: Any) {
        properties[`object`]?.set(key, value) ?: throw IllegalStateException("No such key: $`object`")
    }

    fun dispose() {
        properties.clear()
    }

}

class Properties : HashMap<String, Any?>() {

    inline fun <reified T> getAs(key: String): T? {
        return get(key) as T
    }

}