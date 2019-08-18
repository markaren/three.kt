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
    private val rotation: List<Float>? = null,
    private val scale: List<Float>? = null,
    private val translation: List<Float>? = null
) {

    fun getMatrix(): Matrix4 {

        return if (matrix != null) {
            Matrix4(matrix.toFloatArray())
        } else {
            Matrix4().compose(position = getTranslation(), scale = getScale(), quaternion = getRotation())
        }

    }

    private fun getRotation(): Quaternion {
        return rotation?.let { Quaternion(it[0], it[1], it[2], it[3]) } ?: Quaternion()
    }

    private fun getScale(): Vector3 {
        return scale?.let { Vector3(it[0], it[1], it[2]) } ?: Vector3(1f, 1f, 1f)
    }

    private fun getTranslation(): Vector3 {
        return translation?.let { Vector3(it[0], it[1], it[2]) } ?: Vector3()
    }

}
