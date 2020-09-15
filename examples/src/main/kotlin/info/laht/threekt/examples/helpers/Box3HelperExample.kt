package info.laht.threekt.examples.helpers

import info.laht.threekt.Window
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.geometries.SphereBufferGeometry
import info.laht.threekt.helpers.Box3Helper
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.objects.Mesh
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

        val sphere = Mesh(
                SphereBufferGeometry(0.5f),
                MeshBasicMaterial().apply { this.color.set(0x00ff00) }
        ).also {
            scene.add(it)
        }
        sphere.geometry.computeBoundingBox()

        Box3Helper(sphere.geometry.boundingBox!!, 0xffff00).also {
            scene.add(it)
        }

        canvas.animate {

            renderer.render(scene, camera)

        }

    }

}
