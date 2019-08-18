package info.laht.threekt.loaders.gltf

import info.laht.threekt.cameras.Camera
import info.laht.threekt.cameras.OrthographicCamera
import info.laht.threekt.cameras.PerspectiveCamera
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class GLTFCamera(

    val perspective: Perspective? = null,
    val orthographic: Orthographic? = null,

    val type: String,
    val name: String? = null,

    val extensions: JsonObject? = null,
    val extras: JsonElement? = null

) {

    fun getCamera(): Camera {

        return when {
            perspective != null -> PerspectiveCamera(
                perspective.yfov, perspective.aspectRatio, perspective.znear, perspective.zfar
                    ?: PerspectiveCamera.DEFAULT_FAR
            )
            orthographic != null -> OrthographicCamera(
                orthographic.xmag / -2,
                orthographic.xmag / 2,
                orthographic.ymag / -2,
                orthographic.ymag / 2,
                orthographic.znear,
                orthographic.zfar
            )
            else -> throw IllegalStateException("Neither perspective or orthographic..")
        }.also { camera ->
            name?.also { name -> camera.name = name }
        }

    }

    @Serializable
    data class Perspective(
        val yfov: Float,
        val aspectRatio: Float,
        val znear: Float,
        val zfar: Float? = null
    )

    @Serializable
    data class Orthographic(
        val xmag: Float,
        val ymag: Float,
        val znear: Float,
        val zfar: Float
    )

}
