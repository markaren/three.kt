package info.laht.threekt.examples.textures

import info.laht.threekt.Window
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.geometries.BoxBufferGeometry
import info.laht.threekt.geometries.PlaneBufferGeometry
import info.laht.threekt.geometries.SphereBufferGeometry
import info.laht.threekt.loaders.TextureLoader
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.DEG2RAD
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import info.laht.threekt.textures.Texture

object TextureExample {

    private fun loadTexture(name: String): Texture {
        return TextureLoader.load(javaClass.classLoader.getResource(name)!!.file)
    }

    @JvmStatic
    fun main(args: Array<String>) {

        Window(antialias = 4).use { canvas ->

            val scene = Scene().apply {
                setBackground(Color.aliceblue)
            }

            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 1000)
            camera.position.z = 10f

            val renderer = GLRenderer(canvas.size).apply {
                checkShaderErrors = true
            }

            Mesh(PlaneBufferGeometry(10f, 10f), MeshBasicMaterial().apply {
                color.set(Color.gray)
                map = loadTexture("textures/brick_bump.jpg")
            }).also {
                it.rotation.x = DEG2RAD * -90
                it.translateZ(-1f)
                scene.add(it)
            }


            Mesh(BoxBufferGeometry(1f), MeshBasicMaterial().apply {
                color.set(Color.gray)
                map = loadTexture("textures/crate.gif")
            }).also {
                it.translateY(2f)
                scene.add(it)
            }

            Mesh(SphereBufferGeometry(0.5f), MeshBasicMaterial().apply {
                color.set(Color.gray)
                map = loadTexture("textures/checker.png")
            }).also {
                it.translateY(4f)
                scene.add(it)
            }

            OrbitControls(camera, canvas)

            canvas.animate {
                renderer.render(scene, camera)
            }

        }

    }

}
