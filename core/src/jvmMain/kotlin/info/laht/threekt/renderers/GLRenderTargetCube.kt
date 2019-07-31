package info.laht.threekt.renderers

import info.laht.threekt.scenes.Scene
import info.laht.threekt.textures.Texture

class GLRenderTargetCube(
    width: Int,
    height: Int,
    options: Options? = null
) : GLRenderTarget(width, height, options) {

    fun fromEquirectangularTexture(renderer: GLRenderer, texture: Texture) {

        val scene = Scene()

        TODO()
    }

}
