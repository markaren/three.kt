package info.laht.threekt.examples.geometries

import info.laht.threekt.Colors
import info.laht.threekt.Side
import info.laht.threekt.Window
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.Clock
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.lights.AmbientLight
import info.laht.threekt.lights.DirectionalLight
import info.laht.threekt.materials.MeshPhongMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.Vector3
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Fog
import info.laht.threekt.scenes.Scene

object BufferGeometryExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Window(antialias = 4).use { canvas ->

            val renderer = GLRenderer(canvas.size)

            val camera = PerspectiveCamera(27, canvas.aspect, 1, 3500)
            camera.position.z = 2750F

            val scene = Scene().apply {
                setBackground(0x050505)
                fog = Fog(0x050505, 2000, 3500)
            }

            scene.add(AmbientLight(0x444444))

            val light1 = DirectionalLight(0xffffff, 0.5)
            light1.position.set(1, 1, 1)
            scene.add(light1)

            val light2 = DirectionalLight(0xffffff, 1.5)
            light2.position.set(0, -1, 0)
            scene.add(light2)

            val triangles = 160000

            val geometry = BufferGeometry()

            val positions = ArrayList<Float>(triangles * (3 * 3))
            val normals = ArrayList<Float>(triangles * (3 * 3))
            val colors = ArrayList<Float>(triangles * (3 * 4))

            val color = Color()

            val n = 800F
            val n2 = n / 2    // triangles spread in the cube
            val d = 12F
            val d2 = d / 2    // individual triangle size

            val pA = Vector3()
            val pB = Vector3()
            val pC = Vector3()

            val cb = Vector3()
            val ab = Vector3()

            for (i in 0 until triangles) {

                // positions

                val x = Math.random().toFloat() * n - n2
                val y = Math.random().toFloat() * n - n2
                val z = Math.random().toFloat() * n - n2

                val ax = x + Math.random().toFloat() * d - d2
                val ay = y + Math.random().toFloat() * d - d2
                val az = z + Math.random().toFloat() * d - d2

                val bx = x + Math.random().toFloat() * d - d2
                val by = y + Math.random().toFloat() * d - d2
                val bz = z + Math.random().toFloat() * d - d2

                val cx = x + Math.random().toFloat() * d - d2
                val cy = y + Math.random().toFloat() * d - d2
                val cz = z + Math.random().toFloat() * d - d2

                positions.add(ax)
                positions.add(ay)
                positions.add(az)
                positions.add(bx)
                positions.add(by)
                positions.add(bz)
                positions.add(cx)
                positions.add(cy)
                positions.add(cz)

                // flat face normals

                pA.set(ax, ay, az)
                pB.set(bx, by, bz)
                pC.set(cx, cy, cz)

                cb.subVectors(pC, pB)
                ab.subVectors(pA, pB)
                cb.cross(ab)

                cb.normalize()

                val nx = cb.x
                val ny = cb.y
                val nz = cb.z

                normals.add(nx)
                normals.add(ny)
                normals.add(nz)
                normals.add(nx)
                normals.add(ny)
                normals.add(nz)
                normals.add(nx)
                normals.add(ny)
                normals.add(nz)

                // colors

                val vx = (x / n) + 0.5F
                val vy = (y / n) + 0.5F
                val vz = (z / n) + 0.5F

                color.set(vx, vy, vz)

                val alpha = Math.random().toFloat()

                colors.add(color.r)
                colors.add(color.g)
                colors.add(color.b)
                colors.add(alpha)
                colors.add(color.r)
                colors.add(color.g)
                colors.add(color.b)
                colors.add(alpha)
                colors.add(color.r)
                colors.add(color.g)
                colors.add(color.b)
                colors.add(alpha)

            }

            geometry.attributes["position"] = FloatBufferAttribute(positions.toFloatArray(), 3)
            geometry.attributes["normal"] = FloatBufferAttribute(normals.toFloatArray(), 3)
            geometry.attributes["color"] = FloatBufferAttribute(colors.toFloatArray(), 4)

            geometry.computeBoundingSphere()

            val material = MeshPhongMaterial().apply {
                color.set(0xaaaaaa)
                specular.set(0xffffff)
                shininess = 250F
                side = Side.Double
                vertexColors = Colors.Vertex
                transparent = true
            }

            val mesh = Mesh(geometry, material)
            scene.add(mesh)

            val clock = Clock()
            canvas.animate {

                mesh.rotation.x += clock.getDelta() * 0.25F
                mesh.rotation.y += clock.getDelta() * 0.5F

                renderer.render(scene, camera)

            }

        }

    }

}
