package info.laht.threekt

import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.core.Clock
import info.laht.threekt.geometries.BoxGeometry
import info.laht.threekt.geometries.SphereGeometry
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.ColorBackground
import info.laht.threekt.scenes.Scene
import org.lwjgl.opengl.GLUtil

object Basic {

    @JvmStatic
    fun main(args: Array<String>) {

        Canvas(CanvasOptions().apply {
            antialiasing = 4
        }).use { canvas ->

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

             Mesh(box.geometry.clone(), MeshBasicMaterial().apply {
                color.set(0xffffff)
                 wireframe = true
            }).also {
                scene.add(it)
            }

            camera.position.z = 5f

            val controls = OrbitControls(camera, canvas)

            val clock = Clock()
            while (!canvas.shouldClose()) {

                renderer.render(scene, camera)

                val dt =  clock.getDelta()
//                box.rotation.x += 1f * dt
//                box.rotation.y += 1f * dt

            }

            debugProc.free()

        }

    }

}
