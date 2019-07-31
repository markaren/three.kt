package info.laht.threekt

internal val List<*>.length: Int
    get() = this.size

internal inline fun <reified T> MutableList<T?>.length(length: Int) {

    if (length < size) {
        while (length < size) {
            add(null)
        }
    } else if (length > size) {
        while(length > size) {
            removeAt(length - 1)
        }
    }

}

internal inline fun <reified T> MutableList<T>.shrinkToFit(length: Int) {

    if (length > size) {
        while(length > size) {
            removeAt(length - 1)
        }
    }

}

internal inline fun <reified T> MutableList<T>.safeSet(index: Int, value: T) {

    when {
        index < size -> set(index, value)
        index == size -> add(value)
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

internal inline fun <reified T> MutableList<T>.safeSet(v1: T) {
    if (size > 0) set(0, v1) else add(v1)
}

internal inline fun <reified T> MutableList<T>.safeSet(v1: T, v2: T) {
    if (size > 0) set(0, v1) else add(v1)
    if (size > 1) set(1, v2) else add(v2)
}

internal inline fun <reified T> MutableList<T>.safeSet(v1: T, v2: T, v3: T) {
    if (size > 0) set(0, v1) else add(v1)
    if (size > 1) set(1, v2) else add(v2)
    if (size > 2) set(2, v3) else add(v3)
}

internal inline fun <reified T> MutableList<T>.safeSet(v1: T, v2: T, v3: T, v4: T) {
    if (size > 0) set(0, v1) else add(v1)
    if (size > 1) set(1, v2) else add(v2)
    if (size > 2) set(2, v3) else add(v3)
    if (size > 3) set(3, v4) else add(v4)
}
