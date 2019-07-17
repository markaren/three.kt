package info.laht.threekt.renderers.opengl

import info.laht.threekt.*
import info.laht.threekt.materials.RawShaderMaterial
import info.laht.threekt.materials.ShaderMaterial
import info.laht.threekt.renderers.GLRenderer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import java.lang.IllegalArgumentException

class GLProgram internal constructor(
    val renderer: GLRenderer,
    val code: String,
    material: ShaderMaterial,
    val shader: GLShader,
    val parameters: Map<String, Any>,
    val capabilities: GLCapabilities
) {

    val id = programIdCount++

    var usedTimes = 1
    var program = GL20.glCreateProgram()
        private set

    private val defines = material.defines

    private var vertexShader = shader.vertexShader
    private var fragmentShader = shader.fragmentShader

    private val shadowMapTypeDefine = "SHADOWMAP_TYPE_BASIC"


    private val customDefines = generateDefines(defines)

    init {

        val prefixVertex: MutableList<String>
        val prefixFragment: MutableList<String>

        if (material is RawShaderMaterial) {

            prefixVertex = mutableListOf(
                customDefines
            )

            if (prefixVertex.size > 0) {
                prefixVertex.add("\n")
            }

            prefixFragment = mutableListOf(
                customDefines
            )

            if (prefixFragment.size > 0) {
                prefixFragment.add("\n")
            }

        } else {

            prefixFragment = mutableListOf(

//                "precision " + parameters.precision + " float;",
//                "precision " + parameters.precision + " int;",
//
//                "#define SHADER_NAME " + shader.name,
//
//                customDefines,
//
//                parameters.supportsVertexTextures ? "#define VERTEX_TEXTURES" : "",
//
//            "#define GAMMA_FACTOR " + gammaFactorDefine,
//
//            "#define MAX_BONES " + parameters.maxBones,
//            ( parameters.useFog && parameters.fog ) ? "#define USE_FOG" : "",
//            ( parameters.useFog && parameters.fogExp ) ? "#define FOG_EXP2" : "",
//
//            parameters.map ? "#define USE_MAP" : "",
//            parameters.envMap ? "#define USE_ENVMAP" : "",
//            parameters.envMap ? "#define " + envMapModeDefine : "",
//            parameters.lightMap ? "#define USE_LIGHTMAP" : "",
//            parameters.aoMap ? "#define USE_AOMAP" : "",
//            parameters.emissiveMap ? "#define USE_EMISSIVEMAP" : "",
//            parameters.bumpMap ? "#define USE_BUMPMAP" : "",
//            parameters.normalMap ? "#define USE_NORMALMAP" : "",
//            ( parameters.normalMap && parameters.objectSpaceNormalMap ) ? "#define OBJECTSPACE_NORMALMAP" : "",
//            parameters.displacementMap && parameters.supportsVertexTextures ? "#define USE_DISPLACEMENTMAP" : "",
//            parameters.specularMap ? "#define USE_SPECULARMAP" : "",
//            parameters.roughnessMap ? "#define USE_ROUGHNESSMAP" : "",
//            parameters.metalnessMap ? "#define USE_METALNESSMAP" : "",
//            parameters.alphaMap ? "#define USE_ALPHAMAP" : "",
//
//            parameters.vertexTangents ? "#define USE_TANGENT" : "",
//            parameters.vertexColors ? "#define USE_COLOR" : "",
//
//            parameters.flatShading ? "#define FLAT_SHADED" : "",
//
//            parameters.skinning ? "#define USE_SKINNING" : "",
//            parameters.useVertexTexture ? "#define BONE_TEXTURE" : "",
//
//            parameters.morphTargets ? "#define USE_MORPHTARGETS" : "",
//            parameters.morphNormals && parameters.flatShading === false ? "#define USE_MORPHNORMALS" : "",
//            parameters.doubleSided ? "#define DOUBLE_SIDED" : "",
//            parameters.flipSided ? "#define FLIP_SIDED" : "",
//
//            parameters.shadowMapEnabled ? "#define USE_SHADOWMAP" : "",
//            parameters.shadowMapEnabled ? "#define " + shadowMapTypeDefine : "",
//
//            parameters.sizeAttenuation ? "#define USE_SIZEATTENUATION" : "",
//
//            parameters.logarithmicDepthBuffer ? "#define USE_LOGDEPTHBUF" : "",
//            parameters.logarithmicDepthBuffer && ( capabilities.isWebGL2 || extensions.get( "EXT_frag_depth" ) ) ? "#define USE_LOGDEPTHBUF_EXT" : "",
//
//            "uniform mat4 modelMatrix;",
//            "uniform mat4 modelViewMatrix;",
//            "uniform mat4 projectionMatrix;",
//            "uniform mat4 viewMatrix;",
//            "uniform mat3 normalMatrix;",
//            "uniform vec3 cameraPosition;",
//
//            "attribute vec3 position;",
//            "attribute vec3 normal;",
//            "attribute vec2 uv;",
//
//            "#ifdef USE_TANGENT",
//
//            "	attribute vec4 tangent;",
//
//            "#endif",
//
//            "#ifdef USE_COLOR",
//
//            "	attribute vec3 color;",
//
//            "#endif",
//
//            "#ifdef USE_MORPHTARGETS",
//
//            "	attribute vec3 morphTarget0;",
//            "	attribute vec3 morphTarget1;",
//            "	attribute vec3 morphTarget2;",
//            "	attribute vec3 morphTarget3;",
//
//            "	#ifdef USE_MORPHNORMALS",
//
//            "		attribute vec3 morphNormal0;",
//            "		attribute vec3 morphNormal1;",
//            "		attribute vec3 morphNormal2;",
//            "		attribute vec3 morphNormal3;",
//
//            "	#else",
//
//            "		attribute vec3 morphTarget4;",
//            "		attribute vec3 morphTarget5;",
//            "		attribute vec3 morphTarget6;",
//            "		attribute vec3 morphTarget7;",
//
//            "	#endif",
//
//            "#endif",
//
//            "#ifdef USE_SKINNING",
//
//            "	attribute vec4 skinIndex;",
//            "	attribute vec4 skinWeight;",
//
//            "#endif",
//
//            "\n"

            )

        }

    }

    fun destroy() {
        program = -1
    }

    private companion object {

        var programIdCount = 0

        fun getEncodingComponents(encoding: Int): Pair<String, String> {

            return when (encoding) {

                LinearEncoding -> "Linear" to "( value )"
                sRGBEncoding -> "sRGB" to "( value )"
                RGBEEncoding -> "RGBE" to "( value )"
                RGBM7Encoding -> "RGBM" to "( value, 7.0 )"
                RGBM16Encoding -> "RGBM" to "( value, 16.0 )"
                RGBDEncoding -> "RGBD" to "( value, 256.0 )"
                GammaEncoding -> "Gamma" to "( value, float( GAMMA_FACTOR ) )"
                else -> throw IllegalArgumentException("unsupported encoding: $encoding")

            }

        }

        fun addLineNumbers(string: String): String {
            val lines = string.split("\n").toMutableList()
            for (i in 0 until lines.size) {
                lines[i] = "${(i + 1)}:${lines[i]}"
            }
            return lines.joinToString("\n")
        }

        fun getShaderErrors(shader: Int, type: Int): String {

            val status = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS)
            val log = GL20.glGetShaderInfoLog(shader).trim()

            if (status == GL11.GL_TRUE && log.isEmpty()) return ""

            val source = GL20.glGetShaderSource(shader)

            return "gl.getShaderInfoLog() $type \n $log ${addLineNumbers(
                source
            )}"

        }

        fun getTexelDecodingFunction(functionName: String, encoding: Int): String {
            val components = getEncodingComponents(encoding)
            return "vec4 $functionName( vec4 value ) { return ${components.first}ToLinear ${components.second}; }";
        }

        fun getTexelEncodingFunction(functionName: String, encoding: Int): String {
            val components = getEncodingComponents(encoding)
            return "vec4 $functionName( vec4 value ) { return LinearTo${components.first}${components.second}; }";
        }

        fun getToneMappingFunction(functionName: String, toneMapping: Int): String {

            val toneMappingName = when (toneMapping) {

                LinearToneMapping -> "Linear"
                ReinhardToneMapping -> "Reinhard"
                Uncharted2ToneMapping -> "Uncharted2"
                CineonToneMapping -> "OptimizedCineon"
                ACESFilmicToneMapping -> "ACESFilmic"
                else -> throw IllegalArgumentException("unsupported toneMapping: $toneMapping")

            }

            return "vec3 $functionName( vec3 color ) { return ${toneMappingName}ToneMapping( color ); }";

        }

        fun generateDefines(defines: Map<String, Boolean>): String {

            return defines.mapNotNull { (key, value) ->
                if (!value) {
                    null
                } else {
                    "#define $key $value"
                }
            }.joinToString { "\n" }

        }

        fun replaceLightNums(string: String, parameters: Any) {

        }


    }

}
