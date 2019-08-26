package info.laht.threekt.loaders.gltf

import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Quaternion
import info.laht.threekt.math.Vector3
import kotlinx.serialization.Serializable

@Serializable
internal data class GLTFNode(
    val name: String? = null,
    val camera: Int? = null,
    val children: List<Int> = emptyList(),
    private val matrix: List<Float>? = null,
    @Serializable(QuaternionSerializer::class) private val rotation: Quaternion? = null,
    @Serializable(Vector3Serializer::class) private val scale: Vector3? = null,
    @Serializable(Vector3Serializer::class) private val translation: Vector3? = null
) {

    fun getMatrix(): Matrix4 {

        return if (matrix != null) {
            Matrix4(matrix.toFloatArray())
        } else {
            Matrix4().compose(position = getTranslation(), scale = getScale(), quaternion = getRotation())
        }

    }

    private fun getRotation(): Quaternion {
        return rotation ?: Quaternion()
    }

    private fun getScale(): Vector3 {
        return scale ?: Vector3(1f, 1f, 1f)
    }

    private fun getTranslation(): Vector3 {
        return translation ?: Vector3()
    }

}
