package info.laht.threekt.examples.controls

import info.laht.threekt.Window
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.FlyControls
import info.laht.threekt.core.Clock
import info.laht.threekt.geometries.SphereBufferGeometry
import info.laht.threekt.lights.DirectionalLight
import info.laht.threekt.loaders.TextureLoader
import info.laht.threekt.materials.MeshLambertMaterial
import info.laht.threekt.materials.MeshPhongMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.Vector2
import info.laht.threekt.math.Vector3
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.FogExp2
import info.laht.threekt.scenes.Scene

object FlyControlsExample {

	@JvmStatic
	fun main(args: Array<String>) {
		Window(antialias = 4, width = 1200, height = 800).use { canvas ->
			val radius = 6371f
			val tilt = 0.41f
			val rotationSpeed = 0.02f
			val cloudsScale = 1.005
			val moonScale = 0.23f

			val dMoonVec = Vector3()
			val clock = Clock()

			val scene = Scene().also {
				it.fog = FogExp2(Color(0x000000), 0.00000025)
			}

			val dirLight = DirectionalLight(0xffffff)
			dirLight.position.set(-1, 0, 1).normalize()
			scene.add(dirLight)

			val classLoader = javaClass.classLoader

			val materialNormalMap = MeshPhongMaterial().also {
				it.specular.set(0x333333)
				it.shininess = 15.0f
				it.map = TextureLoader.load(
						classLoader.getResource("textures/planets/earth_atmos_2048.jpg")!!.file
				)
				it.specularMap = TextureLoader.load(
						classLoader.getResource("textures/planets/earth_specular_2048.jpg")!!.file
				)
				it.normalMap = TextureLoader.load(
						classLoader.getResource("textures/planets/earth_normal_2048.png")!!.file
				)

				// y scale is negated to compensate for normal map handedness.
				it.normalScale = Vector2(0.85, -0.85)
			}

			//planet
			val geometry = SphereBufferGeometry(radius, 100, 50)
			val meshPlanet = Mesh(geometry, materialNormalMap)
			meshPlanet.rotation.y = 0.0f
			meshPlanet.rotation.z = tilt
			scene.add(meshPlanet)

			// clouds
			val materialClouds = MeshLambertMaterial().also {
				it.map = TextureLoader.load(
						classLoader.getResource("textures/planets/earth_clouds_1024.png")!!.file
				)
				it.transparent = true
			}
			val meshClouds = Mesh(geometry, materialClouds)
			meshClouds.scale.set(cloudsScale, cloudsScale, cloudsScale)
			meshClouds.rotation.z = tilt
			scene.add(meshClouds)

			// moon
			val materialMoon = MeshPhongMaterial().also {
				it.map = TextureLoader.load(classLoader.getResource("textures/planets/moon_1024.jpg")!!.file)
			}
			val meshMoon = Mesh(geometry, materialMoon)
			meshMoon.position.set(radius * 5, 0, 0)
			meshMoon.scale.set(moonScale, moonScale, moonScale)
			scene.add(meshMoon)

			val camera = PerspectiveCamera(25, canvas.aspect, 50, 1e7).also {
				it.position.z = radius * 5
			}

			val renderer = GLRenderer(canvas.size)

			val controls = FlyControls(camera, canvas)
			controls.movementSpeed = 1000.0f
			controls.rollSpeed = Math.PI.toFloat() / 24.0f
			controls.autoForward = false
			controls.dragToLook = false

			canvas.animate {
				val delta = clock.getDelta()

				meshPlanet.rotation.y += rotationSpeed * delta
				meshClouds.rotation.y += 1.25f * rotationSpeed * delta

				// slow down as we approach the surface

				val dPlanet = camera.position.length()

				dMoonVec.subVectors(camera.position, meshMoon.position)
				val dMoon = dMoonVec.length()

				val d = if (dMoon < dPlanet) {
					dMoon - radius * moonScale * 1.01f
				} else {
					dPlanet - radius * 1.01f
				}

				controls.movementSpeed = 0.33f * d
				controls.update(delta)

				renderer.render(scene, camera)
			}

		}

	}

}
