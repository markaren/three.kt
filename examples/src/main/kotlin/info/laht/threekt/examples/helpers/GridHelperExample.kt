package info.laht.threekt.examples.helpers

import info.laht.threekt.Canvas
import info.laht.threekt.CanvasOptions
import info.laht.threekt.DoubleSide
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.geometries.PlaneGeometry
import info.laht.threekt.helpers.GridHelper
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.DEG2RAD
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene

object GridHelperExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Canvas(CanvasOptions().apply {
            antialiasing = 4
        }).use { canvas ->

            val scene = Scene()

            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 1000).also {
                it.translateZ(10f).translateY(5f)
            }
            val renderer = GLRenderer(canvas)
            val controls = OrbitControls(camera, canvas)

            Mesh(PlaneGeometry(10f, 10f), MeshBasicMaterial().apply {
                color.set(Color.gray)
                side = DoubleSide
            }).also {
                it.rotation.x = DEG2RAD * -90
                it.translateZ(-1f)
                scene.add(it)
            }

            GridHelper().also {
                it.translateY(-0.99f)
                scene.add(it)
                camera.lookAt(it.position)
            }

            while (!canvas.shouldClose()) {

                renderer.render(scene, camera)

            }

        }

    }

}