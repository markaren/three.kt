package info.laht.threekt.renderers

import info.laht.threekt.Blending
import info.laht.threekt.Side
import info.laht.threekt.cameras.CubeCamera
import info.laht.threekt.core.Shader
import info.laht.threekt.core.Uniform
import info.laht.threekt.geometries.BoxBufferGeometry
import info.laht.threekt.materials.ShaderMaterial
import info.laht.threekt.objects.Mesh
import info.laht.threekt.renderers.shaders.cloneUniforms
import info.laht.threekt.scenes.Scene
import info.laht.threekt.textures.Texture

class GLRenderTargetCube(
    width: Int,
    height: Int,
    options: Options? = null
) : GLRenderTarget(width, height, options) {

    fun fromEquirectangularTexture(renderer: GLRenderer, texture: Texture): GLRenderTargetCube {

        val scene = Scene()

        val shader = Shader(
            uniforms = mutableMapOf(
                "tEquirect" to Uniform(null)
            ),
            vertexShader = """
                    varying vec3 vWorldDirection;

                    vec3 transformDirection( in vec3 dir, in mat4 matrix ) {
        
                        return normalize( ( matrix * vec4( dir, 0.0 ) ).xyz );
        
                    }
        
                    void main() {
        
                        vWorldDirection = transformDirection( position, modelMatrix );
        
                        #include <begin_vertex>
                        #include <project_vertex>
        
                    }
                """.trimIndent(),
            fragmentShader = """
                    uniform sampler2D tEquirect;

                    varying vec3 vWorldDirection;
        
                    #define RECIPROCAL_PI 0.31830988618
                    #define RECIPROCAL_PI2 0.15915494
        
                    void main() {
        
                        vec3 direction = normalize( vWorldDirection );
        
                        vec2 sampleUV;
        
                        sampleUV.y = asin( clamp( direction.y, - 1.0, 1.0 ) ) * RECIPROCAL_PI + 0.5;
        
                        sampleUV.x = atan( direction.z, direction.x ) * RECIPROCAL_PI2 + 0.5;
        
                        gl_FragColor = texture2D( tEquirect, sampleUV );
        
                    }
                """.trimIndent()
        )

        val material = ShaderMaterial().apply {
            type = "CubemapFromEquirect"
            uniforms.putAll(cloneUniforms(shader.uniforms))
            vertexShader = shader.vertexShader
            fragmentShader = shader.fragmentShader
            side = Side.Back
            blending = Blending.None
        }

        material.uniforms["tEquirect"]!!.value = texture

        val mesh = Mesh(BoxBufferGeometry(5f, 5f, 5f), material)
        scene.add(mesh)

        val camera = CubeCamera(1f, 10f, 1)

        camera.renderTarget = this
        camera.renderTarget.texture.name = "CubeCameraTexture"

        camera.update(renderer, scene)

        mesh.geometry.dispose()
        mesh.material.dispose()

        return this
    }

}
