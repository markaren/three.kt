package info.laht.threekt.examples.objects

import info.laht.threekt.Canvas
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.controls.OrbitControls
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.Clock
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.core.Uniform
import info.laht.threekt.materials.ShaderMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.objects.Points
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import kotlin.math.sin

object PointsExampleWaves {

    private const val SEPARATION = 100
    private const val AMOUNTX = 250
    private const val AMOUNTY = 250

    private val vertexShader = """
        
        attribute float scale;
        void main() {
            vec4 mvPosition = modelViewMatrix * vec4( position, 1.0 );
            gl_PointSize = scale * ( 300.0 / - mvPosition.z );
            gl_Position = projectionMatrix * mvPosition;
        }
        
    """.trimIndent()

    private val fragmentShader = """
        
        uniform vec3 color;
        void main() {
            if ( length( gl_PointCoord - vec2( 0.5, 0.5 ) ) > 0.475 ) discard;
            gl_FragColor = vec4( color, 1.0 );
        }

        
    """.trimIndent()

    @JvmStatic
    fun main(args: Array<String>) {

        Canvas(resizeable = true).use { canvas ->

            val scene = Scene()
            val renderer = GLRenderer(canvas.width, canvas.height)

            canvas.onWindowResize = { w, h ->
                renderer.setSize(w, h)
            }

            val camera = PerspectiveCamera(75, canvas.aspect, 1, 100000).apply {
                position.z = 1000f
            }
            OrbitControls(camera, canvas).apply {
                zoomSpeed *= 5
            }

            val numParticles = AMOUNTX * AMOUNTY
            val positions = FloatBufferAttribute(numParticles * 3, 3)
            val scales = FloatBufferAttribute(numParticles, 1)

            var i = 0
            var j = 0
            for (ix in 0 until AMOUNTX) {

                for (iy in 0 until AMOUNTY) {

                    positions[i] = (ix * SEPARATION - ((AMOUNTX * SEPARATION)).toFloat() / 2) // x
                    positions[i + 1] = 0f // y
                    positions[i + 2] = (iy * SEPARATION - ((AMOUNTY * SEPARATION)).toFloat() / 2) // z

                    scales[j] = 1f

                    i += 3
                    j++

                }

            }

            val geometry = BufferGeometry()
            geometry.addAttribute("position", positions)
            geometry.addAttribute("scale", scales)

            val material = ShaderMaterial().apply {
                uniforms["color"] = Uniform(Color(0xffffff))
                vertexShader = PointsExampleWaves.vertexShader
                fragmentShader = PointsExampleWaves.fragmentShader
            }

            val particles = Points(geometry, material)
            scene.add(particles)

            var count = 0f
            val clock = Clock()
            canvas.animate {

                camera.lookAt(scene.position)

                i = 0
                j = 0

                for (ix in 0 until AMOUNTX) {

                    for (iy in 0 until AMOUNTY) {

                        positions[i + 1] = (sin((ix + count) * 0.3f) * 50) +
                                (sin((iy + count) * 0.5f) * 50)
                        scales[j] = (sin((ix + count) * 0.3f) + 1) * 8 +
                                (sin((iy + count) * 0.5f) + 1) * 8
                        i += 3
                        j++

                    }

                }

                positions.needsUpdate = true
                scales.needsUpdate = true

                renderer.render(scene, camera)

                count += 10f * clock.getDelta()

            }

        }

    }

}
