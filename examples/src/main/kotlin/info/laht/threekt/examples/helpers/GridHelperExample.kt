package info.laht.threekt.examples.helpers

import info.laht.threekt.Canvas
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.helpers.AxesHelper
import info.laht.threekt.helpers.GridHelper
import info.laht.threekt.math.Color
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene

object GridHelperExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Canvas(Canvas.Options().apply {
            antialiasing = 4
        }).use { canvas ->

            val scene = Scene()

            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 1000).also {
                it.translateZ(10f).translateY(5f)
            }
            val renderer = GLRenderer(canvas.width, canvas.height)
            OrbitControls(camera, canvas)

            GridHelper(
                color1 = Color(0x0000ff),
                color2 = Color(0xffffff)
            ).also {
                scene.add(it)
                camera.lookAt(it.position)
            }

            AxesHelper(2.5).also {
                it.position.set(-5f, 0.1f, -5f)
                scene.add(it)
            }

            canvas.animate {

                renderer.render(scene, camera)

            }

        }

    }

}
