package info.laht.threekt.examples.materials

import info.laht.threekt.Canvas
import info.laht.threekt.CanvasOptions
import info.laht.threekt.Side
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.core.Uniform
import info.laht.threekt.materials.RawShaderMaterial
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import kotlin.random.Random

object RawShaderExample {

    private val vertexShader = """

			uniform mat4 modelViewMatrix; // optional
			uniform mat4 projectionMatrix; // optional
			attribute vec3 position;
			attribute vec4 color;
			varying vec3 vPosition;
			varying vec4 vColor;
			void main()	{
				vPosition = position;
				vColor = color;
				gl_Position = projectionMatrix * modelViewMatrix * vec4( position, 1.0 );
			}

        """.trimIndent()

    private val fragmentShader = """

			uniform float time;
			varying vec3 vPosition;
			varying vec4 vColor;
			void main()	{
				vec4 color = vec4( vColor );
				color.r += sin( vPosition.x * 10.0 + time ) * 0.5;
				gl_FragColor = color;
			}
    """.trimIndent()

    @JvmStatic
    fun main(args: Array<String>) {

        Canvas(CanvasOptions().apply {
            antialiasing = 4
        }).use { canvas ->

            val scene = Scene().apply {
                setBackground(0x101010)
            }
            val camera = PerspectiveCamera(50, canvas.aspect, 1, 10)
            camera.position.z = 2f
            val renderer = GLRenderer(canvas).apply {
                checkShaderErrors = true
            }

            val triangles = 500

            val geometry = BufferGeometry()

            val positions = mutableListOf<Float>()
            val colors = mutableListOf<Float>()

            val random = Random(0)
            for (i in 0 until triangles) {
                positions.add(random.nextFloat() - 0.5f)
                positions.add(random.nextFloat() - 0.5f)
                positions.add(random.nextFloat() - 0.5f)

                colors.add(random.nextFloat())
                colors.add(random.nextFloat())
                colors.add(random.nextFloat())
                colors.add(random.nextFloat())
            }

            val positionAttribute = FloatBufferAttribute(positions.toFloatArray(), 3)
            val colorAttribute = FloatBufferAttribute(colors.toFloatArray(), 4)

            geometry.addAttribute("position", positionAttribute)
            geometry.addAttribute("color", colorAttribute)

            val material = RawShaderMaterial().also {
                it.uniforms["time"] = Uniform(null)
                it.vertexShader = vertexShader
                it.fragmentShader = fragmentShader
                it.side = Side.Double
                it.transparent = true
            }

            val mesh = Mesh(geometry, material)
            scene.add(mesh)

            var value = 0f
            while (!canvas.shouldClose()) {

                value += 0.005f
                mesh.rotation.y = value
                material.uniforms["time"]!!.value = value * 10f

                renderer.render(scene, camera)

            }

        }

    }

}
