package info.laht.threekt.renderers.opengl

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.InstancedBufferGeometry
import info.laht.threekt.core.IntBufferAttribute
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL31

class GLIndexedBufferRenderer internal constructor(
    private val info: GLInfo,
    private val capabilities: GLCapabilities
) {

    var mode: Int = 0
    var type: Int = 0
    var bytesPerElement: Int = 0

    fun setIndex( value: Any ) {
        TODO()
    }

    fun render( start: Int, count: Int ) {

        GL11.glDrawElements(mode, count, type, (start * bytesPerElement).toLong())
        info.update(count, mode)

    }

    fun renderInstances ( geometry: InstancedBufferGeometry, start: Int, count: Int ) {
        GL31.glDrawElementsInstanced(mode, count, type, (start * bytesPerElement).toLong(), geometry.maxInstancedCount)
    }


}