package info.laht.threekt.examples.lights

import info.laht.threekt.ShadowType
import info.laht.threekt.Window
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.geometries.PlaneBufferGeometry
import info.laht.threekt.geometries.SphereBufferGeometry
import info.laht.threekt.helpers.CameraHelper
import info.laht.threekt.lights.PointLight
import info.laht.threekt.materials.MeshPhongMaterial
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import kotlin.math.PI

object ShadowExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Window(antialias = 4).use { canvas ->

            val scene = Scene()

            val camera = PerspectiveCamera()
            camera.position.set(0f, 5f, 20f)

            val renderer = GLRenderer(canvas.size)
            renderer.shadowMap.enabled = true
            renderer.shadowMap.type = ShadowType.PCFSoft

            val light = PointLight(0xffffff, 0.5f, 100)
            light.position.set(0f, 10f, 0f)
            light.castShadow = true
            scene.add(light)

            val sphereGeometry = SphereBufferGeometry(1f)
            val sphereMaterial = MeshPhongMaterial().apply { color.set(0xff0000) }
            val sphere = Mesh(sphereGeometry, sphereMaterial)
            sphere.position.y = 2f
            sphere.castShadow = true
            sphere.receiveShadow = true
            scene.add(sphere)

            val planeGeometry = PlaneBufferGeometry(20f, 20f, 32, 32)
            val planeMaterial = MeshPhongMaterial().apply { color.set(0x00ff00) }
            val plane = Mesh(planeGeometry, planeMaterial)
            plane.rotateX(-PI.toFloat() / 2)
            plane.receiveShadow = true
            scene.add(plane)

            val helper = CameraHelper(light.shadow.camera)
            scene.add(helper)

            OrbitControls(camera, canvas)
            canvas.animate {

                renderer.render(scene, camera)

            }

        }

    }

}
