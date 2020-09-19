package info.laht.threekt.examples.helpers

import info.laht.threekt.Window
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.helpers.ArrowHelper
import info.laht.threekt.math.Color
import info.laht.threekt.math.Vector3
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene

fun main() {

    Window(antialias = 4).use { canvas ->

        val scene = Scene()

        val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 1000).also {
            it.translateZ(1f)
        }
        val renderer = GLRenderer(canvas.size)
        OrbitControls(camera, canvas)

        ArrowHelper(
                length = 0.5f,
                dir = Vector3.X,
                color = Color.orange
        ).also {
            scene.add(it)
            scene.add(it.clone().setDirection(Vector3.Z))
            scene.add(it.clone().setDirection(Vector3.Y))
        }

        canvas.animate {

            renderer.render(scene, camera)

        }

    }

}
