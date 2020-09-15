package info.laht.threekt.examples.geometries

import info.laht.threekt.Window
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.core.Clock
import info.laht.threekt.geometries.ConeBufferGeometry
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene

object ConeGeometryExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Window(antialias = 4).use { canvas ->

            val scene = Scene()
            val renderer = GLRenderer(canvas.size)

            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 100).apply {
                position.z = 5f
            }
            OrbitControls(camera, canvas)

            val geometry = ConeBufferGeometry(0.5f, 1f)
            val material = MeshBasicMaterial().apply {
                color.set(Color.orange)
                wireframe = true
            }

            val sphere = Mesh(geometry, material)
            scene.add(sphere)

            val clock = Clock()
            canvas.animate {

                sphere.rotation.y += 0.5f * clock.getDelta()

                renderer.render(scene, camera)

            }

        }

    }

}
