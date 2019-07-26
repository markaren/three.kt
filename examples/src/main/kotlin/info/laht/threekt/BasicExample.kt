package info.laht.threekt

import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.core.Clock
import info.laht.threekt.geometries.BoxGeometry
import info.laht.threekt.geometries.CylinderBufferGeometry
import info.laht.threekt.geometries.PlaneGeometry
import info.laht.threekt.helpers.GridHelper
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.DEG2RAD
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.ColorBackground
import info.laht.threekt.scenes.Scene

object BasicExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Canvas(CanvasOptions().apply {
            antialiasing = 4
        }).use { canvas ->

            val scene = Scene().apply {
                background = ColorBackground(Color.aliceblue)
            }

            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 1000).also {
                it.translateZ(10f)
            }
            val renderer = GLRenderer(canvas).apply {
                checkShaderErrors = true
            }

            val controls = OrbitControls(camera, canvas)

            val plane = Mesh(PlaneGeometry(10f, 10f), MeshBasicMaterial().apply {
                color.set(Color.gray)
                side = DoubleSide
            }).also {
                it.rotation.x = DEG2RAD * -90
                it.translateZ(-1f)
                scene.add(it)
            }

            val box = Mesh(BoxGeometry(1f), MeshBasicMaterial().apply {
                color.set(0x00ff00)
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

            val clock = Clock()
            while (!canvas.shouldClose()) {

                renderer.render(scene, camera)

                val dt = clock.getDelta()
                box.rotation.x += 0.5f * dt
                box.rotation.y += 0.5f * dt

                cylinder.rotation.z += 0.5f * dt

            }

        }

    }

}
