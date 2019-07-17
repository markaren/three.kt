package info.laht.threekt

import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.geometries.SphereGeometry
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import org.junit.jupiter.api.Test

class Test {

    @Test
    fun test() {

        Canvas().use { canvas ->

            val scene = Scene()
            val camera = PerspectiveCamera(75)

            val renderer = GLRenderer(canvas)

            val geometry = SphereGeometry()
            val material = MeshBasicMaterial().apply {
                color.set(0xff0000)
            }

            val mesh = Mesh(geometry, material)
            scene.add(mesh)

            while (!canvas.shouldClose()) {
                renderer.render(scene, camera)
            }

        }

    }

}