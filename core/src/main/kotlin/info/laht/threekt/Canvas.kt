package info.laht.threekt

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import java.io.Closeable
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose
import org.lwjgl.glfw.GLFW.GLFW_RELEASE
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import java.awt.SystemColor.window
import org.lwjgl.glfw.GLFW.glfwSetKeyCallback


class Canvas(
    val width: Int = 800,
    val height: Int = 600,
    private val title: String = "Untitled"
): Closeable {

    private val pointer: Long

    val aspect: Float
        get() = width.toFloat() / height

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
        // In order to see anything, we createShader a new pointer using GLFW's glfwCreateWindow().
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
        val window = glfwCreateWindow(width, height, title, 0, 0)

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window) { window, key, _, action, _ ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true) // We will detect this in the rendering loop
        }

        // Tell GLFW to make the OpenGL context current so that we can make OpenGL calls.
        glfwMakeContextCurrent(window)

        // Enable v-sync
        glfwSwapInterval(1);

        // Tell LWJGL 3 that an OpenGL context is current in this thread. This will result in LWJGL 3 querying function
        // pointers for various OpenGL functions.
        GL.createCapabilities()

        // Return the handle to the created pointer.
        return window
    }

    internal fun tick() {
        glfwSwapBuffers(pointer)
        glfwPollEvents()
    }

}
