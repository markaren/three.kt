package info.laht.threekt

import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.Clock
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
            val renderer = GLRenderer(canvas).apply {
                checkShaderErrors = true
            }

            Mesh(BoxGeometry(1f), MeshBasicMaterial().apply {
                color.set(0x00ff00)
            }).also { scene.add(it)
            it.frustumCulled = false
            }

            Mesh(SphereGeometry(1f), MeshDepthMaterial()).also { scene.add(it)
            it.frustumCulled = false}

            camera.position.x = 5f

//            val light = AmbientLight(Color.yellow)
//            scene.add(light)

            renderer.compile(scene, camera)
            val clock = Clock(true)
            while (!canvas.shouldClose()) {
                renderer.render(scene, camera)
                val dt = clock.getDelta()
//                println(dt)
//                camera.rotation.x *= 0.1f*dt.toFloat()
//                camera.rotation.y *= 0.2f*dt.toFloat()
//                camera.rotation.z *= 0.3f*dt.toFloat()
            }

        }

    }

}
