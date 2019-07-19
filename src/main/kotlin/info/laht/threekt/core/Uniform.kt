package info.laht.threekt.core

class Uniform(
    var value: Any?,
    private val properties: MutableMap<String, Any> = mutableMapOf()
) {

    internal operator fun set(key: String, value: Any) {
        properties[key] = value
    }

    operator fun get( key: String ): Any {
        return properties[key] ?: throw IllegalArgumentException("No such key $key in ${properties.keys}")
    }

    @Suppress("UNCHECKED_CAST")
    fun clone(): Uniform {

        val value = this.value

        val clone =  if (value is Cloneable) {
            Uniform(value.clone())
        } else {
            Uniform(value)
        }

        properties.forEach { (key, value) ->
            if (value is Cloneable) {
                clone[key] = value.clone()
            } else {
                clone[key] = value
            }
        }

        return clone

    }

    override fun toString(): String {
        return "Uniform(value=$value, properties=$properties)"
    }


}
