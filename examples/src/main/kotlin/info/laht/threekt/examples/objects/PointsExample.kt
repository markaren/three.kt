package info.laht.threekt.examples.objects

import info.laht.threekt.Colors
import info.laht.threekt.Window
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.Clock
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.materials.PointsMaterial
import info.laht.threekt.objects.Points
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Fog
import info.laht.threekt.scenes.Scene
import kotlin.random.Random

object PointsExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Window(antialias = 4).use { canvas ->

            val scene = Scene().apply {
                setBackground(0x050505)
                fog = Fog(0x050505, 2000, 3500)
            }
            val renderer = GLRenderer(canvas.size)

            val camera = PerspectiveCamera(27, canvas.aspect, 5, 3500).apply {
                position.z = 2750f
            }

            val numParticles = 500000
            val positions = FloatArray(numParticles * 3)
            val colors = FloatArray(numParticles * 3)

            val n = 1000
            val n2 = n / 2

            for (i in 0 until numParticles step 3) {

                positions[i] = Random.nextFloat() * n - n2
                positions[i + 1] = Random.nextFloat() * n - n2
                positions[i + 2] = Random.nextFloat() * n - n2

                colors[i] = (positions[i] / n) + 0.5f
                colors[i + 1] = (positions[i + 1] / n) + 0.5f
                colors[i + 2] = (positions[i + 2] / n) + 0.5f

            }


            val geometry = BufferGeometry()
            geometry.addAttribute("position", FloatBufferAttribute(positions, 3))
            geometry.addAttribute("color", FloatBufferAttribute(colors, 3))

            geometry.computeBoundingSphere()

            val material = PointsMaterial().apply {
                size = 15f
                vertexColors = Colors.Vertex
            }

            val points = Points(geometry, material)
            scene.add(points)

            val clock = Clock()
            canvas.animate {

                val time = clock.getElapsedTime()

                points.rotation.x = time * 0.25f
                points.rotation.y = time * 0.5f

                renderer.render(scene, camera)

            }

        }

    }

}
