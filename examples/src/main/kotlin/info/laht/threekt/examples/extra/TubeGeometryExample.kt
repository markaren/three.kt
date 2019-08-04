package info.laht.threekt.examples.extra

import info.laht.threekt.Canvas
import info.laht.threekt.Side
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.extras.core.Curve3
import info.laht.threekt.geometries.TubeBufferGeometry
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.TWO_PI
import info.laht.threekt.math.Vector3
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import kotlin.math.sin

object TubeGeometryExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Canvas(Canvas.Options().apply {
            antialiasing = 4
        }).use { canvas ->

            val scene = Scene()
            val renderer = GLRenderer(canvas.width, canvas.height)

            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 100).apply {
                position.z = 25f
            }
            OrbitControls(camera, canvas)

            val geometry = TubeBufferGeometry(CustomSineCurve(10f))
            val material = MeshBasicMaterial().apply {
                color.set(0xff0000)
                side = Side.Double
            }

            val solidTube = Mesh(geometry, material).also {
                scene.add(it)
            }

            solidTube.clone().also {
                it.material = (solidTube.material.clone() as MeshBasicMaterial).also { m ->
                    m.wireframe = true
                    m.color.set(0xffffff)
                }
                scene.add(it)
            }


            fun render() {

                renderer.render(scene, camera)
                canvas.requestAnimationFrame { render() }

            }

            render()

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
