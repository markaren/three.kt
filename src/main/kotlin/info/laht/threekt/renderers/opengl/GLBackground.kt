package info.laht.threekt.renderers.opengl

import info.laht.threekt.cameras.Camera
import info.laht.threekt.math.Color
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene

class GLBackground internal constructor (
    val renderer: GLRenderer,
    val state: GLState,
    val premultipliedAlpha: Boolean
) {

    val clearColor = Color.fromHex( 0x000000 )
    var clearAlpha = 0f
        set(value) {
            field = value
            setClear(clearColor, clearAlpha)
        }

    private var planeMesh: Mesh? = null
    private var boxMesh: Mesh? = null

    private var currentBackground: Nothing? = null
    private var currentBackgroundVersion = 0

    fun render(renderList: GLRenderList, scene: Scene, camera: Camera, forceClear: Boolean) {
        val background = scene.background

        if (background == null) {
            setClear( clearColor, clearAlpha )
            currentBackground = null
            currentBackgroundVersion = 0
        }

        if ( renderer.autoClear || forceClear ) {
            renderer.clear( renderer.autoClearColor, renderer.autoClearDepth, renderer.autoClearStencil );
        }

    }

    private fun setClear(color: Color, alpha: Float) {
        state.colorBuffer.setClear(color.r, color.g, color.b, alpha, premultipliedAlpha)
    }

    fun setClearColor(color: Color, alpha: Float = 1f) {
        clearColor.set(color)
        clearAlpha = alpha
        setClear( clearColor, clearAlpha )
    }

}
