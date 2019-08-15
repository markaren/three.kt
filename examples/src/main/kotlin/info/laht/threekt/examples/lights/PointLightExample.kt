package info.laht.threekt.examples.lights

import info.laht.threekt.Canvas
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.core.Clock
import info.laht.threekt.geometries.BoxGeometry
import info.laht.threekt.geometries.PlaneGeometry
import info.laht.threekt.helpers.PointLightHelper
import info.laht.threekt.lights.PointLight
import info.laht.threekt.materials.MeshLambertMaterial
import info.laht.threekt.materials.MeshPhongMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.DEG2RAD
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene

object PointLightExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Canvas(antialias = 4).use { canvas ->

            val scene = Scene()
            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 1000)
            camera.position.z = 10f

            OrbitControls(camera, canvas)

            val renderer = GLRenderer(canvas.width, canvas.height).apply {
                shadowMap.enabled = true
            }

            Mesh(PlaneGeometry(10f, 10f), MeshPhongMaterial().apply {
                color.set(Color.gray)
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


            val pointLight1 = PointLight(Color.white).also {
                it.intensity = 0.5f
                it.position.y = 3f
                it.position.z = 3f
                it.castShadow = true
                scene.add(it)
            }
            PointLightHelper(pointLight1, 1).also {
                scene.add(it)
            }

            val pointLight2 = PointLight().copy(pointLight1).also {
                it.position.z = -3f
                scene.add(it)
            }
            PointLightHelper(pointLight2, 1).also {
                scene.add(it)
            }

            val clock = Clock()
            canvas.animate {

                renderer.render(scene, camera)

                val dt = clock.getDelta()
                box.rotation.y += 0.5f * dt

            }

        }

    }

}
