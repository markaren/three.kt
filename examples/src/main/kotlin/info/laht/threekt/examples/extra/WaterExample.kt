package info.laht.threekt.examples.extra

import info.laht.threekt.TextureWrapping
import info.laht.threekt.Window
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.core.Clock
import info.laht.threekt.examples.textures.TextureExample
import info.laht.threekt.extras.objects.Sky
import info.laht.threekt.extras.objects.Water
import info.laht.threekt.geometries.PlaneBufferGeometry
import info.laht.threekt.geometries.SphereBufferGeometry
import info.laht.threekt.lights.DirectionalLight
import info.laht.threekt.loaders.TextureLoader
import info.laht.threekt.materials.MeshPhongMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.TWO_PI
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import kotlin.math.PI
import kotlin.math.sin

object WaterExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Window(
                antialias = 4,
                width = 1280,
                height = 960).use { window ->

            window.enableDebugCallback()

            val scene = Scene().apply {
                setBackground(Color(Color.aliceblue).multiplyScalar(0.8f))
            }
            val renderer = GLRenderer(window.size).apply {
                checkShaderErrors = true
            }

            val camera = PerspectiveCamera(45, window.aspect, 1, 200000).apply {
                position.set(-250f, 500f, 2000f)
            }
            OrbitControls(camera, window)

            val light = DirectionalLight(0xffffff, 0.8).also {
                scene.add(it)
            }

            val sky = Sky()
            sky.scale.setScalar(45000)
            scene.add(sky)

            val planeGeometry = PlaneBufferGeometry(10000)

            val texture =
                    TextureLoader.load(TextureExample::class.java.classLoader.getResource("textures/waternormals.jpg").file)
                            .also {
                                it.wrapS = TextureWrapping.Repeat
                                it.wrapT = TextureWrapping.Repeat
                            }

            val water = Water(
                    planeGeometry, Water.Options(
                    alpha = 0.7f,
                    waterNormals = texture,
                    waterColor = Color(0x001e0f),
                    sunColor = Color(0xffffff),
                    textureWidth = 512,
                    textureHeight = 512,
                    sunDirection = light.position.clone().normalize(),
                    distortionScale = 1f
            )
            ).also {
                it.rotateX(-PI.toFloat() / 2)
                scene.add(it)
            }

            val sphere = Mesh(SphereBufferGeometry(100), MeshPhongMaterial().apply {
                color.set(0x00ff00)
                emissive.set(0x333333)
            }).also {
                it.position.y = 3f
                scene.add(it)
            }

            val clock = Clock()
            window.animate {

                val wTime = water.uniforms["time"]!!.value as Float
                water.uniforms["time"]!!.value = wTime + (0.6f * clock.getDelta())

                sphere.position.y = 50 * sin(TWO_PI * 0.1f * clock.getElapsedTime())

                renderer.render(scene, camera)

            }

        }

    }

}
