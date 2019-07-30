package info.laht.threekt.examples.extra

import info.laht.threekt.Canvas
import info.laht.threekt.CanvasOptions
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.extras.objects.Reflector
import info.laht.threekt.geometries.BoxBufferGeometry
import info.laht.threekt.geometries.PlaneGeometry
import info.laht.threekt.helpers.PointLightHelper
import info.laht.threekt.lights.PointLight
import info.laht.threekt.materials.MeshPhongMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.ColorBackground
import info.laht.threekt.scenes.Scene
import kotlin.math.PI

object ReflectorExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Canvas(CanvasOptions().apply {
            antialiasing = 4
        }).use { canvas ->

            canvas.enableDebugCallback()

            val scene = Scene().apply {
                background = ColorBackground(Color.aliceblue)
            }
            val renderer = GLRenderer(canvas).apply {
                checkShaderErrors = true
            }

            val camera = PerspectiveCamera(45, canvas.aspect, 1, 500).apply {
                position.set(0f, 7.5f, 16.0f)
            }
            val controls = OrbitControls(camera, canvas)

            val planeGeometry = PlaneGeometry(10f, 10f)

            val mirror = Reflector(
                planeGeometry, Reflector.Options(
                    clipBias = 0.003f,
                    color = Color(0x777777),
                    textureWidth = 512,
                    textureHeight = 512,
                    recursion = 1
                )
            ).also {
                it.rotateX(-PI.toFloat() / 2)
                scene.add(it)
            }

            val box = Mesh(BoxBufferGeometry(1f), MeshPhongMaterial().apply {
                color.set(0x00ff00)
                emissive.set(0x333333)
                flatShading = true
            }).also {
                it.position.y = 3f
                scene.add(it)
            }

            val pointLight = PointLight(0xcccccc, 1.5).also {
                it.position.y = 3f
                it.position.z = 3f
                scene.add(it)

                PointLightHelper(it).also {
                    scene.add(it)
                }

            }

            while (!canvas.shouldClose()) {

                renderer.render(scene, camera)

            }

        }

    }

}