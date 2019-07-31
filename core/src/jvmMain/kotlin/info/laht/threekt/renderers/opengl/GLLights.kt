package info.laht.threekt.renderers.opengl

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.Object3D
import info.laht.threekt.lights.*
import info.laht.threekt.math.Color
import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Vector2
import info.laht.threekt.math.Vector3
import info.laht.threekt.safeSet
import info.laht.threekt.shrinkToFit
import info.laht.threekt.textures.Texture
import kotlin.math.cos

internal class GLLights {

    private val cache = UniformsCache()

    internal val state = GLLightsState()

    private var vector3 = Vector3()
//    private var matrix4 = Matrix4()
//    private var matrix42 = Matrix4()

    fun setup(lights: List<Light>, shadows: List<Object3D>, camera: Camera) {

        var r = 0f
        var g = 0f
        var b = 0f

        state.probe.forEach { it.set(0, 0, 0) }

        var directionalLength = 0
        var pointLength = 0
        var spotLength = 0
        var rectAreaLength = 0
        var hemiLength = 0

        val viewMatrix = camera.matrixWorldInverse

        lights.forEach { light ->

            val color = light.color
            val intensity = light.intensity

            val shadowMap = if (light is LightWithShadow && light.shadow.map != null) {
                light.shadow.map!!.texture
            } else null

            when (light) {
                is AmbientLight -> {
                    r += color.r * intensity
                    g += color.g * intensity
                    b += color.b * intensity
                }
                is LightProbe -> {
                    for (i in 0 until 9) {
                        state.probe[i].addScaledVector(light.sh.coefficients[i], intensity)
                    }
                }
                is DirectionalLight -> {
                    val uniforms = cache[light] as DirectionalLightUniforms

                    uniforms.color.copy(light.color).multiplyScalar(light.intensity)
                    uniforms.direction.setFromMatrixPosition(light.matrixWorld)
                    vector3.setFromMatrixPosition(light.target.matrixWorld)
                    uniforms.direction.sub(vector3)
                    uniforms.direction.transformDirection(viewMatrix)

                    uniforms.shadow = light.castShadow

                    if (light.castShadow) {

                        val shadow = light.shadow

                        uniforms.shadowBias = shadow.bias
                        uniforms.shadowRadius = shadow.radius
                        uniforms.shadowMapSize = shadow.mapSize

                    }

                    state.directionalShadowMap.safeSet(directionalLength, shadowMap)
                    state.directionalShadowMatrix.safeSet(directionalLength, light.shadow.matrix)
                    state.directional.safeSet(directionalLength, uniforms)

                    directionalLength++
                }
                is SpotLight -> {
                    val distance = light.distance
                    val uniforms = cache[light] as SpotLightUniforms

                    uniforms.position.setFromMatrixPosition(light.matrixWorld)
                    uniforms.position.applyMatrix4(viewMatrix)

                    uniforms.color.copy(color).multiplyScalar(intensity)
                    uniforms.distance = distance

                    uniforms.direction.setFromMatrixPosition(light.matrixWorld)
                    vector3.setFromMatrixPosition(light.target.matrixWorld)
                    uniforms.direction.sub(vector3)
                    uniforms.direction.transformDirection(viewMatrix)

                    uniforms.coneCos = cos(light.angle)
                    uniforms.penumbraCos = cos(light.angle * (1 - light.penumbra))
                    uniforms.decay = light.decay

                    uniforms.shadow = light.castShadow

                    if (light.castShadow) {

                        val shadow = light.shadow

                        uniforms.shadowBias = shadow.bias
                        uniforms.shadowRadius = shadow.radius
                        uniforms.shadowMapSize.copy(shadow.mapSize)

                    }

                    state.spotShadowMap.safeSet(pointLength, shadowMap)
                    state.spotShadowMatrix.safeSet(pointLength, light.shadow.matrix)
                    state.spot.safeSet(pointLength, uniforms)

                    spotLength++
                }
                is PointLight -> {
                    val uniforms = cache[light] as PointLightUniforms

                    uniforms.position.setFromMatrixPosition(light.matrixWorld)
                    uniforms.position.applyMatrix4(viewMatrix)

                    uniforms.color.copy(light.color).multiplyScalar(light.intensity)
                    uniforms.distance = light.distance
                    uniforms.decay = light.decay

                    uniforms.shadow = light.castShadow

                    if (light.castShadow) {

                        val shadow = light.shadow

                        uniforms.shadowBias = shadow.bias
                        uniforms.shadowRadius = shadow.radius
                        uniforms.shadowMapSize = shadow.mapSize
                        uniforms.shadowCameraNear = shadow.camera.near
                        uniforms.shadowCameraFar = shadow.camera.far

                    }

                    state.pointShadowMap.safeSet(pointLength, shadowMap)
                    state.pointShadowMatrix.safeSet(pointLength, light.shadow.matrix)
                    state.point.safeSet(pointLength, uniforms)

                    pointLength++
                }
            }

        }

        state.ambient.r = r
        state.ambient.g = g
        state.ambient.b = b

        val hash = state.hash

        if (hash.directionalLength != directionalLength ||
                hash.pointLength != pointLength ||
                hash.spotLength != spotLength ||
                hash.rectAreaLength != rectAreaLength ||
                hash.hemiLength != hemiLength ||
                hash.shadowsLength != shadows.size
        ) {

            state.directional.shrinkToFit(directionalLength)
            state.spot.shrinkToFit(spotLength)
            state.rectArea.shrinkToFit(rectAreaLength)
            state.point.shrinkToFit(pointLength)
            state.hemi.shrinkToFit(hemiLength)

            hash.directionalLength = directionalLength
            hash.pointLength = pointLength
            hash.spotLength = spotLength
            hash.rectAreaLength = rectAreaLength
            hash.hemiLength = hemiLength
            hash.shadowsLength = shadows.size

            state.version = nextVersion++

        }

    }

    private companion object {

        var nextVersion = 0

    }

    internal inner class GLLightsState {

        var version = 0

        val hash = Hash()

        val ambient = Color(0f, 0f, 0f)
        val probe = Array(9) { Vector3() }
        val directional = mutableListOf<DirectionalLightUniforms>()
        val directionalShadowMap = mutableListOf<Texture?>()
        val directionalShadowMatrix = mutableListOf<Matrix4>()
        val spot = mutableListOf<SpotLightUniforms>()
        val spotShadowMap = mutableListOf<Texture?>()
        val spotShadowMatrix = mutableListOf<Matrix4>()
        val rectArea = mutableListOf<RectAreaLightUniforms>()
        val point = mutableListOf<PointLightUniforms>()
        val pointShadowMap = mutableListOf<Texture?>()
        val pointShadowMatrix = mutableListOf<Matrix4>()
        val hemi = mutableListOf<HemisphereLightUniforms>()

        inner class Hash {
            var directionalLength = -1
            var pointLength = -1
            var spotLength = -1
            var rectAreaLength = -1
            var hemiLength = -1
            var shadowsLength = -1
        }

    }

    private inner class UniformsCache {

        private val map = mutableMapOf<Int, LightUniforms>()

        operator fun get(light: Light): LightUniforms {

            return map.computeIfAbsent(light.id) {

                when (light) {
                    is AmbientLight -> AmbientLightUniforms()
                    is DirectionalLight -> DirectionalLightUniforms()
                    is PointLight -> PointLightUniforms()
                    is SpotLight -> SpotLightUniforms()
                    else -> throw IllegalArgumentException("Unsupported light: $light")
                }

            }

        }

    }

}

internal sealed class LightUniforms : HashMap<String, Any>()

internal class AmbientLightUniforms : LightUniforms()

internal class DirectionalLightUniforms : LightUniforms() {

    init {
        putAll(
                mapOf(
                        "direction" to Vector3(),
                        "color" to Color(),

                        "shadow" to false,
                        "shadowBias" to 0f,
                        "shadowRadius" to 1f,
                        "shadowMapSize" to Vector2()
                )
        )
    }

    var direction: Vector3
        get() = get("direction") as Vector3
        set(value) {
            direction.copy(value)
        }
    var color: Color
        get() = get("color") as Color
        set(value) {
            color.copy(value)
        }

    var shadow: Boolean
        get() = get("shadow") as Boolean
        set(value) {
            set("shadow", value)
        }
    var shadowBias: Float
        get() = get("shadowBias") as Float
        set(value) {
            set("shadowBias", value)
        }
    var shadowRadius: Float
        get() = get("shadowRadius") as Float
        set(value) {
            set("shadowRadius", value)
        }
    var shadowMapSize: Vector2
        get() = get("shadowMapSize") as Vector2
        set(value) {
            shadowMapSize.copy(value)
        }

}

internal class SpotLightUniforms : LightUniforms() {

    init {
        putAll(
                mapOf(
                        "position" to Vector3(),
                        "direction" to Vector3(),
                        "color" to Color(),
                        "distance" to 0f,
                        "coneCos" to 0f,
                        "penumbraCos" to 0f,
                        "decay" to 0f,

                        "shadow" to false,
                        "shadowBias" to 0f,
                        "shadowRadius" to 1f,
                        "shadowMapSize" to Vector2()
                )
        )
    }

    var position: Vector3
        get() = get("position") as Vector3
        set(value) {
            position.copy(value)
        }
    var direction: Vector3
        get() = get("direction") as Vector3
        set(value) {
            direction.copy(value)
        }
    var color: Color
        get() = get("color") as Color
        set(value) {
            color.copy(value)
        }
    var distance: Float
        get() = get("distance") as Float
        set(value) {
            set("distance", value)
        }
    var coneCos: Float
        get() = get("coneCos") as Float
        set(value) {
            set("coneCos", value)
        }
    var penumbraCos: Float
        get() = get("penumbraCos") as Float
        set(value) {
            set("penumbraCos", value)
        }
    var decay: Float
        get() = get("decay") as Float
        set(value) {
            set("decay", value)
        }

    var shadow: Boolean
        get() = get("shadow") as Boolean
        set(value) {
            set("shadow", value)
        }
    var shadowBias: Float
        get() = get("shadowBias") as Float
        set(value) {
            set("shadowBias", value)
        }
    var shadowRadius: Float
        get() = get("shadowRadius") as Float
        set(value) {
            set("shadowRadius", value)
        }
    var shadowMapSize: Vector2
        get() = get("shadowMapSize") as Vector2
        set(value) {
            shadowMapSize.copy(value)
        }

}

internal class PointLightUniforms : LightUniforms() {

    init {
        putAll(
                mapOf(
                        "position" to Vector3(),
                        "color" to Color(),
                        "distance" to 0f,
                        "decay" to 0f,

                        "shadow" to false,
                        "shadowBias" to 0f,
                        "shadowRadius" to 1f,
                        "shadowMapSize" to Vector2(),
                        "shadowCameraNear" to 1f,
                        "shadowCameraFar" to 1000f
                )
        )
    }

    var position: Vector3
        get() = get("position") as Vector3
        set(value) {
            position.copy(value)
        }
    var color: Color
        get() = get("color") as Color
        set(value) {
            color.copy(value)
        }
    var distance: Float
        get() = get("distance") as Float
        set(value) {
            set("distance", value)
        }
    var decay: Float
        get() = get("decay") as Float
        set(value) {
            set("decay", value)
        }

    var shadow: Boolean
        get() = get("shadow") as Boolean
        set(value) {
            set("shadow", value)
        }
    var shadowBias: Float
        get() = get("shadowBias") as Float
        set(value) {
            set("shadowBias", value)
        }
    var shadowRadius: Float
        get() = get("shadowRadius") as Float
        set(value) {
            set("shadowRadius", value)
        }
    var shadowMapSize: Vector2
        get() = get("shadowMapSize") as Vector2
        set(value) {
            shadowMapSize.copy(value)
        }
    var shadowCameraNear: Float
        get() = get("shadowCameraNear") as Float
        set(value) {
            set("shadowCameraNear", value)
        }
    var shadowCameraFar: Float
        get() = get("shadowCameraFar") as Float
        set(value) {
            set("shadowCameraFar", value)
        }

}

internal class HemisphereLightUniforms : LightUniforms() {

    init {
        putAll(
                mapOf(
                        "direction" to Vector3(),
                        "skyColor" to Color(),
                        "groundColor" to Color()
                )
        )
    }

    var direction: Vector3
        get() = get("direction") as Vector3
        set(value) {
            direction.copy(value)
        }
    var skyColor: Color
        get() = get("skyColor") as Color
        set(value) {
            skyColor.copy(value)
        }
    var groundColor: Color
        get() = get("groundColor") as Color
        set(value) {
            groundColor.copy(value)
        }

}

internal class RectAreaLightUniforms : LightUniforms() {

    init {
        putAll(
                mapOf(
                        "color" to Color(),
                        "position" to Vector3(),
                        "halfWidth" to Vector3(),
                        "halfHeight" to Vector3()
                )
        )
    }

    var color: Color
        get() = get("color") as Color
        set(value) {
            color.copy(value)
        }
    var position: Vector3
        get() = get("position") as Vector3
        set(value) {
            position.copy(value)
        }
    var halfWidth: Vector3
        get() = get("halfWidth") as Vector3
        set(value) {
            halfWidth.copy(value)
        }
    var halfHeight: Vector3
        get() = get("halfHeight") as Vector3
        set(value) {
            halfHeight.copy(value)
        }

}
