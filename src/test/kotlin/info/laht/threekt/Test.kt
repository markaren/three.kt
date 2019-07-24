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
import org.lwjgl.opengl.GLUtil
import org.lwjgl.system.Callback

class Test {

    @Test
    fun test() {

        Canvas().use { canvas ->

            val debugProc = GLUtil.setupDebugMessageCallback()!!

            val scene = Scene().apply {
                background = ColorBackground(Color.aliceblue)
            }

            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 1000)
            val renderer = GLRenderer(canvas).apply {
                checkShaderErrors = true
            }

            val box = Mesh(BoxGeometry(1f), MeshBasicMaterial().apply {
                color.set(0x00ff00)
            }).also {
                scene.add(it)
            }

            val sphere = Mesh(SphereGeometry(0.5f), MeshBasicMaterial().apply {
                color.set(Color.rebeccapurple)
            }).also {
                it.position.x += 2f
                scene.add(it)
            }

            camera.position.z = 5f


            val clock = Clock()
            while (!canvas.shouldClose()) {
                renderer.render(scene, camera)
                box.rotation.x += 1f * clock.getDelta().toFloat()
                box.rotation.y += 1f * clock.getDelta().toFloat()
            }

            debugProc.free()

        }

    }

}
