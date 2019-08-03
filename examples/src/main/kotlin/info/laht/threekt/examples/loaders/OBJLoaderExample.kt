package info.laht.threekt.examples.loaders

import info.laht.threekt.Canvas
import info.laht.threekt.CanvasOptions
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.geometries.PlaneBufferGeometry
import info.laht.threekt.lights.AmbientLight
import info.laht.threekt.lights.PointLight
import info.laht.threekt.loaders.OBJLoader
import info.laht.threekt.loaders.STLLoader
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.materials.MeshLambertMaterial
import info.laht.threekt.materials.MeshPhongMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.Vector3
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import kotlin.math.PI

object OBJLoaderExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Canvas(CanvasOptions().apply {
            antialiasing = 4
        }).use { canvas ->

            val scene = Scene()
            val renderer = GLRenderer(canvas)

            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 100).apply {
                position.set(0,10,75)
            }
            val controls = OrbitControls(camera, canvas)

            OBJLoader().load(OBJLoaderExample::class.java.classLoader.getResource("models/obj/walt/WaltHead.obj").file)
                .also { obj ->

                    obj.center()
                    obj.computeBoundingBox()

                    val mesh = Mesh(obj, MeshPhongMaterial().apply {
                        color.set(0xAAAAAA)
//                        flatShading = true
                    })
                    scene.add(mesh)

                    camera.lookAt(mesh.geometry.boundingBox!!.getCenter(Vector3()))
                    controls.update()

                }

            val light1 = PointLight(intensity = 1f)
            light1.position.set(25f, 15f, 25f)
            scene.add(light1)

            val light2 = PointLight(intensity = 1f)
            light2.position.set(-25f, 15f, 25f)
            scene.add(light2)

            val light3 = PointLight(intensity = 1f)
            light3.position.set(0, 25f, -30f)
            scene.add(light3)

            while (!canvas.shouldClose()) {

                renderer.render(scene, camera)

            }

        }

    }

}
