package info.laht.threekt.renderers.opengl

import org.lwjgl.opengl.GL11

class GLBufferRenderer internal constructor (
    private val info: GLInfo,
    private val capabilities: GLCapabilities
) {

    var mode = 0

    fun render(start: Int, count: Int) {
        GL11.glDrawArrays(mode, start, count)
        info.update(count, mode)
    }

}
