package info.laht.threekt.examples.javafx

import info.laht.threekt.WindowSize
import info.laht.threekt.input.AbstractPeripheralsEventSource
import org.eclipse.fx.drift.*
import org.eclipse.fx.drift.internal.GL
import org.lwjgl.opengl.*
import org.lwjgl.system.Callback
import java.io.Closeable
import java.nio.ByteBuffer

class DriftFxSurfaceRenderer(surface: DriftFXSurface): AbstractPeripheralsEventSource(), Closeable {

    private val driftFxRenderer = GLRenderer.getRenderer(surface)

    private val curWidth = 0
    private val curHeight = 0
    private var fbo = 0
    private var depthTex = 0

    private val context = GL.createContext(0, 3, 2)

    override val size = WindowSize(surface.width.toInt(), surface.height.toInt())

    val aspect: Float
        get() = size.aspect

    private var debugProc: Callback? = null

    private var windowResizeCallback: ((Int, Int) -> Unit)? = null

    @JvmField
    var onCloseCallback: (() -> Unit)? = null

    private var running = true

    fun initialize() {
        // make context current
        GL.makeContextCurrent(context)
        org.lwjgl.opengl.GL.createCapabilities()

        // threekt defaults
        GL11.glEnable(GL32.GL_PROGRAM_POINT_SIZE)
        GL11.glEnable(GL20.GL_POINT_SPRITE)

        fbo = GL30.glGenFramebuffers()
        depthTex = GL11.glGenTextures()
    }

    private fun bind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo)
    }

    private fun unbind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
    }

    private fun dispose() {
        GL30.glDeleteFramebuffers(fbo)
        GL11.glDeleteTextures(depthTex)
        debugProc?.free()
        org.lwjgl.opengl.GL.setCapabilities(null)
        GL.destroyContext(context)
    }

    private fun updateDepthTexSize(size: Vec2i) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTex)
        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D,
            0,
            GL30.GL_DEPTH_COMPONENT32F,
            size.x,
            size.y,
            0,
            GL11.GL_DEPTH_COMPONENT,
            GL11.GL_FLOAT,
            null as ByteBuffer?
        )
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
    }

    private fun updateFBO(size: Vec2i, target: RenderTarget?) {
        val targetTex = GLRenderer.getGLTextureId(target)
        if (size.x != curWidth || size.y != curHeight) {
            updateDepthTexSize(size)
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo)
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, targetTex, 0)
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depthTex, 0)
    }

    fun onWindowResize(listener: (Int, Int) -> Unit) {
        windowResizeCallback = listener
    }

    fun enableDebugCallback() {
        debugProc = GLUtil.setupDebugMessageCallback()
    }

    fun animate(unit: () -> Unit) {
        var swapChain: Swapchain? = null
        while(running) {
            val rendererSize = driftFxRenderer.size
            val swapChainSize = swapChain?.config?.size
            val resized = swapChain == null || rendererSize.x != swapChainSize?.x || rendererSize.y != swapChainSize.y
            if (swapChain == null || swapChainSize == null || resized) {
                swapChain?.dispose()
                try {
                    swapChain = driftFxRenderer.createSwapchain(
                        SwapchainConfig(
                            driftFxRenderer.size,
                            2,
                            PresentationMode.MAILBOX,
                            StandardTransferTypes.MainMemory
                        )
                    )
                } catch (e: Exception) {
                    System.err.println("swapchain creation failed! " + e.message)
                    e.printStackTrace(System.err)
                }
            }

            swapChain ?: continue

            val renderTarget = swapChain.acquire()

            updateFBO(swapChain.config.size, renderTarget)

            if (resized) {
                windowResizeCallback?.invoke(swapChain.config.size.x, swapChain.config.size.y)
            }

            bind()

            unit()

            unbind()
            swapChain.present(renderTarget)
            Thread.sleep(0)
        }
        onCloseCallback?.invoke()
        swapChain?.dispose()
        dispose()
    }

    override fun close() {
        running = false
    }

}
