package info.laht.threekt

import java.lang.reflect.Field

/**
 * Returns the first [Field] in the hierarchy for the specified name
 */
internal fun getFieldInHiarchy(clazz: Class<*>?, name: String): Field? {

    @Suppress("NAME_SHADOWING")
    var clazz: Class<*>? = clazz
    var field: Field? = null
    while (clazz != null && field == null) {
        try {
            field = clazz.getDeclaredField(name)
        } catch (e: Exception) {
        }

        clazz = clazz.superclass
    }
    return field
}

internal fun List<*>.length() : Int = this.size

internal inline fun <reified T> MutableList<T>.length(newLength: Int, noinline defaultValue: (() -> T)? = null) {
    while (newLength > length()) {
        add(defaultValue!!.invoke())
    }
    while (newLength < length()) {
        removeAt(length()-1)
    }
}
