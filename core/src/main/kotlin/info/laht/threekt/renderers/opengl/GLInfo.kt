package info.laht.threekt.renderers.opengl

import org.lwjgl.opengl.GL11

internal class GLInfo {

    val memory = Memory()
    val render = Render()

    var autoReset = true

    fun update(count: Int, mode: Int, instanceCount: Int = 1) {
        render.calls++

        when (mode) {
            GL11.GL_TRIANGLES -> render.triangles += instanceCount * (count * 3)
            GL11.GL_TRIANGLE_STRIP, GL11.GL_TRIANGLE_FAN -> render.triangles += instanceCount * (count - 2)
            GL11.GL_LINES -> render.lines += instanceCount * (count / 2)
            GL11.GL_LINE_STRIP -> render.lines += instanceCount * (count - 1)
            GL11.GL_LINE_LOOP -> render.lines += instanceCount * count
            GL11.GL_POINTS -> render.points += instanceCount * count
            else -> TODO()
        }

    }

    fun reset() {
        render.frame++
        render.calls = 0
        render.triangles = 0
        render.points = 0
        render.lines = 0
    }

    inner class Memory {
        var geometries = 0
            internal set
        var textures = 0
            internal set
    }

    inner class Render {
        var frame = 0
            internal set
        var calls = 0
            internal set
        var triangles = 0
            internal set
        var points = 0
            internal set
        var lines = 0
            internal set
    }

}