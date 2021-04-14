package info.laht.threekt.examples.javafx

import info.laht.threekt.Side
import info.laht.threekt.WindowSize
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.Clock
import info.laht.threekt.geometries.BoxBufferGeometry
import info.laht.threekt.geometries.CylinderBufferGeometry
import info.laht.threekt.geometries.PlaneBufferGeometry
import info.laht.threekt.geometries.SphereBufferGeometry
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.DEG2RAD
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
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

        val button = Button("Open new window")
        button.setOnAction {
            openNewWindow()
        }

        border.left = VBox(checkBox2, checkBox1, button)

        val pane1 = BorderPane(driftFxSurface1)
        pane1.setPrefSize(300.0, 300.0)
        val pane2 = BorderPane(driftFxSurface2)
        pane2.setPrefSize(300.0, 300.0)
        border.center = HBox(pane1, pane2)

        stage.setOnCloseRequest {
            driftFxSurfaceRenderer1.close()
            driftFxSurfaceRenderer2.close()
        }

        stage.show()

        // render multiple surfaces :)

        Thread {
            driftFxSurfaceRenderer1.initialize()
            driftFxSurfaceRenderer1.enableDebugCallback()
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

        driftFxSurfaceRenderer2.onCloseCallback = {
            renderer.dispose()
        }

        driftFxSurfaceRenderer2.animate {
            mesh2.rotation.y += 1f * clock.getDelta()

            renderer.render(scene, camera)
        }
    }

    private fun openNewWindow() {
        val driftFxSurface3 = DriftFXSurface()
        val driftFxSurfaceRenderer3 = DriftFxSurfaceRenderer(driftFxSurface3)

        val stage = Stage()
        val root = BorderPane(driftFxSurface3)
        stage.scene = javafx.scene.Scene(root, 400.0, 300.0)
        stage.setOnCloseRequest {
            driftFxSurfaceRenderer3.close()
        }
        stage.show()

        Thread {
            driftFxSurfaceRenderer3.initialize()

            val scene = Scene().apply {
                setBackground(Color.brown)
            }
            val camera = PerspectiveCamera().apply {
                position.z = 10f
            }

            val geometry = SphereBufferGeometry(2)
            val material = MeshBasicMaterial().apply {
                color.set(0xff0000)
                wireframe = true
            }

            val sphere = Mesh(geometry, material)
            scene.add(sphere)

            val renderer = GLRenderer(WindowSize(driftFxSurface3.width.toInt(), driftFxSurface3.height.toInt()))

            val clock = Clock()

            driftFxSurfaceRenderer3.animate {
                sphere.rotation.y += 0.5f * clock.getDelta()

                renderer.render(scene, camera)
            }
        }.start()
    }

}
