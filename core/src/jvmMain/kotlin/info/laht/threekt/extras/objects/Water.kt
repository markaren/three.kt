package info.laht.threekt.extras.objects

import info.laht.threekt.Side
import info.laht.threekt.TextureFilter
import info.laht.threekt.TextureFormat
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
import info.laht.threekt.renderers.shaders.ShaderChunk
import info.laht.threekt.renderers.shaders.UniformsLib
import info.laht.threekt.renderers.shaders.cloneUniforms
import info.laht.threekt.renderers.shaders.mergeUniforms
import info.laht.threekt.scenes.Scene
import info.laht.threekt.textures.Texture
import kotlin.math.sign

class Water(
        geometry: BufferGeometry,
        private val options: Options
) : Mesh(geometry, ShaderMaterial()) {

    val uniforms = (material as ShaderMaterial).uniforms

    private var mirrorPlane = Plane()
    private var normal = Vector3()
    private var mirrorWorldPosition = Vector3()
    private var cameraWorldPosition = Vector3()
    private var rotationMatrix = Matrix4()
    private var lookAtPosition = Vector3(0f, 0f, -1f)
    private var clipPlane = Vector4()

    private var view = Vector3()
    private var target = Vector3()
    private var q = Vector4()

    private var textureMatrix = Matrix4()

    private var mirrorCamera = PerspectiveCamera()

    private val eye = options.eye

    private val renderTarget: GLRenderTarget

    init {

        renderTarget = GLRenderTarget(
                options.textureWidth, options.textureHeight, GLRenderTarget.Options(
                minFilter = TextureFilter.Linear,
                magFilter = TextureFilter.Linear,
                format = TextureFormat.RGB,
                stencilBuffer = false
        )
        )

        if (!isPowerOfTwo(options.textureWidth) || !isPowerOfTwo(options.textureHeight)) {
            renderTarget.texture.generateMipmaps = false
        }

        material.also {

            it.vertexShader = mirrorShader.vertexShader
            it.fragmentShader = mirrorShader.fragmentShader
            it.uniforms.putAll(cloneUniforms(mirrorShader.uniforms))

            it.transparent = true
            it.lights = true
            it.side = options.side
            it.fog = options.fog


            it.uniforms["mirrorSampler"]!!.value = renderTarget.texture
            it.uniforms["textureMatrix"]!!.value = textureMatrix
            it.uniforms["alpha"]!!.value = options.alpha
            it.uniforms["time"]!!.value = options.time
            it.uniforms["normalSampler"]!!.value = options.waterNormals
            it.uniforms["sunColor"]!!.value = options.sunColor
            it.uniforms["waterColor"]!!.value = options.waterColor
            it.uniforms["sunDirection"]!!.value = options.sunDirection
            it.uniforms["distortionScale"]!!.value = options.distortionScale

            it.uniforms["eye"]!!.value = options.eye

        }


        onBeforeRender = { renderer, scene, camera, _, _, _ ->

            onBeforeRender(renderer as GLRenderer, scene, camera)

        }


    }

    private fun onBeforeRender(renderer: GLRenderer, scene: Scene, camera: Camera) {
        mirrorWorldPosition.setFromMatrixPosition(matrixWorld)
        cameraWorldPosition.setFromMatrixPosition(camera.matrixWorld)

        rotationMatrix.extractRotation(matrixWorld)

        normal.set(0f, 0f, 1f)
        normal.applyMatrix4(rotationMatrix)

        view.subVectors(mirrorWorldPosition, cameraWorldPosition)

        // Avoid rendering when mirror is facing away

        if (view.dot(normal) > 0) return

        view.reflect(normal).negate()
        view.add(mirrorWorldPosition)

        rotationMatrix.extractRotation(camera.matrixWorld)

        lookAtPosition.set(0f, 0f, -1f)
        lookAtPosition.applyMatrix4(rotationMatrix)
        lookAtPosition.add(cameraWorldPosition)

        target.subVectors(mirrorWorldPosition, lookAtPosition)
        target.reflect(normal).negate()
        target.add(mirrorWorldPosition)

        mirrorCamera.position.copy(view)
        mirrorCamera.up.set(0f, 1f, 0f)
        mirrorCamera.up.applyMatrix4(rotationMatrix)
        mirrorCamera.up.reflect(normal)
        mirrorCamera.lookAt(target)

        mirrorCamera.far = when (camera) {
            is PerspectiveCamera -> camera.far
            is OrthographicCamera -> camera.far
            else -> throw IllegalStateException()
        }

        mirrorCamera.updateMatrixWorld()
        mirrorCamera.projectionMatrix.copy(camera.projectionMatrix)

        // Update the texture matrix
        textureMatrix.set(
                0.5f, 0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.0f, 0.5f,
                0.0f, 0.0f, 0.5f, 0.5f,
                0.0f, 0.0f, 0.0f, 1.0f
        )
        textureMatrix.multiply(mirrorCamera.projectionMatrix)
        textureMatrix.multiply(mirrorCamera.matrixWorldInverse)

        // Now update projection matrix with new clip plane, implementing code from: http://www.terathon.com/code/oblique.html
        // Paper explaining this technique: http://www.terathon.com/lengyel/Lengyel-Oblique.pdf
        mirrorPlane.setFromNormalAndCoplanarPoint(normal, mirrorWorldPosition)
        mirrorPlane.applyMatrix4(mirrorCamera.matrixWorldInverse)

        clipPlane.set(mirrorPlane.normal.x, mirrorPlane.normal.y, mirrorPlane.normal.z, mirrorPlane.constant)

        val projectionMatrix = mirrorCamera.projectionMatrix

        q.x = (sign(clipPlane.x) + projectionMatrix.elements[8]) / projectionMatrix.elements[0]
        q.y = (sign(clipPlane.y) + projectionMatrix.elements[9]) / projectionMatrix.elements[5]
        q.z = -1.0f
        q.w = (1.0f + projectionMatrix.elements[10]) / projectionMatrix.elements[14]

        // Calculate the scaled plane vector
        clipPlane.multiplyScalar(2f / clipPlane.dot(q))

        // Replacing the third row of the projection matrix
        projectionMatrix.elements[2] = clipPlane.x
        projectionMatrix.elements[6] = clipPlane.y
        projectionMatrix.elements[10] = clipPlane.z + 1.0f - options.clipBias
        projectionMatrix.elements[14] = clipPlane.w

        eye.setFromMatrixPosition(camera.matrixWorld)

        //

        val currentRenderTarget = renderer.getRenderTarget()


        val currentShadowAutoUpdate = renderer.shadowMap.autoUpdate

        visible = false

        renderer.shadowMap.autoUpdate = false // Avoid re-computing shadows

        renderer.setRenderTarget(renderTarget)
        renderer.clear()
        renderer.render(scene, mirrorCamera)

        visible = true

        renderer.shadowMap.autoUpdate = currentShadowAutoUpdate

        renderer.setRenderTarget(currentRenderTarget)
    }

    class Options @JvmOverloads constructor(
            val waterNormals: Texture,

            var textureWidth: Int = 512,
            var textureHeight: Int = 512,

            var clipBias: Float = 0f,
            var alpha: Float = 1f,
            var time: Float = 0f,

            var sunDirection: Vector3 = Vector3(0.70707f, 0.70707f, 0.0f),
            var sunColor: Color = Color(0xffffff),
            var waterColor: Color = Color(0x7F7F7F),
            var eye: Vector3 = Vector3(),
            var distortionScale: Float = 20f,
            var side: Side = Side.Front,
            var fog: Boolean = false

    )

}

private val mirrorShader by lazy {

    Shader(
            uniforms = mergeUniforms(
                    listOf(
                            UniformsLib.fog,
                            UniformsLib.lights,
                            mapOf(
                                    "normalSampler" to Uniform(null),
                                    "mirrorSampler" to Uniform(null),
                                    "alpha" to Uniform(1f),
                                    "time" to Uniform(0f),
                                    "size" to Uniform(1f),
                                    "distortionScale" to Uniform(20f),
                                    "textureMatrix" to Uniform(Matrix4()),
                                    "sunColor" to Uniform(Color(0x7F7F7F)),
                                    "sunDirection" to Uniform(Vector3(0.70707f, 0.70707f, 0f)),
                                    "eye" to Uniform(Vector3()),
                                    "waterColor" to Uniform(Color(0x555555))
                            )
                    )
            ),
            vertexShader = """
                uniform mat4 textureMatrix;
                uniform float time;
    
                varying vec4 mirrorCoord;
                varying vec4 worldPosition;
    
                ${ShaderChunk.fog_pars_vertex}
                ${ShaderChunk.shadowmap_pars_vertex}
    
                void main() {
                	mirrorCoord = modelMatrix * vec4( position, 1.0 );
                	worldPosition = mirrorCoord.xyzw;
                	mirrorCoord = textureMatrix * mirrorCoord;
                	vec4 mvPosition =  modelViewMatrix * vec4( position, 1.0 );
                	gl_Position = projectionMatrix * mvPosition;
    
                ${ShaderChunk.fog_vertex}
                ${ShaderChunk.shadowmap_vertex}
    
                }
            """.trimIndent(),
            fragmentShader = """
                uniform sampler2D mirrorSampler;
                uniform float alpha;
                uniform float time;
                uniform float size;
                uniform float distortionScale;
                uniform sampler2D normalSampler;
                uniform vec3 sunColor;
                uniform vec3 sunDirection;
                uniform vec3 eye;
                uniform vec3 waterColor;
    
                varying vec4 mirrorCoord;
                varying vec4 worldPosition;
    
                vec4 getNoise( vec2 uv ) {
                    vec2 uv0 = ( uv / 103.0 ) + vec2(time / 17.0, time / 29.0);
                    vec2 uv1 = uv / 107.0-vec2( time / -19.0, time / 31.0 );
                    vec2 uv2 = uv / vec2( 8907.0, 9803.0 ) + vec2( time / 101.0, time / 97.0 );
                    vec2 uv3 = uv / vec2( 1091.0, 1027.0 ) - vec2( time / 109.0, time / -113.0 );
                    vec4 noise = texture2D( normalSampler, uv0 ) +
                        texture2D( normalSampler, uv1 ) +
                        texture2D( normalSampler, uv2 ) +
                        texture2D( normalSampler, uv3 );
                    return noise * 0.5 - 1.0;
                }
    
                void sunLight( const vec3 surfaceNormal, const vec3 eyeDirection, float shiny, float spec, float diffuse, inout vec3 diffuseColor, inout vec3 specularColor ) {
                    vec3 reflection = normalize( reflect( -sunDirection, surfaceNormal ) );
                    float direction = max( 0.0, dot( eyeDirection, reflection ) );
                    specularColor += pow( direction, shiny ) * sunColor * spec;
                    diffuseColor += max( dot( sunDirection, surfaceNormal ), 0.0 ) * sunColor * diffuse;
                }
    
                ${ShaderChunk.common}
                ${ShaderChunk.packing}
                ${ShaderChunk.bsdfs}
                ${ShaderChunk.fog_pars_fragment}
                ${ShaderChunk.lights_pars_begin}
                ${ShaderChunk.shadowmap_pars_fragment}
                ${ShaderChunk.shadowmask_pars_fragment}
    
                void main() {
                    vec4 noise = getNoise( worldPosition.xz * size );
                    vec3 surfaceNormal = normalize( noise.xzy * vec3( 1.5, 1.0, 1.5 ) );
    
                    vec3 diffuseLight = vec3(0.0);
                    vec3 specularLight = vec3(0.0);
    
                    vec3 worldToEye = eye-worldPosition.xyz;
                    vec3 eyeDirection = normalize( worldToEye );
                    sunLight( surfaceNormal, eyeDirection, 100.0, 2.0, 0.5, diffuseLight, specularLight );
    
                    float distance = length(worldToEye);
    
                    vec2 distortion = surfaceNormal.xz * ( 0.001 + 1.0 / distance ) * distortionScale;
                    vec3 reflectionSample = vec3( texture2D( mirrorSampler, mirrorCoord.xy / mirrorCoord.w + distortion ) );
    
                    float theta = max( dot( eyeDirection, surfaceNormal ), 0.0 );
                    float rf0 = 0.3;
                    float reflectance = rf0 + ( 1.0 - rf0 ) * pow( ( 1.0 - theta ), 5.0 );
                    vec3 scatter = max( 0.0, dot( surfaceNormal, eyeDirection ) ) * waterColor;
                    vec3 albedo = mix( ( sunColor * diffuseLight * 0.3 + scatter ) * getShadowMask(), ( vec3( 0.1 ) + reflectionSample * 0.9 + reflectionSample * specularLight ), reflectance);
                    vec3 outgoingLight = albedo;
                    gl_FragColor = vec4( outgoingLight, alpha );
    
                ${ShaderChunk.tonemapping_fragment}
                ${ShaderChunk.fog_fragment}
    
                }
            """.trimIndent()
    )

}
