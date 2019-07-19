package info.laht.threekt

import java.lang.reflect.Field

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
