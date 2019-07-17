package info.laht.threekt.core

class Uniform(
    var value: Any?
) {

    private val additionalDetails = mutableMapOf<String, Any>()

    operator fun set(key: String, value: Any) {
        additionalDetails[key] = value
    }

    operator fun get( key: String ): Any {
        return additionalDetails[key] ?: throw IllegalArgumentException("No such key $key in ${additionalDetails.keys}")
    }

    @Suppress("UNCHECKED_CAST")
    fun clone(): Uniform {

        val value = this.value

        val clone =  if (value is Cloneable) {
            Uniform(value.clone())
        } else {
            Uniform(value)
        }

        additionalDetails.forEach { (key, value) ->
            if (value is Cloneable) {
                clone[key] = value.clone()
            } else {
                clone[key] = value
            }
        }

        return clone

    }

}
