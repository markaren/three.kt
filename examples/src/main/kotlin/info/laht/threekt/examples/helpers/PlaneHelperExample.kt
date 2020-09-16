package info.laht.threekt.examples.helpers

import info.laht.threekt.Window
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.helpers.PlaneHelper
import info.laht.threekt.math.Plane
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene

fun main() {

    Window(antialias = 4).use { canvas ->

        val scene = Scene()

        val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 1000).also {
            it.position.set(2.5f, 2.5f, 5f)
        }
        val renderer = GLRenderer(canvas.size)
        OrbitControls(camera, canvas)

        val plane = Plane()
        PlaneHelper(plane, 5f, 0x00ff00).also {
            scene.add(it)
        }

        canvas.animate {

            renderer.render(scene, camera)

        }

    }

}
