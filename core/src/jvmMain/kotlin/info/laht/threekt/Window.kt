package info.laht.threekt

import info.laht.threekt.input.*
import org.lwjgl.BufferUtils.createByteBuffer
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.opengl.*
import org.lwjgl.system.Callback
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.awt.image.DataBufferInt
import java.io.Closeable
import java.nio.ByteBuffer
import java.util.concurrent.atomic.AtomicBoolean
import javax.imageio.ImageIO


private const val DEFAULT_WIDTH = 800
private const val DEFAULT_HEIGHT = 600

fun interface WindowClosingCallback {
    fun onWindowClosing()
}

class Window @JvmOverloads constructor(
        options: Options = Options()
) : AbstractPeripheralsEventSource(), Closeable {

    val hwnd: Long

    override val size: WindowSize = options.size

    val aspect: Float
        get() = size.aspect

    private val mouseEvent = MouseEvent()
    private var debugProc: Callback? = null

    private var windowResizeCallback: WindowResizeListener? = null

    var animating = AtomicBoolean(false)
    private var closed = AtomicBoolean(false)

    @JvmField
    var onCloseCallback: WindowClosingCallback? = null

    constructor(title: String? = null,
                width: Int? = null,
                height: Int? = null,
                antialias: Int? = null,
                vSync: Boolean? = null,
                resizeable: Boolean? = null,
                favicon: BufferedImage? = null
    ) : this(Options(title, WindowSize(width ?: DEFAULT_WIDTH, height
            ?: DEFAULT_HEIGHT), antialias, vSync, resizeable, favicon))

    init {

        val errorCallback = GLFWErrorCallback.createPrint(System.err)
        glfwSetErrorCallback(errorCallback)
        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }
        hwnd = createWindow(options)

        if (options.resizeable) {
            glfwSetWindowSizeCallback(hwnd) { _, width, height ->
                size.width = width
                size.height = height
                windowResizeCallback?.onWindowResize(width, height)
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

    fun onWindowResize(listener: WindowResizeListener) {
        this.windowResizeCallback = listener
    }

    fun enableDebugCallback() {
        debugProc = GLUtil.setupDebugMessageCallback()
    }

    override fun close() {
        if (!closed.getAndSet(true)) {
            glfwSetWindowShouldClose(hwnd, true)

            while (animating.get()) {
                Thread.sleep(1)
            }

            debugProc?.free()
            glfwTerminate()

            onCloseCallback?.onWindowClosing()
        }
    }

    inline fun animate(f: () -> Unit) {

        animating.set(true)

        while (!glfwWindowShouldClose(hwnd)) {

            f.invoke()

            glfwSwapBuffers(hwnd)
            glfwPollEvents()

        }

        animating.set(false)

    }

    fun animate(f: Runnable) {
        animate { f.run() }
    }

    private companion object {

        fun createWindow(options: Options): Long {

            val (width, height) = options.size

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

            // Set favicon
            val favicon = options.favicon
            if (favicon != null) {
                val rasterBuffer = favicon.raster.dataBuffer
                val buffer: ByteBuffer
                when (rasterBuffer) {
                    is DataBufferByte -> {
                        val pixels = rasterBuffer.data
                        buffer = createByteBuffer(pixels.size).put(pixels)
                    }
                    is DataBufferInt -> {
                        val pixels = rasterBuffer.data
                        buffer = createByteBuffer(pixels.size * 4)
                        for (pixel in pixels) {
                            buffer.put((pixel shr 16 and 0xFF).toByte())
                            buffer.put((pixel shr 8 and 0xFF).toByte())
                            buffer.put((pixel and 0xFF).toByte())
                            buffer.put((pixel shr 24 and 0xFF).toByte())
                        }
                    }
                    else -> throw IllegalStateException("Unhandled data buffer type.")
                }
                buffer.flip()

                val glfwImageBuffer = GLFWImage.create(1)
                val glfwImage = GLFWImage.create().set(favicon.width, favicon.height, buffer)
                glfwImageBuffer.put(0, glfwImage)
                glfwSetWindowIcon(hwnd, glfwImageBuffer)
            }

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
            size: WindowSize? = null,
            antialias: Int? = null,
            vsync: Boolean? = null,
            resizeable: Boolean? = null,
            favicon: BufferedImage? = null
    ) {

        var title = title ?: "three.kt"
        var size = size ?: WindowSize(DEFAULT_WIDTH, DEFAULT_HEIGHT)

        var antialias = antialias ?: 0
        var vsync = vsync ?: true
        var resizeable = resizeable ?: false

        var favicon = favicon ?: javaClass.getResourceAsStream("/images/favicon.bmp")?.let {
            ImageIO.read(it)
        }

    }

}


fun interface WindowResizeListener {

    fun onWindowResize(width: Int, height: Int)

}

private fun Int.toKeyAction(): KeyAction {
    return when (this) {
        GLFW_RELEASE -> KeyAction.RELEASE
        GLFW_PRESS -> KeyAction.PRESS
        GLFW_REPEAT -> KeyAction.REPEAT
        else -> throw IllegalArgumentException()
    }
}
