package info.laht.threekt.renderers.opengl

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.Object3D
import info.laht.threekt.lights.Light

internal class GLRenderState {

    internal val lights = GLLights()

    internal val lightsArray = mutableListOf<Light>()
    internal val shadowsArray = mutableListOf<Object3D>()

    fun init() {
        lightsArray.clear()
        shadowsArray.clear()
    }

    fun pushLight(light: Light) {
        lightsArray.add(light)
    }

    fun pushShadow(shadow: Object3D) {
        shadowsArray.add(shadow)
    }

    fun setupLights(camera: Camera) {
        lights.setup(lightsArray, shadowsArray, camera)
    }

}
