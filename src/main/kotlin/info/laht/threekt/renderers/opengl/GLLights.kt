package info.laht.threekt.renderers.opengl

import info.laht.threekt.cameras.Camera
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.lights.*
import info.laht.threekt.math.*
import info.laht.threekt.textures.Texture


class GLLights {

    private val cache = UniformsCache()

    internal val state = GLLightsState()

    private var vector3 = Vector3()
    private var matrix4 = Matrix4()
    private var matrix42 = Matrix4()

    fun setup(lights: List<Light>, shadows: List<LightShadow>, camera: Camera) {

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

            when (light) {
                is AmbientLight -> {
                    r += color.r * intensity
                    g += color.g * intensity
                    b += color.b * intensity
                }
                is LightProbe -> {
                    for (i in 0 until 9) {
                        state.probe[ i ].addScaledVector( light.sh.coefficients[ i ], intensity )
                    }
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

                    val shadowMap = light.shadow.map?.texture

                    state.pointShadowMap[pointLength] = shadowMap
                    state.pointShadowMatrix[pointLength] = light.shadow.matrix
                    state.point[pointLength] = uniforms

                    pointLength++
                }
            }

        }

        state.ambient[0] = r
        state.ambient[1] = g
        state.ambient[2] = b

        val hash = state.hash

        if (hash.directionalLength != directionalLength ||
            hash.pointLength != pointLength ||
            hash.spotLength != spotLength ||
            hash.rectAreaLength != rectAreaLength ||
            hash.hemiLength != hemiLength ||
            hash.shadowsLength != shadows.size
        ) {

//            state.directional.length = directionalLength
//            state.spot.length = spotLength
//            state.rectArea.length = rectAreaLength
//            state.point.length = pointLength
//            state.hemi.length = hemiLength

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

    inner class GLLightsState {

        var version = 0

        var hash = Hash()

        val ambient = floatArrayOf(0f, 0f, 0f)
        val probe = Array(9) { Vector3() }
        val directional = mutableListOf<Any>()
        val directionalShadowMap = mutableListOf<Texture?>()
        val directionalShadowMatrix = mutableListOf<Matrix4>()
        val spot = mutableListOf<Any>()
        val spotShadowMap = mutableListOf<Texture?>()
        val spotShadowMatrix = mutableListOf<Matrix4>()
        val rectArea = mutableListOf<Any>()
        val point = mutableListOf<Any>()
        val pointShadowMap = mutableListOf<Texture?>()
        val pointShadowMatrix = mutableListOf<Matrix4>()
        val hemi = mutableListOf<Any>()

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
                    is PointLight -> PointLightUniforms()
                    else -> throw IllegalArgumentException("")
                }

            }

        }

    }

}

private sealed class LightUniforms

private class AmbientLightUniforms : LightUniforms() {
    val direction = Vector3()
    val color = Color()

    var shadow = false
    var shadowBias = 0f
    var shadowRadius = 1f
    val shadowMapSize = Vector2i()
}

private class PointLightUniforms : LightUniforms() {
    val position = Vector3()
    val color = Color()
    var distance = 0f
    var decay = 0f

    var shadow = false
    var shadowBias = 0f
    var shadowRadius = 1f
    var shadowMapSize = Vector2i()
    var shadowCameraNear = 1f
    var shadowCameraFar = 1000f
}
