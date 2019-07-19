package info.laht.threekt.renderers.shaders

import info.laht.threekt.core.Uniform
import info.laht.threekt.math.Matrix3
import java.lang.Exception

object ShaderLib {

    val basic = Shader(
        mergeUniforms(
            listOf(
                UniformsLib.common,
                UniformsLib.specularmap,
                UniformsLib.envmap,
                UniformsLib.aomap,
                UniformsLib.lightmap,
                UniformsLib.fog
            )
        ),
        ShaderChunk.meshbasic_vert,
        ShaderChunk.meshbasic_frag
    )

    val lambert = Shader(
        mergeUniforms(
            listOf(
                UniformsLib.common,
                UniformsLib.specularmap,
                UniformsLib.envmap,
                UniformsLib.aomap,
                UniformsLib.lightmap,
                UniformsLib.emissivemap,
                UniformsLib.fog
//            UniformsLib.lights
            )
        ),
        ShaderChunk.meshlambert_vert,
        ShaderChunk.meshlambert_frag
    )

    val matcap = Shader(
        mergeUniforms(
            listOf(
                UniformsLib.common,
                UniformsLib.bumpmap,
                UniformsLib.normalmap,
                UniformsLib.displacementmap,
                UniformsLib.fog,
                mapOf(
                    "matcap" to Uniform(null)
                )
            )
        ),
        ShaderChunk.meshmatcap_vert,
        ShaderChunk.meshmatcap_frag
    )

    val points = Shader(
        mergeUniforms(
            listOf(
                UniformsLib.points,
                UniformsLib.fog
            )
        ),
        ShaderChunk.points_vert,
        ShaderChunk.points_frag
    )

    val dashed = Shader(
        mergeUniforms(
            listOf(
                UniformsLib.common,
                UniformsLib.fog,
                mapOf(
                    "scale" to Uniform(1f),
                    "dashSize" to Uniform(1f),
                    "totalSize" to Uniform(2f)
                )
            )
        ),
        ShaderChunk.linedashed_vert,
        ShaderChunk.linedashed_frag
    )

    val depth = Shader(
        mergeUniforms(
            listOf(
                UniformsLib.common,
                UniformsLib.displacementmap
            )
        ),
        ShaderChunk.depth_vert,
        ShaderChunk.depth_frag
    )

    val normal = Shader(
        mergeUniforms(
            listOf(
                UniformsLib.common,
                UniformsLib.bumpmap,
                UniformsLib.normalmap,
                UniformsLib.displacementmap,
                mapOf(
                    "opacity" to Uniform(1f)
                )
            )
        ),
        ShaderChunk.normal_vert,
        ShaderChunk.normal_frag
    )

    val sprite = Shader(
        mergeUniforms(
            listOf(
                UniformsLib.sprite,
                UniformsLib.fog
            )
        ),
        ShaderChunk.sprite_vert,
        ShaderChunk.sprite_frag
    )

    val background = Shader(
        mapOf(
            "uvTransform" to Uniform(Matrix3()),
            "t2D" to Uniform(null)
        ),
        ShaderChunk.background_vert,
        ShaderChunk.background_frag
    )

    val cube = Shader(
        mapOf(
            "tCube" to Uniform(null),
            "tFlip" to Uniform(-1),
            "opacity" to Uniform(1f)
        ),
        ShaderChunk.cube_vert,
        ShaderChunk.cube_frag
    )

    operator fun get(name: String): Shader? {

        return try {
            ShaderLib::class.java.getDeclaredField(name).get(null) as Shader
        } catch (ex: Exception) {
            null
        }

    }

    class Shader(
        val uniforms: Map<String, Uniform>,
        val vertexShader: String,
        val fragmentShader: String
    )
    
}
