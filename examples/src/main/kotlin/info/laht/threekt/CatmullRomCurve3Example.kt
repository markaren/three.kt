package info.laht.threekt

import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.extras.curves.CatmullRomCurve3
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.math.Vector3
import info.laht.threekt.objects.Line
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene

object CatmullRomCurve3Example {

    @JvmStatic
    fun main(args: Array<String>) {

        Canvas(CanvasOptions().apply {
            antialiasing = 4
        }).use { canvas ->

            val scene = Scene()
            val renderer = GLRenderer(canvas)

            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 1000).apply {
                position.z = 10f
            }
            val controls = OrbitControls(camera, canvas)

            val points = CatmullRomCurve3(
                listOf(
                    Vector3(-10, 0, 10),
                    Vector3(-5, 5, 5),
                    Vector3(0, 0, 0),
                    Vector3(5, -5, 5),
                    Vector3(10, 0, 10)
                )
            ).getPoints(5)

            val geometry = BufferGeometry().setFromPoints(points)
            val material = LineBasicMaterial().apply {
                color.set(0xff0000)
            }

            val line = Line(geometry, material)
            scene.add(line)

            while (!canvas.shouldClose()) {

                renderer.render(scene, camera)

            }

        }

    }

}
