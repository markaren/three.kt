package info.laht.threekt

import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.Clock
import info.laht.threekt.geometries.BoxGeometry
import info.laht.threekt.geometries.SphereGeometry
import info.laht.threekt.lights.AmbientLight
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.materials.MeshDepthMaterial
import info.laht.threekt.materials.MeshStandardMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.ColorBackground
import info.laht.threekt.scenes.Scene
import org.junit.jupiter.api.Test

class Test {

    @Test
    fun test() {

        Canvas().use { canvas ->

            val scene = Scene().apply {
                background = ColorBackground(Color.aliceblue)
            }

            val camera = PerspectiveCamera(50, canvas.width.toFloat() / canvas.height)
            val renderer = GLRenderer(canvas).apply {
                checkShaderErrors = true
            }

            val box = Mesh(BoxGeometry(1f), MeshStandardMaterial().apply {
                color.set(0x00ff00)
            }).also {
                scene.add(it)
                it.frustumCulled = false
            }

            Mesh(SphereGeometry(1f), MeshBasicMaterial()).also {
                scene.add(it)
                it.frustumCulled = false
            }

            camera.position.z = 5f
            camera.lookAt(box.position)


            renderer.compile(scene, camera)
            val clock = Clock(true)
            while (!canvas.shouldClose()) {
                renderer.render(scene, camera)
//                val dt = clock.getDelta()
//                println(dt)
//                camera.rotation.x *= 0.1f*dt.toFloat()
//                camera.rotation.y *= 0.2f*dt.toFloat()
//                camera.rotation.z *= 0.3f*dt.toFloat()
            }

        }

    }

}
