package info.laht.threekt.examples.loaders

import info.laht.threekt.Canvas
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.Clock
import info.laht.threekt.lights.PointLight
import info.laht.threekt.loaders.OBJLoader
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene

object OBJLoaderExample {

    @JvmStatic
    fun main(args: Array<String>) {

        Canvas(Canvas.Options().apply {
            antialiasing = 4
        }).use { canvas ->

            val scene = Scene()
            val renderer = GLRenderer(canvas.width, canvas.height)

            val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 1000).apply {
                position.set(0, 100, 175)
            }

            val obj = OBJLoader(true).load(OBJLoaderExample::class.java.classLoader.getResource("models/obj/female02/female02.obj").file)
            scene.add(obj)

            val light1 = PointLight(intensity = 1f)
            light1.position.set(25f, 115f, 25f)
            scene.add(light1)

            val light2 = PointLight(intensity = 1f)
            light2.position.set(-25f, 115f, 125f)
            scene.add(light2)

            val light3 = PointLight(intensity = 1f)
            light3.position.set(0, 25f, -30f)
            scene.add(light3)

            val clock = Clock()
            canvas.animate {

                obj.rotation.y += 1f * clock.getDelta()

                renderer.render(scene, camera)

            }

        }

    }

}
