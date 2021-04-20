package info.laht.threekt.examples.loaders

import info.laht.threekt.Window
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.geometries.PlaneBufferGeometry
import info.laht.threekt.lights.PointLight
import info.laht.threekt.loaders.STLLoader
import info.laht.threekt.materials.MeshPhongMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import kotlin.math.PI

object STLLoaderExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Window(antialias = 4).use { canvas ->

            val scene = Scene()
            val renderer = GLRenderer(canvas.size)

            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 100).apply {
                position.z = 5f
            }
            OrbitControls(camera, canvas)

            val cl = STLLoaderExample::class.java.classLoader
            STLLoader().load(cl.getResource("models/stl/pr2_head_tilt.stl")!!.file)
                .also { stl ->

                    stl.rotateX(-PI.toFloat() / 2)
                    stl.rotateY(PI.toFloat() / 2)
                    stl.scale(2f)

                    val mesh = Mesh(stl, MeshPhongMaterial().apply {
                        color.set(0xAAAAAA)
                        flatShading = true
                    })
                    scene.add(mesh)

                }

            STLLoader().load(STLLoaderExample::class.java.classLoader.getResource("models/stl/pr2_head_pan.stl")!!.file)
                .also { stl ->

                    stl.rotateX(-PI.toFloat() / 2)
                    stl.rotateY(PI.toFloat() / 2)
                    stl.scale(2f)

                    val mesh = Mesh(stl, MeshPhongMaterial().apply {
                        color.set(0xAAAAAA)
                        flatShading = true
                    })
                    scene.add(mesh)

                }

            val plane = Mesh(PlaneBufferGeometry(4f, 4f), MeshPhongMaterial().apply {
                color.set(Color.orange)
            })
            plane.translateY(-0.13f)
            plane.rotateX(-PI.toFloat() / 2)
            scene.add(plane)

            val light1 = PointLight(intensity = 0.6f)
            light1.position.set(2f, 2f, -1f)
            scene.add(light1)

            val light2 = PointLight(intensity = 0.6f)
            light2.position.set(-2f, 2f, 1f)
            scene.add(light2)

            canvas.animate {

                renderer.render(scene, camera)

            }

        }

    }

}
