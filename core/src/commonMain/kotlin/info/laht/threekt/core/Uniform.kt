package info.laht.threekt.core

class Uniform(
        var value: Any?
) {

    internal var needsUpdate: Boolean? = null

    inline fun <reified T> value(): T? = value as T

    @Suppress("UNCHECKED_CAST")
    fun clone(): Uniform {

        val value = this.value
        return Uniform(if (value is Cloneable) value.clone() else value)

    }

    override fun toString(): String {
        return "Uniform(value=$value)"
    }

}
