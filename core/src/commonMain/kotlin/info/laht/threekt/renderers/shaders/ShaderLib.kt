package info.laht.threekt.renderers.shaders

import info.laht.threekt.core.Shader
import info.laht.threekt.core.Uniform
import info.laht.threekt.math.Color
import info.laht.threekt.math.Matrix3
import info.laht.threekt.math.Vector3

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
                            UniformsLib.fog,
                            UniformsLib.lights,
                            mapOf(
                                    "emissive" to Uniform(Color(0x000000))
                            )
                    )
            ),
            ShaderChunk.meshlambert_vert,
            ShaderChunk.meshlambert_frag
    )

    val phong = Shader(
            mergeUniforms(
                    listOf(
                            UniformsLib.common,
                            UniformsLib.specularmap,
                            UniformsLib.envmap,
                            UniformsLib.aomap,
                            UniformsLib.lightmap,
                            UniformsLib.emissivemap,
                            UniformsLib.bumpmap,
                            UniformsLib.normalmap,
                            UniformsLib.displacementmap,
                            UniformsLib.gradientmap,
                            UniformsLib.fog,
                            UniformsLib.lights,
                            mapOf(
                                    "emissive" to Uniform(Color(0x000000)),
                                    "specular" to Uniform(Color(0x111111)),
                                    "shininess" to Uniform(30f)
                            )
                    )
            ),
            ShaderChunk.meshphong_vert,
            ShaderChunk.meshphong_frag
    )

    val standard = Shader(

            mergeUniforms(
                    listOf(
                            UniformsLib.common,
                            UniformsLib.envmap,
                            UniformsLib.aomap,
                            UniformsLib.lightmap,
                            UniformsLib.emissivemap,
                            UniformsLib.bumpmap,
                            UniformsLib.normalmap,
                            UniformsLib.displacementmap,
                            UniformsLib.roughnessmap,
                            UniformsLib.metalnessmap,
                            UniformsLib.fog,
                            UniformsLib.lights,
                            mapOf(
                                    "emissive" to Uniform(Color(0x000000)),
                                    "roughness" to Uniform(0.5f),
                                    "metalness" to Uniform(0.5f),
                                    "envMapIntensity" to Uniform(1f) // temporary
                            )
                    )
            ),

            ShaderChunk.meshphysical_vert,
            ShaderChunk.meshphysical_frag

    )

    val matcap = Shader(
            mergeUniforms(
                    listOf(
                            UniformsLib.common,
                            UniformsLib.bumpmap,
                            UniformsLib.normalmap,
                            UniformsLib.displacementmap,
                            UniformsLib.fog,
                            mutableMapOf(
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
                            mutableMapOf(
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
                            mutableMapOf(
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
            mutableMapOf(
                    "uvTransform" to Uniform(Matrix3()),
                    "t2D" to Uniform(null)
            ),
            ShaderChunk.background_vert,
            ShaderChunk.background_frag
    )

    val cube = Shader(
            mutableMapOf(
                    "tCube" to Uniform(null),
                    "tFlip" to Uniform(-1),
                    "opacity" to Uniform(1f)
            ),
            ShaderChunk.cube_vert,
            ShaderChunk.cube_frag
    )

    val equirect = Shader(
            mutableMapOf(
                    "tEquirect" to Uniform(null)
            ),
            ShaderChunk.equirect_vert,
            ShaderChunk.equirect_frag
    )

    val distanceRGBA = Shader(
            mergeUniforms(
                    listOf(
                            UniformsLib.common,
                            UniformsLib.displacementmap,
                            mutableMapOf(
                                    "referencePosition" to Uniform(Vector3()),
                                    "nearDistance" to Uniform(1f),
                                    "farDistance" to Uniform(1000f)
                            )
                    )
            ),
            ShaderChunk.distanceRGBA_vert,
            ShaderChunk.distanceRGBA_frag
    )

    val shadow = Shader(
            mergeUniforms(
                    listOf(
                            UniformsLib.lights,
                            UniformsLib.fog,
                            mutableMapOf(
                                    "color" to Uniform(Color(0x000000)),
                                    "opacity" to Uniform(1f)
                            )
                    )
            ),
            ShaderChunk.shadow_vert,
            ShaderChunk.shadow_frag
    )

    val physical = Shader(
            mergeUniforms(
                    listOf(
                            standard.uniforms,
                            UniformsLib.bumpmap,
                            UniformsLib.normalmap,
                            UniformsLib.displacementmap,
                            mutableMapOf(
                                    "clearCoat" to Uniform(0f),
                                    "clearCoatRoughness" to Uniform(0f)
                            )
                    )
            ),
            ShaderChunk.normal_vert,
            ShaderChunk.normal_frag
    )

    private val map = mapOf(
            "basic" to basic,
            "lambert" to lambert,
            "phong" to phong,
            "standard" to standard,
            "matcap" to matcap,
            "points" to points,
            "dashed" to dashed,
            "depth" to depth,
            "normal" to normal,
            "sprite" to sprite,
            "background" to background,
            "cube" to cube,
            "equirect" to equirect,
            "distanceRGBA" to distanceRGBA,
            "shadow" to shadow,
            "physical" to physical

    )

    operator fun get(name: String): Shader {
        return map.getValue(name)
    }

}
