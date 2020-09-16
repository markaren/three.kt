package info.laht.threekt.examples.geometries

import info.laht.threekt.Window
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.core.Clock
import info.laht.threekt.geometries.IcosahedronBufferGeometry
import info.laht.threekt.geometries.PolyhedronBufferGeometry
import info.laht.threekt.materials.MaterialWithColor
import info.laht.threekt.materials.MaterialWithWireframe
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene

fun main() {

    Window(antialias = 4).use { canvas ->

        val scene = Scene()
        val renderer = GLRenderer(canvas.size)

        val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 100).apply {
            position.z = 15f
        }
        OrbitControls(camera, canvas)

        val verticesOfCube = floatArrayOf(
                -1f, -1f, -1f, 1f, -1f, -1f, 1f, 1f, -1f, -1f, 1f, -1f,
                -1f, -1f, 1f, 1f, -1f, 1f, 1f, 1f, 1f, -1f, 1f, 1f,
        )

        val indicesOfFaces = intArrayOf(
                2, 1, 0, 0, 3, 2,
                0, 4, 7, 7, 3, 0,
                0, 1, 5, 5, 4, 0,
                1, 2, 6, 6, 5, 1,
                2, 3, 7, 7, 6, 2,
                4, 5, 6, 6, 7, 4
        )

        val geometry1 = PolyhedronBufferGeometry(verticesOfCube, indicesOfFaces, 4f, 1)
        val mesh1 = Mesh(geometry1, MeshBasicMaterial().apply { color.set(Color.blueviolet) }).also {
            it.position.set(-5f, 0f, 0f)
            scene.add(it)
        }

        Mesh(geometry1.clone()).apply {
            (material as MaterialWithColor).color.set(Color.white)
            (material as MaterialWithWireframe).wireframe = true
            mesh1.add(this)
        }

        val geometry2 = IcosahedronBufferGeometry(4f, 1)
        val mesh2 = Mesh(geometry2, MeshBasicMaterial().apply { color.set(Color.blueviolet) }).also {
            it.position.set(5f, 0f, 0f)
            scene.add(it)
        }

        Mesh(geometry2.clone()).apply {
            (material as MaterialWithColor).color.set(Color.white)
            (material as MaterialWithWireframe).wireframe = true
            mesh2.add(this)
        }

        val clock = Clock()
        canvas.animate {

            val dt = clock.getDelta()
            mesh1.rotation.y -= 0.5f * dt
            mesh2.rotation.y += 0.5f * dt

            renderer.render(scene, camera)

        }

    }

}
