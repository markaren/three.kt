package info.laht.threekt.renderers

import info.laht.threekt.cameras.Camera
import info.laht.threekt.scenes.Scene
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import java.io.Closeable


class Renderer : Closeable {

    private val window: Long

    init {
        glfwInit()
        window = createWindow()
    }

    @JvmOverloads
    fun clear(color: Boolean = true, depth: Boolean = true, stencil: Boolean = true) {
        var bits = 0
        if (color) bits = bits or GL_COLOR_BUFFER_BIT
        if (depth) bits = bits or GL_DEPTH_BUFFER_BIT
        if (stencil) bits = bits or GL_STENCIL_BUFFER_BIT
        glClear(bits)
    }

    fun render(scene: Scene, camera: Camera) {

        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            glDrawArrays(GL_TRIANGLES, 0, 3);
            glfwSwapBuffers(window);
        }

    }

    override fun close() {
        glfwTerminate()
    }

    private companion object {

        private fun createWindow(): Long {
            // In order to see anything, we create a new window using GLFW's glfwCreateWindow().
            glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
            val window = glfwCreateWindow(800, 600, "Intro2", 0, 0)

            // Tell GLFW to make the OpenGL context current so that we can make OpenGL calls.
            glfwMakeContextCurrent(window)

            // Tell LWJGL 3 that an OpenGL context is current in this thread. This will result in LWJGL 3 querying function
            // pointers for various OpenGL functions.
            GL.createCapabilities()

            // Return the handle to the created window.
            return window
        }

    }

}