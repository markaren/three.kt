package info.laht.threekt

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import java.io.Closeable
import org.lwjgl.glfw.GLFWErrorCallback


class Canvas(
    val width: Int = 800,
    val height: Int = 600,
    private val title: String = "Untitled"
): Closeable {

    internal val pointer: Long

    init {
        val errorCallback = GLFWErrorCallback.createPrint(System.err)
        glfwSetErrorCallback(errorCallback)
        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }
        pointer = createWindow()
    }

    fun shouldClose(): Boolean {
        return glfwWindowShouldClose(pointer)
    }

    override fun close() {
        glfwTerminate()
    }

    private fun createWindow(): Long {
        // In order to see anything, we create a new pointer using GLFW's glfwCreateWindow().
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
        val window = glfwCreateWindow(width, height, title, 0, 0)

        // Tell GLFW to make the OpenGL context current so that we can make OpenGL calls.
        glfwMakeContextCurrent(window)

        // Tell LWJGL 3 that an OpenGL context is current in this thread. This will result in LWJGL 3 querying function
        // pointers for various OpenGL functions.
        GL.createCapabilities()

        // Return the handle to the created pointer.
        return window
    }

}
