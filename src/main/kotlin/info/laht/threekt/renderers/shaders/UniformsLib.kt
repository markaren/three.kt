package info.laht.threekt.renderers.shaders

import info.laht.threekt.core.Uniform
import info.laht.threekt.math.Color
import info.laht.threekt.math.Matrix3
import info.laht.threekt.math.Vector2
import info.laht.threekt.math.Vector2i

object UniformsLib {

    val common = mapOf(
        "diffuse" to Uniform(Color.fromHex(0xeeeeee)),
        "opacity" to Uniform(1f),
        "additionalDetails" to Uniform(null),
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
        "normalScale" to Uniform(Vector2i(1, 1))
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
    
//    val lights = mapOf(
//
//        "ambientLightColor" to Uniform( mutableListOf<Float>() )
//
//        "lightProbe" to Uniform( [] },
//
//        "directionalLights" to Uniform( emptyl, properties: {
//            "direction": {},
//            "color: {},
//
//            shadow: {},
//            shadowBias: {},
//            shadowRadius: {},
//            shadowMapSize: {}
//        } },
//
//        "directionalShadowMap" to Uniform( [] },
//        "directionalShadowMatrix" to Uniform( [] },
//
//        spotLights" to Uniform( [], properties: {
//            color: {},
//            position: {},
//            direction: {},
//            distance: {},
//            coneCos: {},
//            penumbraCos: {},
//            decay: {},
//
//            shadow: {},
//            shadowBias: {},
//            shadowRadius: {},
//            shadowMapSize: {}
//        } },
//
//        spotShadowMap" to Uniform( [] },
//        spotShadowMatrix" to Uniform( [] },
//
//        pointLights" to Uniform( [], properties: {
//            color: {},
//            position: {},
//            decay: {},
//            distance: {},
//
//            shadow: {},
//            shadowBias: {},
//            shadowRadius: {},
//            shadowMapSize: {},
//            shadowCameraNear: {},
//            shadowCameraFar: {}
//        } },
//
//        pointShadowMap" to Uniform( [] },
//        pointShadowMatrix" to Uniform( [] },
//
//        hemisphereLights" to Uniform( [], properties: {
//            direction: {},
//            skyColor: {},
//            groundColor: {}
//        } },
//
//        // TODO (abelnation): RectAreaLight BRDF data needs to be moved from example to main src
//        rectAreaLights" to Uniform( [], properties: {
//            color: {},
//            position: {},
//            width: {},
//            height: {}
//        } }
//
//    )

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
