package info.laht.threekt

import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.core.Clock
import info.laht.threekt.core.MaterialObject
import info.laht.threekt.core.Raycaster
import info.laht.threekt.geometries.BoxBufferGeometry
import info.laht.threekt.geometries.CylinderBufferGeometry
import info.laht.threekt.geometries.PlaneBufferGeometry
import info.laht.threekt.input.MouseAdapter
import info.laht.threekt.input.MouseEvent
import info.laht.threekt.materials.MaterialWithColor
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.DEG2RAD
import info.laht.threekt.math.Vector2
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import kotlin.random.Random

object BasicExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Window(antialias = 4).use { canvas ->

            val scene = Scene().apply {
                setBackground(Color.aliceblue)
            }

            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 1000).also {
                it.translateZ(10f)
            }
            val renderer = GLRenderer(canvas.size)

            OrbitControls(camera, canvas)

            Mesh(PlaneBufferGeometry(10f), MeshBasicMaterial().apply {
                color.set(Color.gray)
                side = Side.Double
            }).also {
                it.rotation.x = DEG2RAD * -90
                it.translateZ(-1f)
                scene.add(it)
            }

            val box = Mesh(BoxBufferGeometry(1f), MeshBasicMaterial().apply {
                color.set(0x00ff00)
                transparent = true
                opacity = 0.5f
            }).also {
                it.position.x = -2f
                scene.add(it)
            }

            Mesh(box.geometry.clone(), MeshBasicMaterial().apply {
                color.set(0xffffff)
                wireframe = true
            }).also {
                box.add(it)
            }

            val cylinder = Mesh(CylinderBufferGeometry(0.5f, 1f), MeshBasicMaterial().apply {
                color.set(0x0000ff)
            }).also {
                it.position.x = 2f
                scene.add(it)
            }

            Mesh(cylinder.geometry.clone(), MeshBasicMaterial().apply {
                color.set(0xffffff)
                wireframe = true
            }).also {
                cylinder.add(it)
            }

            val mouse = Vector2()
            val raycaster = Raycaster()
            canvas.addMouseListener(object : MouseAdapter() {

                override fun onMouseMove(event: MouseEvent) {

                    mouse.x = (event.clientX.toFloat() / canvas.size.width) * 2 - 1
                    mouse.y = -(event.clientY.toFloat() / canvas.size.height) * 2 + 1


                }

            })

            val clock = Clock()
            canvas.animate {

                if (clock.elapsedTime_ > 0.1f) {
                    raycaster.setFromCamera(mouse, camera)
                    raycaster.intersectObjects(scene.children).forEach {

                        val obj = it.`object`
                        if (obj is MaterialObject) {
                            val mat = obj.material
                            if (mat is MaterialWithColor) {
                                mat.color.set(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
                            }
                        }

                    }
                }

                renderer.render(scene, camera)

                val dt = clock.getDelta()
                box.rotation.x += 0.5f * dt
                box.rotation.y += 0.5f * dt

                cylinder.rotation.z += 0.5f * dt

            }

        }

    }

}
