package info.laht.threekt.examples.textures

import info.laht.threekt.Canvas
import info.laht.threekt.CanvasOptions
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.geometries.BoxGeometry
import info.laht.threekt.geometries.PlaneGeometry
import info.laht.threekt.loaders.TextureLoader
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.DEG2RAD
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.ColorBackground
import info.laht.threekt.scenes.Scene
import java.io.File

object TextureExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Canvas(CanvasOptions().apply {
            antialiasing = 4
        }).use { canvas ->

            val scene = Scene().apply {
                background = ColorBackground(Color.aliceblue)
            }

            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 1000)
            val renderer = GLRenderer(canvas).apply {
                checkShaderErrors = true
            }

            Mesh(PlaneGeometry(10f, 10f), MeshBasicMaterial().apply {
                color.set(Color.gray)
                map = TextureLoader.load(File(TextureExample::class.java.classLoader.getResource("textures/brick_bump.jpg").file))
            }).also {
                it.rotation.x = DEG2RAD * -90
                it.translateZ(-1f)
                scene.add(it)
            }

            Mesh(BoxGeometry(1f), MeshBasicMaterial().apply {
                color.set(Color.gray)
                map = TextureLoader.load(File(TextureExample::class.java.classLoader.getResource("textures/crate.gif").file))
            }).also {
                it.translateY(2f)
                scene.add(it)
            }

            camera.position.z = 10f

            val controls = OrbitControls(camera, canvas)

            while (!canvas.shouldClose()) {
                renderer.render(scene, camera)
            }

        }

    }

}
