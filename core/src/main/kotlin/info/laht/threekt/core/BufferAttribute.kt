package info.laht.threekt.core

import info.laht.threekt.math.*
import kotlin.properties.Delegates

sealed class BufferAttribute(
        itemSize: Int,
        normalized: Boolean? = null
) : Cloneable {

    internal var name = ""
    internal var version = 0

    var itemSize = itemSize
        private set

    var normalized = normalized ?: false
        private set

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

class IntBufferAttribute @JvmOverloads constructor(
        internal val buffer: IntArray,
        itemSize: Int,
        normalized: Boolean? = null
) : BufferAttribute(itemSize, normalized) {

    override val size: Int
        get() = buffer.size

    @JvmOverloads
    constructor(
            capacity: Int,
            itemSize: Int,
            normalized: Boolean? = null
    ) : this(IntArray(capacity), itemSize, normalized)

    operator fun get(index: Int): Int {
        return buffer[index]
    }

    operator fun set(index: Int, value: Int) {
        buffer[index] = value
    }

//    fun add(value: Int): IntBufferAttribute {
//        buffer.put(value)
//        return this
//    }

    fun getX(index: Int): Int {
        return buffer[index * itemSize]
    }

    fun setX(index: Int, value: Int): IntBufferAttribute {
        buffer[index * itemSize] = value
        return this
    }

    fun getY(index: Int): Int {
        return buffer[index * itemSize + 1]
    }

    fun setY(index: Int, value: Int): IntBufferAttribute {
        buffer[index * itemSize + 1] = value
        return this
    }

    fun getZ(index: Int): Int {
        return buffer[index * itemSize + 2]
    }

    fun setZ(index: Int, value: Int): IntBufferAttribute {
        buffer[index * itemSize + 2] = value
        return this
    }

    fun getW(index: Int): Int {
        return buffer[index * itemSize + 3]
    }

    fun setW(index: Int, value: Int): IntBufferAttribute {
        buffer[index * itemSize + 3] = value
        return this
    }

    fun setXY(index: Int, x: Int, y: Int): IntBufferAttribute {
        @Suppress("NAME_SHADOWING")
        val index = index * itemSize

        buffer[index + 0] = x
        buffer[index + 1] = y

        return this
    }

    fun setXYZ(index: Int, x: Int, y: Int, z: Int): IntBufferAttribute {
        @Suppress("NAME_SHADOWING")
        val index = index * itemSize

        buffer[index + 0] = x
        buffer[index + 1] = y
        buffer[index + 2] = z

        return this
    }

    fun setXYZW(index: Int, x: Int, y: Int, z: Int, w: Int): IntBufferAttribute {
        @Suppress("NAME_SHADOWING")
        val index = index * itemSize

        buffer[index + 0] = x
        buffer[index + 1] = y
        buffer[index + 2] = z
        buffer[index + 3] = w

        return this
    }

    fun copy(source: IntBufferAttribute): IntBufferAttribute {
        super.copy(source)
        source.buffer.copyInto(buffer)
        return this
    }

    @Suppress("NAME_SHADOWING")
    fun copyAt(index1: Int, attribute: IntBufferAttribute, index2: Int): IntBufferAttribute {

        val index1 = index1 * this.itemSize
        val index2 = index2 * attribute.itemSize

        for (i in 0 until itemSize) {
            buffer[index1 + i] = attribute.buffer[index2 + i]
        }

        return this
    }


    override fun clone(): IntBufferAttribute {
        return IntBufferAttribute(buffer.size, 0).copy(this)
    }

}

class FloatBufferAttribute @JvmOverloads constructor(
        internal val buffer: FloatArray,
        itemSize: Int,
        normalized: Boolean? = null
) : BufferAttribute(itemSize, normalized) {

    override val size: Int
        get() = buffer.size

    @JvmOverloads
    constructor(
            capacity: Int,
            itemSize: Int,
            normalized: Boolean? = null
    ) : this(FloatArray(capacity), itemSize, normalized)

    operator fun get(index: Int): Float {
        return buffer[index]
    }

    operator fun set(index: Int, value: Float) {
        buffer[index] = value
    }

//    fun add(value: Float): FloatBufferAttribute {
//        buffer.put(value)
//        return this
//    }

    fun getX(index: Int): Float {
        return buffer[index * itemSize]
    }

    fun setX(index: Int, value: Float): FloatBufferAttribute {
        buffer[index * itemSize] = value
        return this
    }

    fun getY(index: Int): Float {
        return buffer[index * itemSize + 1]
    }

    fun setY(index: Int, value: Float): FloatBufferAttribute {
        buffer[index * itemSize + 1] = value
        return this
    }

    fun getZ(index: Int): Float {
        return buffer[index * itemSize + 2]
    }

    fun setZ(index: Int, value: Float): FloatBufferAttribute {
        buffer[index * itemSize + 2] = value
        return this
    }

    fun getW(index: Int): Float {
        return buffer[index * itemSize + 3]
    }

    fun setW(index: Int, value: Float): FloatBufferAttribute {
        buffer[index * itemSize + 3] = value
        return this
    }

    fun setXY(index: Int, x: Float, y: Float): FloatBufferAttribute {
        @Suppress("NAME_SHADOWING")
        val index = index * itemSize

        buffer[index + 0] = x
        buffer[index + 1] = y

        return this
    }

    fun setXYZ(index: Int, x: Float, y: Float, z: Float): FloatBufferAttribute {
        @Suppress("NAME_SHADOWING")
        val index = index * itemSize

        buffer[index + 0] = x
        buffer[index + 1] = y
        buffer[index + 2] = z

        return this
    }

    fun setXYZW(index: Int, x: Float, y: Float, z: Float, w: Float): FloatBufferAttribute {
        @Suppress("NAME_SHADOWING")
        val index = index * itemSize

        buffer[index + 0] = x
        buffer[index + 1] = y
        buffer[index + 2] = z
        buffer[index + 3] = w

        return this
    }

    fun copyColorsArray(colors: List<Color>): FloatBufferAttribute {

        var offset = 0
        colors.forEach { color ->
            buffer[offset++] = color.r
            buffer[offset++] = color.g
            buffer[offset++] = color.b
        }

        return this
    }


    fun copyVector2sArray(vectors: List<Vector2>): FloatBufferAttribute {

        var offset = 0
        vectors.forEach { vector ->
            buffer[offset++] = vector.x
            buffer[offset++] = vector.y
        }

        return this

    }

    fun copyVector3sArray(vectors: List<Vector3>): FloatBufferAttribute {

        var offset = 0
        vectors.forEach { vector ->
            buffer[offset++] = vector.x
            buffer[offset++] = vector.y
            buffer[offset++] = vector.z
        }

        return this
    }

    fun copyVector4sArray(vectors: List<Vector4>): FloatBufferAttribute {

        var offset = 0
        vectors.forEach { vector ->
            buffer[offset++] = vector.x
            buffer[offset++] = vector.y
            buffer[offset++] = vector.z
            buffer[offset++] = vector.w
        }

        return this
    }

    fun copy(source: FloatBufferAttribute): FloatBufferAttribute {
        super.copy(source)
        source.buffer.copyInto(buffer)
        return this
    }

    @Suppress("NAME_SHADOWING")
    fun copyAt(index1: Int, attribute: FloatBufferAttribute, index2: Int): FloatBufferAttribute {

        val index1 = index1 * this.itemSize
        val index2 = index2 * attribute.itemSize

        for (i in 0 until itemSize) {
            buffer[index1 + i] = attribute.buffer[index2 + i]
        }

        return this
    }

    /**
     * Sets this vector's x and y values from the attribute.
     * @param output the target vector.
     * @param index index in the attribute.
     */
    fun toVector2(index: Int, output: Vector2 = Vector2()): Vector2 {
        output.x = getX(index)
        output.y = getY(index)

        return output
    }

    fun toVector3(index: Int, output: Vector3 = Vector3()): Vector3 {
        output.x = getX(index)
        output.y = getY(index)
        output.z = getZ(index)

        return output
    }

    fun toVector4(index: Int, output: Vector4 = Vector4()): Vector4 {
        output.x = getX(index)
        output.y = getY(index)
        output.z = getZ(index)
        output.w = getW(index)

        return output
    }

    fun toBox3(output: Box3): Box3 {
        var minX = Float.POSITIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var minZ = Float.POSITIVE_INFINITY

        var maxX = Float.NEGATIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY
        var maxZ = Float.NEGATIVE_INFINITY

        for (i in 0 until count) {
            val x = getX(i)
            val y = getY(i)
            val z = getZ(i)

            if (x < minX) minX = x
            if (y < minY) minY = y
            if (z < minZ) minZ = z

            if (x > maxX) maxX = x
            if (y > maxY) maxY = y
            if (z > maxZ) maxZ = z
        }

        output.min.set(minX, minY, minZ)
        output.max.set(maxX, maxY, maxZ)

        return output
    }

    fun applyMatrix3(matrix: Matrix3): FloatBufferAttribute {
        val v1 = Vector3()

        for (i in 0 until count) {
            v1.x = getX(i)
            v1.y = getY(i)
            v1.z = getZ(i)

            v1.applyMatrix3(matrix)

            setXYZ(i, v1.x, v1.y, v1.z)
        }

        return this
    }

    fun applyMatrix4(matrix: Matrix4): FloatBufferAttribute {
        val v1 = Vector3()
        for (i in 0 until count) {
            v1.x = getX(i)
            v1.y = getY(i)
            v1.z = getZ(i)

            v1.applyMatrix4(matrix)

            setXYZ(i, v1.x, v1.y, v1.z)
        }

        return this
    }

    override fun clone(): FloatBufferAttribute {
        return FloatBufferAttribute(buffer.size, 0).copy(this)
    }

}

class BufferAttributes : MutableMap<String, BufferAttribute> by mutableMapOf() {

    val index get() = get("index") as IntBufferAttribute?
    val position get() = get("position") as FloatBufferAttribute?
    val normal get() = get("normal") as FloatBufferAttribute?
    val uv get() = get("uv") as FloatBufferAttribute?
    val uv2 get() = get("uv2") as FloatBufferAttribute?
    val color get() = get("color") as FloatBufferAttribute?
    val tangent get() = get("tangent") as FloatBufferAttribute?


}

//private operator fun IntBuffer.set(index: Int, value: Int) {
//    this.put(index, value)
//}
//
//private operator fun FloatBuffer.set(index: Int, value: Float) {
//    this.put(index, value)
//}
//
//private fun ByteBuffer.clone(): ByteBuffer {
//
//    // Create clone with same capacity as original.
//    val clone = BufferUtils.createByteBuffer(capacity())
//    // Create a read-only copy of the original.
//    // This allows reading from the original without modifying it.
//    val readOnlyCopy = asReadOnlyBuffer()
//
//    readOnlyCopy.rewind()
//    clone.put(readOnlyCopy)
//    clone.flip()
//
//    return clone
//}
//
//private fun getAllocationSize(elements: Int, elementShift: Int): Int {
//    APIUtil.apiCheckAllocation(elements, APIUtil.apiGetBytes(elements, elementShift), 0x7FFF_FFFFL)
//    return elements shl elementShift
//}
