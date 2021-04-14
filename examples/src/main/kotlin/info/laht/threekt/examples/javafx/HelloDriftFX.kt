package info.laht.threekt.examples.javafx

import info.laht.threekt.Side
import info.laht.threekt.WindowSize
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.Clock
import info.laht.threekt.geometries.BoxBufferGeometry
import info.laht.threekt.geometries.CylinderBufferGeometry
import info.laht.threekt.geometries.PlaneBufferGeometry
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.DEG2RAD
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.control.CheckBox
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.eclipse.fx.drift.DriftFXSurface

fun main() {
    Application.launch(HelloDriftFX::class.java)
}

class HelloDriftFX : Application() {

    private val driftFxSurface1 = DriftFXSurface()
    private val driftFxSurfaceRenderer1 = DriftFxSurfaceRenderer(driftFxSurface1)

    private val driftFxSurface2 = DriftFXSurface()
    private val driftFxSurfaceRenderer2 = DriftFxSurfaceRenderer(driftFxSurface2)

    private val mesh1: Mesh
    private val mesh2: Mesh

    init {
        driftFxSurface1.prefWidth = 300.0
        driftFxSurface1.prefHeight = 200.0

        driftFxSurface2.prefWidth = 400.0
        driftFxSurface2.prefHeight = 200.0

        mesh1 = Mesh(BoxBufferGeometry(1f), MeshBasicMaterial().apply {
            color.set(0x00ff00)
        })

        mesh2 = Mesh(CylinderBufferGeometry(), MeshBasicMaterial().apply {
            color.set(0x0000ff)
        })
    }

    override fun start(stage: Stage) {
        val border = BorderPane()
        stage.scene = javafx.scene.Scene(border, 800.0, 500.0)

        val checkBox1 = CheckBox("Show cube")
        checkBox1.onAction = EventHandler { event ->
            mesh1.visible = (event.source as CheckBox).isSelected
        }
        checkBox1.isSelected = mesh1.visible

        val checkBox2 = CheckBox("Show cylinder")
        checkBox2.onAction = EventHandler { event ->
            mesh2.visible = (event.source as CheckBox).isSelected
        }
        checkBox2.isSelected = mesh1.visible

        border.left = VBox(checkBox2, checkBox1)
        border.center = driftFxSurface2
        border.right = driftFxSurface1

        stage.setOnCloseRequest {
            driftFxSurfaceRenderer1.close()
            driftFxSurfaceRenderer2.close()
        }

        stage.show()

        // render multiple surfaces :)

        Thread {
            driftFxSurfaceRenderer1.initialize()
            renderThree1()
        }.start()

        Thread {
            driftFxSurfaceRenderer2.initialize()
            renderThree2()
        }.start()
    }

    private fun renderThree1() {
        val scene = Scene().apply {
            setBackground(Color.aliceblue)
            add(mesh1)
        }
        val camera = PerspectiveCamera().apply {
            position.z = 10f
        }

        Mesh(PlaneBufferGeometry(10f), MeshBasicMaterial().apply {
            color.set(Color.gray)
            side = Side.Double
        }).also {
            it.rotation.x = DEG2RAD * -90
            it.translateZ(-1f)
            scene.add(it)
        }

        val renderer = GLRenderer(WindowSize(driftFxSurface1.width.toInt(), driftFxSurface1.height.toInt()))

        val clock = Clock()
        driftFxSurfaceRenderer1.animate {
            mesh1.rotation.y += 1f * clock.getDelta()

            renderer.render(scene, camera)
        }
    }

    private fun renderThree2() {
        val scene = Scene().apply {
            setBackground(Color.orangered)
            add(mesh2)
        }
        val camera = PerspectiveCamera().apply {
            position.z = 10f
        }

        Mesh(PlaneBufferGeometry(10f), MeshBasicMaterial().apply {
            color.set(Color.green)
            side = Side.Double
        }).also {
            it.rotation.x = DEG2RAD * -90
            it.translateZ(-1f)
            scene.add(it)
        }

        val renderer = GLRenderer(WindowSize(driftFxSurface2.width.toInt(), driftFxSurface2.height.toInt()))

        val clock = Clock()
        driftFxSurfaceRenderer2.animate {
            mesh2.rotation.y += 1f * clock.getDelta()

            renderer.render(scene, camera)
        }
    }

}
