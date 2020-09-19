package info.laht.threekt.examples.materials

import info.laht.threekt.Window
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.core.Clock
import info.laht.threekt.core.Object3DImpl
import info.laht.threekt.geometries.SphereBufferGeometry
import info.laht.threekt.helpers.DirectionalLightHelper
import info.laht.threekt.lights.DirectionalLight
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.materials.MeshLambertMaterial
import info.laht.threekt.materials.MeshNormalMaterial
import info.laht.threekt.materials.MeshPhongMaterial
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene

fun main() {

    Window(antialias = 4).use { canvas ->

        val scene = Scene()
        val renderer = GLRenderer(canvas.size)

        val camera = PerspectiveCamera(75, canvas.aspect, 0.1, 100).apply {
            position.z = 5f
        }
        OrbitControls(camera, canvas)

        DirectionalLight().also { light ->
            light.position.set(1f, 1f, 1f)
            DirectionalLightHelper(light).also { helper ->
                scene.add(helper)
            }
            scene.add(light)
        }

        val parent = Object3DImpl()
        scene.add(parent)
        val geometry = SphereBufferGeometry(0.5f)

        Mesh(geometry, MeshBasicMaterial().apply {
            color.set(0xff0000)
        }).also {
            it.position[0] = -2f
            parent.add(it)
        }

        Mesh(geometry, MeshPhongMaterial().apply {
            color.set(0xff0000)
        }).also {
            it.position[0] = -1f
            parent.add(it)
        }

        Mesh(geometry, MeshLambertMaterial().apply {
            color.set(0xff0000)
        }).also {
            it.position[0] = 1f
            parent.add(it)
        }

        Mesh(geometry, MeshNormalMaterial()).also {
            it.position[0] = 2f
            parent.add(it)
        }

        val clock = Clock()
        canvas.animate {

            val dt = clock.getDelta()
            parent.rotation.y += 0.5f * dt
            renderer.render(scene, camera)

        }

    }

}
