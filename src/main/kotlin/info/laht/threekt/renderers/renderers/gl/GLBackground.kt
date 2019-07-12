package info.laht.threekt.renderers.renderers.gl

import info.laht.threekt.math.Color

class GLBackground(
    val state: GLState,
    val premultipliedAlpha: Boolean
) {

    var clearColor = Color.fromHex( 0x000000 )
    var clearAlpha = 0f

    fun setClear(color: Color, alpha: Float) {
        state.colorBuffer.setClear(color.r, color.g, color.b, alpha, premultipliedAlpha)
    }

}