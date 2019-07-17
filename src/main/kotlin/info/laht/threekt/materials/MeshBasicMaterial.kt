package info.laht.threekt.materials

import info.laht.threekt.MultiplyOperation
import info.laht.threekt.math.Color
import info.laht.threekt.textures.Texture

class MeshBasicMaterial(
    parameters: MeshBasicMaterialParameters? = null
): Material(parameters) {

    val color = Color.fromHex(0xffffff)

    var map: Texture? = null

    var aoMap: Texture? = null
    var aoMapIntensity = 1f

    var specularMap: Texture? = null
    var alphaMap: Texture? = null
    var envMap: Texture? = null
    var combine = MultiplyOperation
    var reflectivity = 1f
    var refractionRatio = 0.98f

    var wireframe = false
    var wireframeLinewidth = 1f
    var wireframeLinecap = "round"
    var wireframeLinejoin = "round"

    var skinning = false
    var morphTargets = false

    init {

        parameters?.also { source ->
            source.color?.also { this.color.copy(it)}
            source.map.also { this.map = it }

            lights = false

        }

    }

}

class MeshBasicMaterialParameters: MaterialParameters() {

    var color: Color? = null
    var map: Texture? = null
    var aoMap: Texture? = null
    var aoMapIntensity: Float? = null
    var specularMap: Texture? = null
    var alphaMap: Texture? = null
    var envMap: Texture? = null
    var combine: Int? = null
    var reflectivity: Float? = null
    var refractionRatio: Float? = null
    var wireframe: Boolean? = null;
    var wireframeLinewidth: Float? = null
    var wireframeLinecap: String? = null
    var wireframeLinejoin: String? = null
    var skinning: Boolean? = null
    var morphTargets: Boolean? = null

}