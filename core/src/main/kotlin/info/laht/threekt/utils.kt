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

internal val List<*>.length: Int
    get() = this.size

internal inline fun <reified T> MutableList<T?>.length(length: Int) {

    if (length < size) {
        while (length < size) {
            add(null)
        }
    } else if (length > size) {
        removeAt(length - 1)
    }

}

internal inline fun <reified T> MutableList<T?>.safeSet(index: Int, value: T) {

    when {
        index < size -> set(index, value)
        index == size -> add(value)
        else -> while (index > size) {
            if (index == size) add(value) else add(null)
        }
    }

}

internal inline fun <reified T> MutableList<T>.add(v1: T, v2: T) {
    add(v1)
    add(v2)
}

internal inline fun <reified T> MutableList<T>.add(v1: T, v2: T, v3: T) {
    add(v1)
    add(v2)
    add(v3)
}
