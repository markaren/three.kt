package info.laht.threekt

import info.laht.threekt.input.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.*
import org.lwjgl.system.Callback
import java.io.Closeable


class Canvas @JvmOverloads constructor(
        options: Options = Options()
) : AbstractPeripheralsEventSource(), Closeable {

    val hwnd: Long

    override val width: Int = options.width
    override val height: Int = options.height

    val aspect: Float
        get() = width.toFloat() / height

    private val mouseEvent = MouseEvent()
    private var debugProc: Callback? = null

    var onWindowResize: ((Int, Int) -> Unit)? = null

    constructor(title: String? = null,
                width: Int? = null,
                height: Int? = null,
                antialias: Int? = null,
                vsync: Boolean? = null,
                resizeable: Boolean? = null
    ) : this(Options(title, width, height, antialias, vsync, resizeable))


    init {

        val errorCallback = GLFWErrorCallback.createPrint(System.err)
        glfwSetErrorCallback(errorCallback)
        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }
        hwnd = createWindow(options)

        if (options.resizeable) {
            glfwSetWindowSizeCallback(hwnd) { _, width, height ->
                onWindowResize?.invoke(width, height)
            }
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(hwnd) { _, key, _, action, _ ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(hwnd, true) // We will detect this in the rendering loop
            } else {
                keyListeners?.also { listeners ->
                    val evt = KeyEvent(key, action.toKeyAction())
                    listeners.forEach {
                        it.onKeyPressed(evt)
                    }
                }
            }
        }

        glfwSetMouseButtonCallback(hwnd) { _, button, action, _ ->
            mouseEvent.button = button
            val listeners = mouseListeners?.toMutableList() // avoid concurrent modification exception
            when (action) {
                0 -> listeners?.forEach { it.onMouseUp(mouseEvent) }
                1 -> listeners?.forEach { it.onMouseDown(mouseEvent) }
            }
        }

        glfwSetCursorPosCallback(hwnd) { _, xpos, ypos ->
            mouseEvent.updateCoordinates(xpos.toInt(), ypos.toInt())
            mouseListeners?.forEach { it.onMouseMove(mouseEvent) }

        }

        glfwSetScrollCallback(hwnd) { _, xoffset, yoffset ->
            mouseListeners?.also { listeners ->
                val evt = MouseWheelEvent(xoffset.toFloat(), yoffset.toFloat())
                listeners.forEach { it.onMouseWheel(evt) }
            }
        }

    }

    fun enableDebugCallback() {
        debugProc = GLUtil.setupDebugMessageCallback()
    }

    override fun close() {
        debugProc?.free()
        glfwTerminate()
    }

    fun shouldClose(): Boolean {
        return glfwWindowShouldClose(hwnd)
    }

    fun swapBuffers() {
        glfwSwapBuffers(hwnd)
    }

    fun pollEvents() {
        glfwPollEvents()
    }

    inline fun animate(f: () -> Unit) {

        while (!shouldClose()) {

            f.invoke()

            swapBuffers()
            pollEvents()

        }

    }

    private companion object {

        fun createWindow(options: Options): Long {

            val width = options.width
            val height = options.height

            if (options.antialias > 0) {
                glfwWindowHint(GLFW_SAMPLES, options.antialias)
            }

            // In order to see anything, we createShader a new pointer using GLFW's glfwCreateWindow().
            glfwWindowHint(GLFW_RESIZABLE, if (options.resizeable) GLFW_TRUE else GLFW_FALSE)
            val hwnd = glfwCreateWindow(width, height, options.title, 0, 0)

            // Get the resolution of the primary monitor
            val vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())!!

            // Center the window
            glfwSetWindowPos(
                    hwnd,
                    (vidMode.width() - width) / 2,
                    (vidMode.height() - height) / 2
            )

            // Tell GLFW to make the OpenGL context current so that we can make OpenGL calls.
            glfwMakeContextCurrent(hwnd)

            if (options.vsync) glfwSwapInterval(1) else glfwSwapInterval(0)

            // Tell LWJGL 3 that an OpenGL context is current in this thread. This will result in LWJGL 3 querying function
            // pointers for various OpenGL functions.
            GL.createCapabilities()

            // required for various point stuff to work
            GL11.glEnable(GL32.GL_PROGRAM_POINT_SIZE)
            GL11.glEnable(GL20.GL_POINT_SPRITE)

            // Return the handle to the created pointer.
            return hwnd
        }

    }

    class Options(
            title: String? = null,
            width: Int? = null,
            height: Int? = null,
            antialias: Int? = null,
            vsync: Boolean? = null,
            resizeable: Boolean? = null
    ) {

        val title = title ?: "three.kt"

        val width = width ?: 800
        val height = height ?: 600

        val antialias = antialias ?: 0
        val vsync = vsync ?: true
        val resizeable = resizeable ?: false

    }

}

private fun Int.toKeyAction(): KeyAction {
    return when (this) {
        GLFW_RELEASE -> KeyAction.RELEASE
        GLFW_PRESS -> KeyAction.PRESS
        GLFW_REPEAT -> KeyAction.REPEAT
        else -> throw IllegalArgumentException()
    }
}
