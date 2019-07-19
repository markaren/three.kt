package info.laht.threekt.renderers.opengl

import java.util.*

internal typealias Properties = MutableMap<String, Any>

class GLProperties internal constructor() {

    private val properties = WeakHashMap<Any, Properties>()

    operator fun get(`object`: Any): Properties {

        return properties.computeIfAbsent(`object`) {
            mutableMapOf()
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
