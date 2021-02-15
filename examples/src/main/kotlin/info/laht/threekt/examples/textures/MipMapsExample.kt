package info.laht.threekt.examples.textures

import info.laht.threekt.TextureWrapping
import info.laht.threekt.Window
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.geometries.PlaneBufferGeometry
import info.laht.threekt.helpers.AxesHelper
import info.laht.threekt.lights.AmbientLight
import info.laht.threekt.loaders.ImageLoader
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import info.laht.threekt.textures.Image
import info.laht.threekt.textures.Texture

object MipMapsExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Window(antialias = 4).use { canvas ->

            val scene = Scene().apply {
                setBackground(Color.aliceblue)
            }

            val camera = PerspectiveCamera(75, canvas.aspect, 0.01, 1000).apply {
                position.z = 1f
            }

            val renderer = GLRenderer(canvas.size).apply {
                checkShaderErrors = true
            }

            val light = AmbientLight()
            scene.add(light)

            val axesHelper1 = AxesHelper(5)
            scene.add(axesHelper1)
            val axesHelper2 = AxesHelper(5)
            scene.add(axesHelper2)

            fun Texture.addMipMap(size: Int) {
                val cl = MipMapsExample.javaClass.classLoader
                val mip = cl.getResource("textures/mipmaps/${size}x${size}.png")!!.file
                val img = ImageLoader.load(mip)
                mipmaps.add(img)
            }

            val texture = Texture().apply {
                addMipMap(128)
                addMipMap(64)
                addMipMap(32)
                addMipMap(16)
                addMipMap(8)
                addMipMap(4)
                addMipMap(2)
                addMipMap(1)
                image = Image(0, 0, null)
                repeat.set(5f, 5f)
                wrapS = TextureWrapping.Repeat
                wrapT = TextureWrapping.Repeat
                needsUpdate = true
            }

            val material = MeshBasicMaterial().apply {
                map = texture
            }

            val planeGeometry = PlaneBufferGeometry()
            val plane = Mesh(planeGeometry, material)
            scene.add(plane)

            OrbitControls(camera, canvas).apply {
                screenSpacePanning = true
            }

            canvas.animate {

                renderer.render(scene, camera)

            }

        }

    }

}
