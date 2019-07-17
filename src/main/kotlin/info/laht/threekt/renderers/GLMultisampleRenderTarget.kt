package info.laht.threekt.renderers

class GLMultisampleRenderTarget(
    width: Int,
    height: Int,
    options: Options? = null
) : GLRenderTarget(width, height, options) {

    var samples = 4

    fun copy(source: GLMultisampleRenderTarget): GLMultisampleRenderTarget {
        super.copy(source)
        samples = source.samples
        return this
    }

}