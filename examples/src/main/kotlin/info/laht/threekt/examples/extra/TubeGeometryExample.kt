package info.laht.threekt.examples.extra

import info.laht.threekt.Canvas
import info.laht.threekt.CanvasOptions
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.extras.core.Curve3
import info.laht.threekt.extras.curves.CatmullRomCurve3
import info.laht.threekt.geometries.TubeBufferGeometry
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.math.TWO_PI
import info.laht.threekt.math.Vector3
import info.laht.threekt.objects.Line
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import kotlin.math.sin

object TubeGeometryExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Canvas(CanvasOptions().apply {
            antialiasing = 4
        }).use { canvas ->

            val scene = Scene()
            val renderer = GLRenderer(canvas)

            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 100).apply {
                position.z = 25f
            }
            OrbitControls(camera, canvas)

            val geometry = TubeBufferGeometry(CustomSineCurve(10f))
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

    class CustomSineCurve(
            private val scale: Float
    ): Curve3() {

        override fun getPoint(t: Float, optionalTarget: Vector3): Vector3 {
            val tx = t * 3f - 1.5f
            val ty = sin( TWO_PI * t )
            val tz = 0f
            return optionalTarget.set(tx, ty, tz).multiplyScalar(scale)
        }
    }


}
