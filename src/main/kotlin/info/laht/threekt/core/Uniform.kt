package info.laht.threekt.core


class Uniform(
    var value: Any?,
    private val properties: MutableMap<String, Any> = mutableMapOf()
) {

    var needsUpdate = false

    inline fun <reified T> value(): T? = value as T

    internal fun setProperty(key: String, value: Any) {
        properties[key] = value
    }

    fun getProperty(key: String): Any {
        return properties[key] ?: throw IllegalArgumentException("No such key $key in ${properties.keys}")
    }

    @Suppress("UNCHECKED_CAST")
    fun clone(): Uniform {

        val value = this.value
        return Uniform(if (value is Cloneable) value.clone() else value).also {
            properties.forEach { (key, value) ->
                it.setProperty(key, if (value is Cloneable) value.clone() else value)
            }
        }

    }

    override fun toString(): String {
        return "Uniform(value=$value, properties=$properties)"
    }

}

//sealed class UniformValue: Cloneable {
//
//    abstract override fun clone(): UniformValue
//
//}
//
//class IntUniformValue(
//    val value: Int
//): UniformValue() {
//
//    override fun clone(): IntUniformValue {
//        return IntUniformValue(value)
//    }
//}
//
//class FloatUniformValue(
//    val value: Float
//): UniformValue() {
//
//    override fun clone(): FloatUniformValue {
//        return FloatUniformValue(value)
//    }
//}
//
//class Vector2iUniformValue(
//    val value: Vector2i
//): UniformValue() {
//
//    override fun clone(): Vector2iUniformValue {
//        return Vector2iUniformValue(value.clone())
//    }
//}
//
//class Vector2UniformValue(
//    val value: Vector2
//): UniformValue() {
//
//    override fun clone(): Vector2UniformValue {
//        return Vector2UniformValue(value.clone())
//    }
//}
//
//class Vector3UniformValue(
//    val value: Vector3
//): UniformValue() {
//
//    override fun clone(): Vector3UniformValue {
//        return Vector3UniformValue(value.clone())
//    }
//}
//
//class Vector4UniformValue(
//    val value: Vector4
//): UniformValue() {
//
//    override fun clone(): Vector4UniformValue {
//        return Vector4UniformValue(value.clone())
//    }
//}
//
//class Matrix3UniformValue(
//    val value: Matrix3
//): UniformValue() {
//
//    override fun clone(): Matrix3UniformValue {
//        return Matrix3UniformValue(value.clone())
//    }
//}
//
//class Matrix4UniformValue(
//    val value: Matrix4
//): UniformValue() {
//
//    override fun clone(): Matrix4UniformValue {
//        return Matrix4UniformValue(value.clone())
//    }
//}