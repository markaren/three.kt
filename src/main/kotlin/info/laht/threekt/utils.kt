package info.laht.threekt

import java.lang.reflect.Field
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Returns the first [Field] in the hierarchy for the specified name
 */
fun getFieldInHiarchy(clazz: Class<*>?, name: String): Field? {

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

inline fun <reified T> MutableList<T>.safeSet(index: Int, value: T, defaultValue: () ->T) {
    while (index >= size ) {
        add(defaultValue.invoke())
    }
    set(index, value)
}
