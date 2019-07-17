package info.laht.threekt.renderers.opengl

import info.laht.threekt.cameras.Camera
import info.laht.threekt.lights.AmbientLight
import info.laht.threekt.lights.Light
import info.laht.threekt.lights.LightShadow
import info.laht.threekt.lights.PointLight
import info.laht.threekt.math.*

private typealias Uniforms = Map<String, Any>

class GLLights {

    private val cache = UniformsCache()

    internal val state = State()

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

        lights.forEach { light ->

            val color = light.color
            val intensity = light.intensity

            when (light) {
                is AmbientLight -> {
                    r += color.r * intensity
                    g += color.g * intensity
                    b += color.b * intensity
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

//            state.directional.length = directionalLength;
//            state.spot.length = spotLength;
//            state.rectArea.length = rectAreaLength;
//            state.point.length = pointLength;
//            state.hemi.length = hemiLength;

            hash.directionalLength = directionalLength;
            hash.pointLength = pointLength;
            hash.spotLength = spotLength;
            hash.rectAreaLength = rectAreaLength;
            hash.hemiLength = hemiLength;
            hash.shadowsLength = shadows.size;

            state.version = nextVersion++;

        }

    }

    private companion object {

        var nextVersion = 0

    }

    internal inner class State {

        var version = 0

        var hash = Hash()

        var ambient = floatArrayOf(0f, 0f, 0f)
        var probe = Array(9) { Vector3() }

        internal inner class Hash {
            var directionalLength = -1
            var pointLength = -1
            var spotLength = -1
            var rectAreaLength = -1
            var hemiLength = -1
            var shadowsLength = -1
        }

    }

    internal inner class UniformsCache {

        private val map = mutableMapOf<Int, Uniforms>()

        fun get(light: Light): Uniforms? {

            return map.computeIfAbsent(light.id) {

                when (light) {
                    is AmbientLight -> mapOf(
                        "direction" to Vector3(),
                        "color" to Color(),

                        "shadow" to false,
                        "shadowBias" to 0,
                        "shadowRadius" to 1f,
                        "shadowMapSize" to Vector2i()
                    )
                    is PointLight -> mapOf(
                        TODO()
                    )
                }

            }

        }

    }

}
