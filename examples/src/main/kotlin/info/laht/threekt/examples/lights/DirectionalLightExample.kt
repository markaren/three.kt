package info.laht.threekt.examples.lights

import info.laht.threekt.Canvas
import info.laht.threekt.CanvasOptions
import info.laht.threekt.DoubleSide
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.core.Clock
import info.laht.threekt.geometries.BoxGeometry
import info.laht.threekt.geometries.PlaneGeometry
import info.laht.threekt.helpers.DirectionalLightHelper
import info.laht.threekt.lights.DirectionalLight
import info.laht.threekt.lights.PointLight
import info.laht.threekt.materials.*
import info.laht.threekt.math.Color
import info.laht.threekt.math.DEG2RAD
import info.laht.threekt.math.Vector3
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene

object DirectionalLightExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Canvas(CanvasOptions().apply {
            antialiasing = 4
        }).use { canvas ->

            val scene = Scene()
            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 1000)
            camera.position.set(5f, 5f, 5f)

            val controls = OrbitControls(camera, canvas)

            val renderer = GLRenderer(canvas)

            Mesh(PlaneGeometry(10f, 10f), MeshPhongMaterial().apply {
                color.set(Color.gray)
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

            val light = DirectionalLight(Color.fromHex(Color.white)).also {
                it.intensity = 0.5f
                it.castShadow = true
                it.position.set(-10,10,-10)
                scene.add(it)
            }

            val helper = DirectionalLightHelper(light, 5)
            scene.add(helper)

            val clock = Clock()
            while (!canvas.shouldClose()) {

                renderer.render(scene, camera)

                val dt = clock.getDelta()
                box.rotation.y += 0.5f * dt

            }

        }

    }

}