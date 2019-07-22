package info.laht.threekt

import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.geometries.BoxGeometry
import info.laht.threekt.geometries.SphereGeometry
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.materials.MeshDepthMaterial
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import org.junit.jupiter.api.Test

class Test {

    @Test
    fun test() {

        Canvas().use { canvas ->

            val scene = Scene().apply {
                //                background = ColorBackground(0xffffff)
            }

            val camera = PerspectiveCamera(75, canvas.width / canvas.height)
            val renderer = GLRenderer(canvas)

            Mesh(BoxGeometry(1f), MeshBasicMaterial().apply {
                color.set(0x00ff00)
            }).also { scene.add(it)
            it.frustumCulled = false
            }

            Mesh(SphereGeometry(1f), MeshDepthMaterial()).also { scene.add(it) }

            camera.position.x = 5f

//            val light = AmbientLight(Color.yellow)
//            scene.add(light)

            renderer.compile(scene, camera)
            while (!canvas.shouldClose()) {
                renderer.render(scene, camera)
            }

        }

    }

}
