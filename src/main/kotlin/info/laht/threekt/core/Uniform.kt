package info.laht.threekt.core


class Uniform(
    var value: Any?,
    private val properties: MutableMap<String, Any> = mutableMapOf()
) {

    internal var needsUpdate = false

    inline fun <reified T> value(): T? = value as T

    fun getProperty(key: String): Any {
        return properties[key] ?: throw IllegalArgumentException("No such key $key in ${properties.keys}")
    }

    @Suppress("UNCHECKED_CAST")
    fun clone(): Uniform {

        val value = this.value
        return Uniform(if (value is Cloneable) value.clone() else value).also {
            properties.forEach { (key, value) ->
                it.properties.set(key, if (value is Cloneable) value.clone() else value)
            }
        }

    }

    override fun toString(): String {
        return "Uniform(value=$value, properties=$properties)"
    }

}
