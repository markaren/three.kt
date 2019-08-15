package info.laht.threekt.examples.javafx

import com.sun.prism.es2.JFXGLContext
import cuchaz.jfxgl.CalledByEventsThread
import cuchaz.jfxgl.CalledByMainThread
import cuchaz.jfxgl.JFXGL
import cuchaz.jfxgl.JFXGLLauncher
import cuchaz.jfxgl.controls.OpenGLPane
import info.laht.threekt.Canvas
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.core.Clock
import info.laht.threekt.geometries.BoxBufferGeometry
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.CheckBox
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import java.io.IOException

fun main(args: Array<String>) {
    JFXGLLauncher.launchMain(HelloJavaFX.javaClass, args)
}

object HelloJavaFX : Application() {

    private lateinit var mesh: Mesh

    @JvmStatic
    fun jfxglmain(args: Array<String>) {

        Canvas(antialias = 4).use { canvas ->

            val scene = Scene().apply {
                setBackground(Color.aliceblue)
            }
            val camera = PerspectiveCamera().apply {
                position.z = 10f
            }
            val renderer = GLRenderer(canvas.width, canvas.height)

            mesh = Mesh(BoxBufferGeometry(1f), MeshBasicMaterial().apply {
                color.set(0x00ff00)
            })
            scene.add(mesh)

            OrbitControls(camera, canvas)

            val clock = Clock()
            JFXGL.start(canvas.hwnd, args, HelloJavaFX)
            canvas.animate {

                mesh.rotation.y += 1f * clock.getDelta()

                renderer.render(scene, camera)
                JFXGL.render()

            }

            JFXGL.terminate()

        }


    }

    @CalledByEventsThread
    @Throws(IOException::class)
    override fun start(stage: Stage) {

        val border = BorderPane()
        stage.scene = javafx.scene.Scene(border)

        val checkBox = CheckBox("Show mesh")
        checkBox.onAction = EventHandler<ActionEvent> { event ->
            mesh.visible = (event.source as CheckBox).isSelected
        }
        checkBox.isSelected = mesh.visible

        border.left = checkBox


        val glPane = OpenGLPane().apply {
            setRenderer { context -> render(context) }
        }

        border.center = glPane

    }

    @CalledByMainThread
    private fun render(context: JFXGLContext) {
    }

}
