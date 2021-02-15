package info.laht.threekt.examples.instancing

import info.laht.threekt.Window
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.core.Raycaster
import info.laht.threekt.geometries.IcosahedronBufferGeometry
import info.laht.threekt.input.MouseAdapter
import info.laht.threekt.input.MouseEvent
import info.laht.threekt.lights.HemisphereLight
import info.laht.threekt.materials.MeshPhongMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Vector2
import info.laht.threekt.objects.InstancedMesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import javax.imageio.ImageIO

object InstancedMeshExample {

    @JvmStatic
    fun main(args: Array<String>) {
        val favicon = ImageIO.read(javaClass.getResourceAsStream("/images/favicon.bmp"))
        Window(antialias = 4, favicon = favicon).use { canvas ->

            val scene = Scene()

            val amount = 10
            val count = Math.pow(amount.toDouble(), 3.toDouble()).toInt() // amount of instances

            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 100).also {
                it.position.set(amount, amount, amount)
                it.lookAt(0f, 0f, 0f)
            }
            val renderer = GLRenderer(canvas.size)

            OrbitControls(camera, canvas)

            // TODO Hemisphere lighting doesn't show up form some reason
            val light1 = HemisphereLight(0xffffff, 0x000088)
            light1.position.set(- 1, 1.5, 1)
            scene.add(light1)

            val light2 = HemisphereLight(0xffffff, 0x880000, 0.5)
            light2.position.set(- 1, 1.5, 1)
            scene.add(light2)

            val color = Color(Color.whitesmoke) // Added whitesmoke color because hemisphere lighting isn't working
            val geometry = IcosahedronBufferGeometry(0.5f, 3)
            val material = MeshPhongMaterial()
            val mesh = InstancedMesh(geometry, mutableListOf(material), count)

            val offset = (amount.toDouble() - 1) / 2
            val matrix = Matrix4()
            var index = 0
            for (x in 0 until amount) {
                for (y in 0 until amount) {
                    for (z in 0 until amount) {
                        matrix.setPosition(offset - x, offset - y, offset - z)
                        mesh.setMatrixAt(index, matrix)
                        mesh.setColorAt(index, color)
                        index++
                    }
                }
            }
            scene.add(mesh)

            val mouse = Vector2(-1, -1)
            val raycaster = Raycaster()
            canvas.addMouseListener(object : MouseAdapter() {
                override fun onMouseMove(event: MouseEvent) {
                    mouse.x = (event.clientX.toFloat() / canvas.size.width) * 2 - 1
                    mouse.y = -(event.clientY.toFloat() / canvas.size.height) * 2 + 1
                }
            })

            canvas.animate {
                raycaster.setFromCamera(mouse, camera)
                val intersection = raycaster.intersectObject(mesh)
                if (intersection.isNotEmpty()) {
                    val instanceId = intersection.firstOrNull()?.instanceId
                    if (instanceId != null) {
                        mesh.setColorAt(instanceId, color.set((Math.random().toFloat() * 0xFFFFFF).toInt()))
                        mesh.instanceColor?.needsUpdate = true
                    }
                }
                renderer.render(scene, camera)
            }
        }
    }

}
