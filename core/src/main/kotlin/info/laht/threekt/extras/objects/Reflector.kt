package info.laht.threekt.extras.objects

import info.laht.threekt.LinearFilter
import info.laht.threekt.RGBFormat
import info.laht.threekt.cameras.Camera
import info.laht.threekt.cameras.OrthographicCamera
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.Shader
import info.laht.threekt.core.Uniform
import info.laht.threekt.materials.ShaderMaterial
import info.laht.threekt.math.*
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.GLRenderTarget
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.renderers.shaders.cloneUniforms
import info.laht.threekt.renderers.shaders.mergeUniforms
import info.laht.threekt.scenes.Scene
import kotlin.math.sign

class Reflector(
    geometry: BufferGeometry,
    private val options: Options = Options()
) : Mesh(geometry, ShaderMaterial()) {

    private val reflectorPlane = Plane()
    private val normal = Vector3()
    private val reflectorWorldPosition = Vector3()
    private val cameraWorldPosition = Vector3()
    private val rotationMatrix = Matrix4()
    private val lookAtPosition = Vector3(0, 0, -1)
    private val clipPlane = Vector4()

    private val view = Vector3()
    private val target = Vector3()
    private val q = Vector4()

    private val textureMatrix = Matrix4()
    private val virtualCamera = PerspectiveCamera()

    private val renderTarget: GLRenderTarget

    init {

        renderTarget = GLRenderTarget(
            options.textureWidth, options.textureHeight, GLRenderTarget.Options(
                minFilter = LinearFilter,
                magFilter = LinearFilter,
                format = RGBFormat,
                stencilBuffer = false
            )
        )

        if (!isPowerOfTwo(options.textureWidth) || !isPowerOfTwo(options.textureHeight)) {
            renderTarget.texture.generateMipmaps = false
        }

        material.also {

            it.uniforms.putAll(cloneUniforms(options.shader.uniforms))

            it.uniforms["tDiffuse"]!!.value = renderTarget.texture
            it.uniforms["color"]!!.value = options.color
            it.uniforms["textureMatrix"]!!.value = textureMatrix

            it.vertexShader = ReflectorShader.vertexShader
            it.fragmentShader = ReflectorShader.fragmentShader

        }

        onBeforeRender = { renderer, scene, camera, _, _, _ ->

            onBeforeRender(renderer, scene, camera)

        }

    }

    private fun onBeforeRender(renderer: GLRenderer, scene: Scene, camera: Camera) {

        if ("recursion" in camera.userData) {

            val value = camera.userData["recursion"] as Int
            if (value == options.recursion) return

            camera.userData["recursion"] = value + 1

        }

        reflectorWorldPosition.setFromMatrixPosition(matrixWorld)
        cameraWorldPosition.setFromMatrixPosition(camera.matrixWorld)

        rotationMatrix.extractRotation(matrixWorld)

        normal.set(0, 0, 1)
        normal.applyMatrix4(rotationMatrix)

        view.subVectors(reflectorWorldPosition, cameraWorldPosition)

        if ((view.dot(normal) > 0)) return

        view.reflect(normal).negate()
        view.add(reflectorWorldPosition)

        rotationMatrix.extractRotation(camera.matrixWorld)

        lookAtPosition.set(0, 0, -1)
        lookAtPosition.applyMatrix4(rotationMatrix)
        lookAtPosition.add(cameraWorldPosition)

        target.subVectors(reflectorWorldPosition, lookAtPosition)
        target.reflect(normal).negate()
        target.add(reflectorWorldPosition)

        virtualCamera.position.copy(view)
        virtualCamera.up.set(0, 1, 0)
        virtualCamera.up.applyMatrix4(rotationMatrix)
        virtualCamera.up.reflect(normal)
        virtualCamera.lookAt(target)

        virtualCamera.far = when (camera) {
            is PerspectiveCamera -> camera.far
            is OrthographicCamera -> camera.far
            else -> throw IllegalStateException()
        }

        virtualCamera.updateMatrixWorld()
        virtualCamera.projectionMatrix.copy(camera.projectionMatrix)

        virtualCamera.userData["recursion"] = 0

        // Update the texture matrix
        textureMatrix.set(
            0.5f, 0.0f, 0.0f, 0.5f,
            0.0f, 0.5f, 0.0f, 0.5f,
            0.0f, 0.0f, 0.5f, 0.5f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
        textureMatrix.multiply(virtualCamera.projectionMatrix)
        textureMatrix.multiply(virtualCamera.matrixWorldInverse)
        textureMatrix.multiply(matrixWorld)

        // Now update projection matrix with new clip plane, implementing code from: http://www.terathon.com/code/oblique.html
        // Paper explaining this technique: http://www.terathon.com/lengyel/Lengyel-Oblique.pdf
        reflectorPlane.setFromNormalAndCoplanarPoint(normal, reflectorWorldPosition)
        reflectorPlane.applyMatrix4(virtualCamera.matrixWorldInverse)

        clipPlane.set(
            reflectorPlane.normal.x,
            reflectorPlane.normal.y,
            reflectorPlane.normal.z,
            reflectorPlane.constant
        )

        val projectionMatrix = virtualCamera.projectionMatrix

        q.x = (sign(clipPlane.x) + projectionMatrix.elements[8]) / projectionMatrix.elements[0]
        q.y = (sign(clipPlane.y) + projectionMatrix.elements[9]) / projectionMatrix.elements[5]
        q.z = -1.0f
        q.w = (1.0f + projectionMatrix.elements[10]) / projectionMatrix.elements[14]

        // Calculate the scaled plane vector
        clipPlane.multiplyScalar(2f / clipPlane.dot(q))

        // Replacing the third row of the projection matrix
        projectionMatrix.elements[2] = clipPlane.x
        projectionMatrix.elements[6] = clipPlane.y
        projectionMatrix.elements[10] = clipPlane.z + 1f - options.clipBias
        projectionMatrix.elements[14] = clipPlane.w

        // Render

        visible = false

        val currentRenderTarget = renderer.getRenderTarget()

        val currentShadowAutoUpdate = renderer.shadowMap.autoUpdate

        renderer.shadowMap.autoUpdate = false // Avoid re-computing shadows

        renderer.setRenderTarget(renderTarget)
        renderer.clear()
        renderer.render(scene, virtualCamera, false)

        renderer.shadowMap.autoUpdate = currentShadowAutoUpdate

        renderer.setRenderTarget(currentRenderTarget)

        // Restore viewport

        visible = true

    }

    class Options(
        val color: Color = Color(0x7F7F7F),
        val textureWidth: Int = 512,
        val textureHeight: Int = 512,
        val clipBias: Float = 0f,
        val shader: Shader = ReflectorShader,
        val recursion: Int = 0
    )

}

val ReflectorShader by lazy {

    Shader(
        mutableMapOf(
            "color" to Uniform(null),
            "tDiffuse" to Uniform(null),
            "textureMatrix" to Uniform(null)
        ),
        vertexShader = """
            uniform mat4 textureMatrix;
            varying vec4 vUv;
    
            void main() {

			vUv = textureMatrix * vec4( position, 1.0 );

			gl_Position = projectionMatrix * modelViewMatrix * vec4( position, 1.0 );

		}
        """.trimIndent(),
        fragmentShader = """
            uniform vec3 color;
            uniform sampler2D tDiffuse;
            varying vec4 vUv;
    
            float blendOverlay( float base, float blend ) {
    
                return( base < 0.5 ? ( 2.0 * base * blend ) : ( 1.0 - 2.0 * ( 1.0 - base ) * ( 1.0 - blend ) ) );
    
            }

            vec3 blendOverlay( vec3 base, vec3 blend ) {
    
                return vec3( blendOverlay( base.r, blend.r ), blendOverlay( base.g, blend.g ), blendOverlay( base.b, blend.b ) );
    
            }
    
            void main() {
    
                vec4 base = texture2DProj( tDiffuse, vUv );
                gl_FragColor = vec4( blendOverlay( base.rgb, color ), 1.0 );
    
            }
        """.trimIndent()
    )

}

