package info.laht.threekt

import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.core.Clock
import info.laht.threekt.geometries.BoxGeometry
import info.laht.threekt.geometries.CylinderBufferGeometry
import info.laht.threekt.geometries.PlaneGeometry
import info.laht.threekt.lights.PointLight
import info.laht.threekt.materials.*
import info.laht.threekt.math.Color
import info.laht.threekt.math.DEG2RAD
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.ColorBackground
import info.laht.threekt.scenes.Scene
import org.lwjgl.opengl.GLUtil

object PointLightExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Canvas(CanvasOptions().apply {
            antialiasing = 4
        }).use { canvas ->

            val scene = Scene()

            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 1000)
            val renderer = GLRenderer(canvas).apply {
                checkShaderErrors = true
            }


            Mesh(PlaneGeometry(10f, 10f), MeshPhongMaterial().apply {
                color.set(Color.gray)
                side = DoubleSide
            }).also {
                it.rotation.x = DEG2RAD * -90
                it.translateZ(-1f)
                it.receiveShadow = true
                scene.add(it)
            }


            val box = Mesh(BoxGeometry(1f).apply { computeVertexNormals() }, MeshPhongMaterial().apply {
                color.set(0x00ff00)
            }).also {
                it.position.x = -2f
                it.castShadow = true
                scene.add(it)
            }
            camera.position.z = 10f

            val controls = OrbitControls(camera, canvas)

            val pointLight = PointLight(Color.fromHex(Color.yellow)).also {
                it.intensity = 0.5f
                it.position.y = 5f
                it.castShadow = true
                scene.add(it)
            }


            val clock = Clock()
            while (!canvas.shouldClose()) {

                renderer.render(scene, camera)

                val dt = clock.getDelta()
                box.rotation.x += 0.5f * dt
                box.rotation.y += 0.5f * dt

            }

        }

    }

}