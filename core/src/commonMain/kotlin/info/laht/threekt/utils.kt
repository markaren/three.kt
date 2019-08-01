package info.laht.threekt

internal val List<*>.length: Int
    get() = this.size

internal inline fun <reified T> MutableList<T?>.length(length: Int) {

    if (length < size) {
        while (length < size) {
            add(null)
        }
    } else if (length > size) {
        while (length > size) {
            removeAt(length - 1)
        }
    }

}

internal inline fun <reified T> MutableList<T>.shrinkToFit(length: Int) {

    if (length > size) {
        while (length > size) {
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
    if (size == 0) {
        add(v1)
    } else {
        set(0, v1)
    }
}

internal inline fun <reified T> MutableList<T>.safeSet(v1: T, v2: T) {
    if (size == 0) {
        add(v1)
        add(v2)
    } else if (size == 1) {
        set(0, v1)
        add(v2)
    } else {
        set(0, v1)
        set(1, v2)

    }
}

internal inline fun <reified T> MutableList<T>.safeSet(v1: T, v2: T, v3: T) {
    if (size == 0) {
        add(v1)
        add(v2)
        add(v3)
    } else if (size == 1) {
        set(0, v1)
        add(v2)
        add(v3)
    } else if (size == 2) {
        set(0, v1)
        set(1, v2)
        add(v3)
    } else {
        set(0, v1)
        set(1, v2)
        set(2, v3)
    }
}

internal inline fun <reified T> MutableList<T>.safeSet(v1: T, v2: T, v3: T, v4: T) {
    if (size == 0) {
        add(v1)
        add(v2)
        add(v3)
        add(v4)
    } else if (size == 1) {
        set(0, v1)
        add(v2)
        add(v3)
        add(v4)
    } else if (size == 2) {
        set(0, v1)
        set(1, v2)
        add(v3)
        add(v4)
    } else if (size == 3) {
        set(0, v1)
        set(1, v2)
        set(2, v3)
        add(v4)
    } else {
        set(0, v1)
        set(1, v2)
        set(2, v3)
        set(3, v4)
    }
}


internal fun FloatArray.contentEquals(list: List<Float>, allowLongerList: Boolean = false): Boolean {

    if (allowLongerList) {
        if (size > list.size) return false
    } else {
        if (size != list.size) return false
    }

    for (i in 0 until size) {
        if (get(i) != list[0]) return false
    }

    return true
}


internal fun FloatArray.copyInto(list: MutableList<Float>): MutableList<Float> {

    while (list.size <= size) {
        list.add(0f)
    }

    forEach {
        list.add(it)
    }

    return list

}
