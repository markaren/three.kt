package info.laht.threekt.examples.lights

import info.laht.threekt.Canvas
import info.laht.threekt.CanvasOptions
import info.laht.threekt.DoubleSide
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.core.Clock
import info.laht.threekt.geometries.BoxGeometry
import info.laht.threekt.geometries.PlaneGeometry
import info.laht.threekt.lights.AmbientLight
import info.laht.threekt.lights.SpotLight
import info.laht.threekt.materials.MeshLambertMaterial
import info.laht.threekt.materials.MeshPhongMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.DEG2RAD
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene

object SpotLightExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Canvas(CanvasOptions().apply {
            antialiasing = 4
        }).use { canvas ->

            val scene = Scene()

            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 1000)
            val renderer = GLRenderer(canvas).apply {
                checkShaderErrors = true
            }


            Mesh(PlaneGeometry(25f, 25f), MeshPhongMaterial().apply {
                color.set(Color.yellowgreen)
                side = DoubleSide
            }).also {
                it.rotation.x = DEG2RAD * -90
                it.translateZ(-1f)
                it.receiveShadow = true
                scene.add(it)
            }


            val box = Mesh(BoxGeometry(1f), MeshPhongMaterial().apply {
                color.set(0x00ff00)
            }).also {
                it.castShadow = true
                scene.add(it)
            }

             Mesh(BoxGeometry(1f), MeshPhongMaterial().apply {
                color.set(0x0000ff)
            }).also {
                it.position.x = 2f
                it.castShadow = true
                box.add(it)
            }

            Mesh(BoxGeometry(1f), MeshLambertMaterial().apply {
                color.set(0xff0000)
            }).also {
                it.position.x = -2f
                it.castShadow = true
                box.add(it)
            }

            camera.position.z = 10f

            val controls = OrbitControls(camera, canvas)

            AmbientLight(0xffffff, 0.25f).also {
                scene.add(it)
            }

            val spotLight = SpotLight(Color.fromHex(Color.white)).also {
                it.intensity = 0.3f
                it.position.y = 3f
                it.castShadow = true
                scene.add(it)
            }

            val clock = Clock()
            while (!canvas.shouldClose()) {

                renderer.render(scene, camera)

                val dt = clock.getDelta()
                box.rotation.x += 0.5f * dt

            }

        }

    }

}