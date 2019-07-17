package info.laht.threekt.renderers.opengl

import java.util.*

class GLProperties {

    private val properties = WeakHashMap<Any, MutableMap<Any, Any>>()

    operator fun get(`object`: Any): Any {
        return properties.computeIfAbsent(`object`) {
            mutableMapOf()
        }
    }

    fun remove(`object`: Any) {
        properties.remove(`object`)
    }

    fun update(`object`: Any, key: Any, value: Any) {
        properties[`object`]?.set(key, value) ?: throw IllegalStateException("No such key: $`object`")
    }

    fun dispose() {
        properties.clear()
    }

}
