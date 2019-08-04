package info.laht.threekt

import info.laht.threekt.input.KeyAction
import info.laht.threekt.input.KeyEvent
import info.laht.threekt.input.MouseEvent
import info.laht.threekt.input.MouseWheelEvent
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.*
import org.lwjgl.system.Callback
import java.io.Closeable

class Canvas @JvmOverloads constructor(
    options: Options = Options()
) : Closeable {

    val width: Int = options.width
    val height: Int = options.height

    val ___pointer___: Long

    val aspect: Float
        get() = width.toFloat() / height

    var onKeyPressed: ((KeyEvent) -> Unit)? = null
    var onMouseWheel: ((MouseWheelEvent) -> Unit)? = null
    var onMouseDown: ((MouseEvent) -> Unit)? = null
    var onMouseUp: ((MouseEvent) -> Unit)? = null
    var onMouseMove: ((MouseEvent) -> Unit)? = null

    private val mouseEvent = MouseEvent()

    private var debugProc: Callback? = null

    init {
        val errorCallback = GLFWErrorCallback.createPrint(System.err)
        glfwSetErrorCallback(errorCallback)
        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }
        ___pointer___ = createWindow(options)
    }

    fun enableDebugCallback() {
        debugProc = GLUtil.setupDebugMessageCallback()
    }

    override fun close() {
        debugProc?.free()
        glfwTerminate()
    }

    private fun createWindow(options: Options): Long {

        if (options.antialiasing > 0) {
            glfwWindowHint(GLFW_SAMPLES, options.antialiasing)
        }

        // In order to see anything, we createShader a new pointer using GLFW's glfwCreateWindow().
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
        val window = glfwCreateWindow(width, height, options.title, 0, 0)

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window) { _, key, _, action, _ ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true) // We will detect this in the rendering loop
            } else {
                onKeyPressed?.invoke(KeyEvent(key, action.toKeyAction()))
            }
        }

        glfwSetMouseButtonCallback(window) { _, button, action, _ ->
            mouseEvent.button = button
            when (action) {
                0 -> onMouseUp?.invoke(mouseEvent)
                1 -> onMouseDown?.invoke(mouseEvent)
            }
        }

        glfwSetCursorPosCallback(window) { _, xpos, ypos ->
            mouseEvent.updateCoordinates(xpos.toInt(), ypos.toInt())
            onMouseMove?.invoke(mouseEvent)
        }

        glfwSetScrollCallback(window) { _, xoffset, yoffset ->
            onMouseWheel?.invoke(MouseWheelEvent(xoffset.toFloat(), yoffset.toFloat()))
        }

        // Tell GLFW to make the OpenGL context current so that we can make OpenGL calls.
        glfwMakeContextCurrent(window)

        if (options.vsync) glfwSwapInterval(1) else glfwSwapInterval(0)

        // Tell LWJGL 3 that an OpenGL context is current in this thread. This will result in LWJGL 3 querying function
        // pointers for various OpenGL functions.
        GL.createCapabilities()

        // required for various point stuff to work
        GL11.glEnable(GL32.GL_PROGRAM_POINT_SIZE)
        GL11.glEnable(GL20.GL_POINT_SPRITE)

        // Return the handle to the created pointer.
        return window
    }

    fun requestAnimationFrame(f: () -> Unit) {

        if (!glfwWindowShouldClose(___pointer___)) {

            glfwSwapBuffers(___pointer___)
            glfwPollEvents()

            f.invoke()

        }

    }

    class Options(
        var width: Int = 800,
        var height: Int = 600,

        var antialiasing: Int = 0,
        var vsync: Boolean = true,

        var title: String = "Three.kt"
    )


}

private fun Int.toKeyAction(): KeyAction {
    return when (this) {
        GLFW_RELEASE -> KeyAction.RELEASE
        GLFW_PRESS -> KeyAction.PRESS
        GLFW_REPEAT -> KeyAction.REPEAT
        else -> throw IllegalArgumentException()
    }
}
