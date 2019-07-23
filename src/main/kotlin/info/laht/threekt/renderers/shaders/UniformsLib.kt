package info.laht.threekt.renderers.shaders

import info.laht.threekt.core.Uniform
import info.laht.threekt.math.Color
import info.laht.threekt.math.Matrix3
import info.laht.threekt.math.Vector2
import info.laht.threekt.math.Vector3

object UniformsLib {

    val common = mapOf(

            "diffuse" to Uniform(Color.fromHex(0xeeeeee)),
            "opacity" to Uniform(1f),

            "map" to Uniform(null),

            "uvTransform" to Uniform(Matrix3()),
            "alphaMap" to Uniform(null)

    )

    val specularmap = mapOf(

            "specularMap" to Uniform(null)

    )

    val envmap = mapOf(

            "envMap" to Uniform(null),
            "flipEnvMap" to Uniform(-1),
            "reflectivity" to Uniform(1f),
            "refractionRatio" to Uniform(0.98f),
            "maxMipLevel" to Uniform(0)

    )

    val aomap = mapOf(

            "aoMap" to Uniform(null),
            "aoMapIntensity" to Uniform(1f)

    )

    val lightmap = mapOf(

            "lightMap" to Uniform(null),
            "lightMapIntensity" to Uniform(1f)

    )

    val emissivemap = mapOf(

            "emissiveMap" to Uniform(null)

    )

    val bumpmap = mapOf(

            "bumpMap" to Uniform(null),
            "bumpScale" to Uniform(1f)

    )

    val normalmap = mapOf(

            "normalMap" to Uniform(null),
            "normalScale" to Uniform(Vector2(1, 1))
    )

    val displacementmap = mapOf(

            "displacementMap" to Uniform(null),
            "displacementScale" to Uniform(1f),
            "displacementBias" to Uniform(0f)

    )

    val roughnessmap = mapOf(

            "roughnessMap" to Uniform(null)

    )

    val metalnessmap = mapOf(

            "metalnessMap" to Uniform(null)

    )

    val gradientmap = mapOf(

            "gradientMap" to Uniform(null)

    )

    val fog = mapOf(

            "fogDensity" to Uniform(0.00025f),
            "fogNear" to Uniform(1f),
            "fogFar" to Uniform(2000f),
            "fogColor" to Uniform(Color(0xffffff))

    )

    val lights = mapOf(

            "ambientLightColor" to Uniform(null),

            "lightProbe" to Uniform(null),

            "directionalLights" to Uniform(null, mutableMapOf(
                    "direction" to null,
                    "color" to null,

                    "shadow" to null,
                    "shadowBias" to null,
                    "shadowRadius" to null,
                    "shadowMapSize" to null
            )),

            "directionalShadowMap" to Uniform(null),
            "directionalShadowMatrix" to Uniform(null),

            "spotLights" to Uniform(null, mutableMapOf(
                    "color" to null,
                    "position" to null,
                    "direction" to null,
                    "distance" to null,
                    "coneCos" to null,
                    "penumbraCos" to null,
                    "decay" to null,

                    "shadow" to null,
                    "shadowBias" to null,
                    "shadowRadius" to null,
                    "shadowMapSize" to null
            )),

            "spotShadowMap" to Uniform(null),
            "spotShadowMatrix" to Uniform(null),

            "pointLights" to Uniform(null, mutableMapOf(
                    "color" to null,
                    "position" to null,
                    "decay" to null,
                    "distance" to null,

                    "shadow" to null,
                    "shadowBias" to null,
                    "shadowRadius" to null,
                    "shadowMapSize" to null,
                    "shadowCameraNear" to null,
                    "shadowCameraFar" to null
            )),

            "pointShadowMap" to Uniform(null),
            "pointShadowMatrix" to Uniform(null),

            "hemisphereLights" to Uniform(null, mutableMapOf(
                    "direction" to Vector3(),
                    "skyColor" to Color(),
                    "groundColor" to Color()
            )),

            "rectAreaLights" to Uniform(null, mutableMapOf(
                    "color" to Color(),
                    "position" to Vector3(),
                    "width" to null,
                    "height" to null
            ))

    )

    val points = mapOf(

            "diffuse" to Uniform(Color(0xeeeeee)),
            "opacity" to Uniform(1f),
            "size" to Uniform(1f),
            "scale" to Uniform(1f),
            "map" to Uniform(null),
            "uvTransform" to Uniform(Matrix3())


    )

    val sprite = mapOf(

            "diffuse" to Uniform(Color(0xeeeeee)),
            "opacity" to Uniform(1f),
            "center" to Uniform(Vector2(0.5f, 0.5f)),
            "rotation" to Uniform(0f),
            "map" to Uniform(null),
            "uvTransform" to Uniform(Matrix3())

    )

}
