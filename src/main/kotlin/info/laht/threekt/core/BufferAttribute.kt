package info.laht.threekt.core


import info.laht.threekt.math.*
import kotlin.properties.Delegates


sealed class BufferAttribute(
    internal var itemSize: Int,
    internal var normalized: Boolean = false
) : Cloneable {

    var name = ""

    internal var version = 0

    abstract val size: Int

    internal val count: Int
        get() = size / itemSize

    var dynamic: Boolean = false
    internal var updateRange = UpdateRange(0, -1)

    internal var onUploadCallback: (() -> Unit)? = null

    var needsUpdate by Delegates.observable(false) { _, _, newValue ->
        if (newValue) version++
    }

    fun copy(source: BufferAttribute): BufferAttribute {

        this.name = source.name
        this.itemSize = source.itemSize
        this.normalized = source.normalized

        this.dynamic = source.dynamic

        return this

    }

    abstract override fun clone(): BufferAttribute

}

class IntBufferAttribute(
    internal var array: IntArray,
    itemSize: Int,
    normalized: Boolean = false
) : BufferAttribute(itemSize, normalized) {

    override val size: Int
        get() = array.size

    fun getX(index: Int): Int {
        return array[index * itemSize]
    }

    fun setX(index: Int, value: Int): IntBufferAttribute {
        array[index * itemSize] = value
        return this
    }

    fun getY(index: Int): Int {
        return array[index * itemSize + 1]
    }

    fun setY(index: Int, value: Int): IntBufferAttribute {
        array[index * itemSize + 1] = value
        return this
    }

    fun getZ(index: Int): Int {
        return array[index * itemSize + 2]
    }

    fun setZ(index: Int, value: Int): IntBufferAttribute {
        array[index * itemSize + 2] = value
        return this
    }

    fun getW(index: Int): Int {
        return array[index * itemSize + 3]
    }

    fun setW(index: Int, value: Int): IntBufferAttribute {
        array[index * itemSize + 3] = value
        return this
    }

    fun setXY(index: Int, x: Int, y: Int): IntBufferAttribute {
        @Suppress("NAME_SHADOWING")
        val index = index * itemSize

        array[index + 0] = x
        array[index + 1] = y

        return this
    }

    fun setXYZ(index: Int, x: Int, y: Int, z: Int): IntBufferAttribute {
        @Suppress("NAME_SHADOWING")
        val index = index * itemSize

        array[index + 0] = x
        array[index + 1] = y
        array[index + 2] = z

        return this
    }

    fun setXYZW(index: Int, x: Int, y: Int, z: Int, w: Int): IntBufferAttribute {
        @Suppress("NAME_SHADOWING")
        val index = index * itemSize

        array[index + 0] = x
        array[index + 1] = y
        array[index + 2] = z
        array[index + 3] = w

        return this
    }

    fun copy(source: IntBufferAttribute): IntBufferAttribute {
        super.copy(this)
        array = source.array.clone()
        return this
    }

    @Suppress("NAME_SHADOWING")
    fun copyAt(index1: Int, attribute: IntBufferAttribute, index2: Int): IntBufferAttribute {

        val index1 = index1 * this.itemSize;
        val index2 = index2 * attribute.itemSize;

        for (i in 0 until itemSize) {
            array[index1 + i] = attribute.array[index2 + i];
        }

        return this
    }


    override fun clone(): IntBufferAttribute {
        return IntBufferAttribute(array.clone(), itemSize, normalized)
    }

}

class FloatBufferAttribute(
    internal var array: FloatArray,
    itemSize: Int,
    normalized: Boolean = false
) : BufferAttribute(itemSize, normalized) {

    override val size: Int
        get() = array.size

    fun getX(index: Int): Float {
        return array[index * itemSize]
    }

    fun setX(index: Int, value: Float): FloatBufferAttribute {
        array[index * itemSize] = value
        return this
    }

    fun getY(index: Int): Float {
        return array[index * itemSize + 1]
    }

    fun setY(index: Int, value: Float): FloatBufferAttribute {
        array[index * itemSize + 1] = value
        return this
    }

    fun getZ(index: Int): Float {
        return array[index * itemSize + 2]
    }

    fun setZ(index: Int, value: Float): FloatBufferAttribute {
        array[index * itemSize + 2] = value
        return this
    }

    fun getW(index: Int): Float {
        return array[index * itemSize + 3]
    }

    fun setW(index: Int, value: Float): FloatBufferAttribute {
        array[index * itemSize + 3] = value
        return this
    }


    fun setXY(index: Int, x: Float, y: Float): FloatBufferAttribute {
        @Suppress("NAME_SHADOWING")
        val index = index * itemSize

        array[index + 0] = x
        array[index + 1] = y

        return this
    }

    fun setXYZ(index: Int, x: Float, y: Float, z: Float): FloatBufferAttribute {
        @Suppress("NAME_SHADOWING")
        val index = index * itemSize

        array[index + 0] = x
        array[index + 1] = y
        array[index + 2] = z

        return this
    }

    fun setXYZW(index: Int, x: Float, y: Float, z: Float, w: Float): FloatBufferAttribute {
        @Suppress("NAME_SHADOWING")
        val index = index * itemSize

        array[index + 0] = x
        array[index + 1] = y
        array[index + 2] = z
        array[index + 3] = w

        return this
    }

    fun copyColorsArray(colors: List<Color>): FloatBufferAttribute {

        var offset = 0
        colors.forEach { color ->
            array[offset++] = color.r
            array[offset++] = color.g
            array[offset++] = color.b
        }

        return this
    }


    fun copyVector2sArray(vectors: List<Vector2>): FloatBufferAttribute {

        var offset = 0
        vectors.forEach { vector ->
            array[offset++] = vector.x
            array[offset++] = vector.y
        }

        return this

    }

    fun copyVector3sArray(vectors: List<Vector3>): FloatBufferAttribute {

        var offset = 0
        vectors.forEach { vector ->
            array[offset++] = vector.x
            array[offset++] = vector.y
            array[offset++] = vector.z
        }

        return this
    }

    fun copyVector4sArray(vectors: List<Vector4>): FloatBufferAttribute {

        var offset = 0
        vectors.forEach { vector ->
            array[offset++] = vector.x
            array[offset++] = vector.y
            array[offset++] = vector.z
            array[offset++] = vector.w
        }

        return this
    }

    fun copy(source: FloatBufferAttribute): FloatBufferAttribute {
        super.copy(this)
        array = source.array.clone()
        return this
    }

    @Suppress("NAME_SHADOWING")
    fun copyAt(index1: Int, attribute: FloatBufferAttribute, index2: Int): FloatBufferAttribute {

        val index1 = index1 * this.itemSize;
        val index2 = index2 * attribute.itemSize;

        for (i in 0 until itemSize) {
            array[index1 + i] = attribute.array[index2 + i];
        }

        return this
    }

    override fun clone(): FloatBufferAttribute {
        return FloatBufferAttribute(array.clone(), itemSize, normalized)
    }

}

class BufferAttributes : HashMap<String, BufferAttribute>() {

    val index get() = get("index") as IntBufferAttribute?
    val position get() = get("position") as FloatBufferAttribute?
    val normal get() = get("normal") as FloatBufferAttribute?
    val uv get() = get("uv") as IntBufferAttribute?
    val color get() = get("color") as FloatBufferAttribute?
    val tangent get() = get("tangent") as FloatBufferAttribute?

}
