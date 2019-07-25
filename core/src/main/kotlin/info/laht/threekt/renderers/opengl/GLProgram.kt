package info.laht.threekt.renderers.opengl

import info.laht.threekt.*
import info.laht.threekt.materials.Material
import info.laht.threekt.materials.RawShaderMaterial
import info.laht.threekt.materials.ShaderMaterial
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.renderers.shaders.ShaderChunk
import info.laht.threekt.renderers.shaders.ShaderLib
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20

sealed class _GLProgram {
    abstract val id: Int
}

object GLProgramDefault: _GLProgram() {
    override val id = -1

}

class GLProgram internal constructor(
    renderer: GLRenderer,
    val code: String,
    material: Material,
    shader: ShaderLib.Shader,
    parameters: GLPrograms.Parameters
): _GLProgram() {

    override val id = programIdCount++

    var usedTimes = 1

    var program = GL20.glCreateProgram()
        private set

    var glVertexShader = -1
        private set

    var glFragmentShader = -1
        private set

    private val defines = material.defines

    private val customDefines = generateDefines(defines)

    init {

        var vertexShader = shader.vertexShader
        var fragmentShader = shader.fragmentShader

        val shadowMapTypeDefine = "SHADOWMAP_TYPE_BASIC"

        var envMapTypeDefine = "ENVMAP_TYPE_CUBE";
        var envMapModeDefine = "ENVMAP_MODE_REFLECTION";
        var envMapBlendingDefine = "ENVMAP_BLENDING_MULTIPLY";

        if (parameters.envMap) {

            when (material.envMap?.mapping) {

                CubeReflectionMapping, CubeRefractionMapping -> envMapTypeDefine = "ENVMAP_TYPE_CUBE"
                CubeUVReflectionMapping, CubeUVRefractionMapping -> envMapTypeDefine = "ENVMAP_TYPE_CUBE_UV"
                EquirectangularReflectionMapping, EquirectangularRefractionMapping -> envMapTypeDefine =
                    "ENVMAP_TYPE_EQUIREC"
                SphericalReflectionMapping -> envMapTypeDefine = "ENVMAP_TYPE_SPHERE";

            }

            when (material.envMap?.mapping) {
                CubeRefractionMapping, EquirectangularRefractionMapping -> envMapModeDefine = "ENVMAP_MODE_REFRACTION"
            }

            when (material.combine) {
                MultiplyOperation -> envMapBlendingDefine = "ENVMAP_BLENDING_MULTIPLY"
                MixOperation -> envMapBlendingDefine = "ENVMAP_BLENDING_MIX"
                AddOperation -> envMapBlendingDefine = "ENVMAP_BLENDING_ADD"
            }

        }

        val gammaFactorDefine = if (renderer.gammaFactor > 0) renderer.gammaFactor else 1f

        var prefixVertex: String
        var prefixFragment: String

        if (material is RawShaderMaterial) {

            prefixVertex = mutableListOf(
                customDefines
            ).filter { it.isNotEmpty() }.joinToString("\n")

            if (prefixVertex.isNotEmpty()) {
                prefixVertex += "\n"
            }

            prefixFragment = mutableListOf(
                customDefines
            ).filter { it.isNotEmpty() }.joinToString("\n")

            if (prefixFragment.isNotEmpty()) {
                prefixFragment += "\n"
            }

        } else {

            prefixVertex = listOf(

                "precision ${parameters.precision} float;",
                "precision ${parameters.precision} int;",

                "#define SHADER_NAME ${shader.name}",

                customDefines,

                if (parameters.supportsVertexTextures) "#define VERTEX_TEXTURES" else "",

                "#define GAMMA_FACTOR $gammaFactorDefine",

                "#define MAX_BONES ${parameters.maxBones}",
                if (parameters.useFog && parameters.fog) "#define USE_FOG" else "",
                if (parameters.useFog && parameters.fogExp) "#define FOG_EXP2" else "",

                if (parameters.map) "#define USE_MAP" else "",
                if (parameters.envMap) "#define USE_ENVMAP" else "",
                if (parameters.envMap) "#define $envMapModeDefine" else "",
                if (parameters.lightMap) "#define USE_LIGHTMAP" else "",
                if (parameters.aoMap) "#define USE_AOMAP" else "",
                if (parameters.emissiveMap) "#define USE_EMISSIVEMAP" else "",
                if (parameters.bumpMap) "#define USE_BUMPMAP" else "",
                if (parameters.normalMap) "#define USE_NORMALMAP" else "",
                if (parameters.normalMap && parameters.objectSpaceNormalMap) "#define OBJECTSPACE_NORMALMAP" else "",
                if (parameters.displacementMap && parameters.supportsVertexTextures) "#define USE_DISPLACEMENTMAP" else "",
                if (parameters.specularMap) "#define USE_SPECULARMAP" else "",
                if (parameters.roughnessMap) "#define USE_ROUGHNESSMAP" else "",
                if (parameters.metalnessMap) "#define USE_METALNESSMAP" else "",
                if (parameters.alphaMap) "#define USE_ALPHAMAP" else "",

                if (parameters.vertexTangents) "#define USE_TANGENT" else "",
                if (parameters.vertexColors > 0) "#define USE_COLOR" else "",

                if (parameters.flatShading) "#define FLAT_SHADED" else "",

                if (parameters.skinning) "#define USE_SKINNING" else "",
                if (parameters.useVertexTexture) "#define BONE_TEXTURE" else "",

                if (parameters.morphTargets) "#define USE_MORPHTARGETS" else "",
                if (parameters.morphNormals && !parameters.flatShading) "#define USE_MORPHNORMALS" else "",
                if (parameters.doubleSided) "#define DOUBLE_SIDED" else "",
                if (parameters.flipSided) "#define FLIP_SIDED" else "",

                if (parameters.shadowMapEnabled) "#define USE_SHADOWMAP" else "",
                if (parameters.shadowMapEnabled) "#define $shadowMapTypeDefine" else "",

                if (parameters.sizeAttenuation) "#define USE_SIZEATTENUATION" else "",

                "uniform mat4 modelMatrix;",
                "uniform mat4 modelViewMatrix;",
                "uniform mat4 projectionMatrix;",
                "uniform mat4 viewMatrix;",
                "uniform mat3 normalMatrix;",
                "uniform vec3 cameraPosition;",

                "attribute vec3 position;",
                "attribute vec3 normal;",
                "attribute vec2 uv;",

                "#ifdef USE_TANGENT",

                "	attribute vec4 tangent;",

                "#endif",

                "#ifdef USE_COLOR",

                "	attribute vec3 color;",

                "#endif",

                "#ifdef USE_MORPHTARGETS",

                "	attribute vec3 morphTarget0;",
                "	attribute vec3 morphTarget1;",
                "	attribute vec3 morphTarget2;",
                "	attribute vec3 morphTarget3;",

                "	#ifdef USE_MORPHNORMALS",

                "		attribute vec3 morphNormal0;",
                "		attribute vec3 morphNormal1;",
                "		attribute vec3 morphNormal2;",
                "		attribute vec3 morphNormal3;",

                "	#else",

                "		attribute vec3 morphTarget4;",
                "		attribute vec3 morphTarget5;",
                "		attribute vec3 morphTarget6;",
                "		attribute vec3 morphTarget7;",

                "	#endif",

                "#endif",

                "#ifdef USE_SKINNING",

                "	attribute vec4 skinIndex;",
                "	attribute vec4 skinWeight;",

                "#endif",

                "\n"

            ).filter { it.isNotEmpty() }.joinToString("\n")

            prefixFragment = listOf(

                "precision ${parameters.precision} float;",
                "precision ${parameters.precision} int;",

                "#define SHADER_NAME ${shader.name}",

                customDefines,

                if (parameters.alphaTest != 0f) "#define ALPHATEST " + parameters.alphaTest + (if (!(parameters.alphaTest % 1f).isNaN()) "" else ".0") else "", // add ".0" if integer

                "#define GAMMA_FACTOR $gammaFactorDefine",

                if ((parameters.useFog && parameters.fog)) "#define USE_FOG" else "",
                if ((parameters.useFog && parameters.fogExp)) "#define FOG_EXP2" else "",

                if (parameters.map) "#define USE_MAP" else "",
                if (parameters.matcap) "#define USE_MATCAP" else "",
                if (parameters.envMap) "#define USE_ENVMAP" else "",
                if (parameters.envMap) "#define $envMapTypeDefine" else "",
                if (parameters.envMap) "#define $envMapModeDefine" else "",
                if (parameters.envMap) "#define $envMapBlendingDefine" else "",
                if (parameters.lightMap) "#define USE_LIGHTMAP" else "",
                if (parameters.aoMap) "#define USE_AOMAP" else "",
                if (parameters.emissiveMap) "#define USE_EMISSIVEMAP" else "",
                if (parameters.bumpMap) "#define USE_BUMPMAP" else "",
                if (parameters.normalMap) "#define USE_NORMALMAP" else "",
                if ((parameters.normalMap && parameters.objectSpaceNormalMap)) "#define OBJECTSPACE_NORMALMAP" else "",
                if (parameters.specularMap) "#define USE_SPECULARMAP" else "",
                if (parameters.roughnessMap) "#define USE_ROUGHNESSMAP" else "",
                if (parameters.metalnessMap) "#define USE_METALNESSMAP" else "",
                if (parameters.alphaMap) "#define USE_ALPHAMAP" else "",

                if (parameters.vertexTangents) "#define USE_TANGENT" else "",
                if (parameters.vertexColors > 0) "#define USE_COLOR" else "",

                if (parameters.gradientMap) "#define USE_GRADIENTMAP" else "",

                if (parameters.flatShading) "#define FLAT_SHADED" else "",

                if (parameters.doubleSided) "#define DOUBLE_SIDED" else "",
                if (parameters.flipSided) "#define FLIP_SIDED" else "",

                if (parameters.shadowMapEnabled) "#define USE_SHADOWMAP" else "",
                if (parameters.shadowMapEnabled) "#define $shadowMapTypeDefine" else "",

                if (parameters.premultipliedAlpha) "#define PREMULTIPLIED_ALPHA" else "",

                if (parameters.physicallyCorrectLights) "#define PHYSICALLY_CORRECT_LIGHTS" else "",

                if (parameters.logarithmicDepthBuffer) "#define USE_LOGDEPTHBUF" else "",
                if (parameters.logarithmicDepthBuffer) "#define USE_LOGDEPTHBUF_EXT" else "",

                if (parameters.envMap) "#define TEXTURE_LOD_EXT" else "",

                "uniform mat4 viewMatrix;",
                "uniform vec3 cameraPosition;",

                if ((parameters.toneMapping != NoToneMapping)) "#define TONE_MAPPING" else "",
                if ((parameters.toneMapping != NoToneMapping)) ShaderChunk["tonemapping_pars_fragment"]!! else "", // this code is required here because it is used by the toneMapping() function defined below
                if ((parameters.toneMapping != NoToneMapping)) getToneMappingFunction(
                    "toneMapping",
                    parameters.toneMapping
                ) else "",

                if (parameters.dithering) "#define DITHERING" else "",

                ShaderChunk["encodings_pars_fragment"]!!, // this code is required here because it is used by the various encoding/decoding function defined below
                getTexelDecodingFunction(
                    "mapTexelToLinear",
                    parameters.mapEncoding
                ),
                getTexelDecodingFunction(
                    "matcapTexelToLinear",
                    parameters.matcapEncoding
                ),
                getTexelDecodingFunction(
                    "envMapTexelToLinear",
                    parameters.envMapEncoding
                ),
                getTexelDecodingFunction(
                    "emissiveMapTexelToLinear",
                    parameters.emissiveMapEncoding
                ),
                getTexelEncodingFunction(
                    "linearToOutputTexel",
                    parameters.outputEncoding
                ),

                if (parameters.depthPacking != false) "#define DEPTH_PACKING " + parameters.depthPacking else "",

                "\n"

            ).filter { it.isNotEmpty() }.joinToString("\n")

        }

        vertexShader = parseIncludes(vertexShader);
        vertexShader = replaceLightNums(vertexShader, parameters);
        vertexShader = replaceClippingPlaneNums(vertexShader, parameters);

        fragmentShader = parseIncludes(fragmentShader);
        fragmentShader = replaceLightNums(fragmentShader, parameters);
        fragmentShader = replaceClippingPlaneNums(fragmentShader, parameters);

        if (material !is RawShaderMaterial) {

            var isGLSL3ShaderMaterial = false

            val versionRegex = "^\\s*#version\\s+300\\s+es\\s*\n".toRegex()

            if (material is ShaderMaterial &&
                versionRegex.containsMatchIn(vertexShader) &&
                versionRegex.containsMatchIn(fragmentShader)
            ) {

                isGLSL3ShaderMaterial = true

                vertexShader.replace(versionRegex, "")
                fragmentShader.replace(versionRegex, "")

            }

            prefixVertex = listOf(
                "#version 330 core\n",
                "#define attribute in",
                "#define varying out",
                "#define texture2D texture"
            ).joinToString("\n") + "\n" + prefixVertex

            prefixFragment = listOf(
                "#version 330 core\n",
                "#define varying in",
                if (isGLSL3ShaderMaterial) "" else "out highp vec4 pc_fragColor;",
                if (isGLSL3ShaderMaterial) "" else "#define gl_FragColor pc_fragColor",
                "#define gl_FragDepthEXT gl_FragDepth",
                "#define texture2D texture",
                "#define textureCube texture",
                "#define texture2DProj textureProj",
                "#define texture2DLodEXT textureLod",
                "#define texture2DProjLodEXT textureProjLod",
                "#define textureCubeLodEXT textureLod",
                "#define texture2DGradEXT textureGrad",
                "#define texture2DProjGradEXT textureProjGrad",
                "#define textureCubeGradEXT textureGrad"
            ).joinToString("\n") + "\n" + prefixFragment

            val vertexGlsl = prefixVertex + vertexShader;
            val fragmentGlsl = prefixFragment + fragmentShader;

            glVertexShader = createShader(GL20.GL_VERTEX_SHADER, vertexGlsl);
            glFragmentShader = createShader(GL20.GL_FRAGMENT_SHADER, fragmentGlsl);

            GL20.glAttachShader(program, glVertexShader)
            GL20.glAttachShader(program, glFragmentShader)

            GL20.glLinkProgram(program)

//            println(addLineNumbers(GL20.glGetShaderSource(glVertexShader)))
//            println()
//            println(addLineNumbers(GL20.glGetShaderSource(glFragmentShader)))

            if (renderer.checkShaderErrors) {

                val programLog = GL20.glGetProgramInfoLog( program ).trim();
//                val vertexLog = GL20.glGetShaderInfoLog( glVertexShader ).trim();
//                val fragmentLog = GL20.glGetShaderInfoLog( glFragmentShader ).trim();

                if ( GL20.glGetProgrami( program, GL20.GL_LINK_STATUS ) == GL11.GL_FALSE ) {

                    val vertexErrors = getShaderErrors(glVertexShader, "vertex" );
                    val fragmentErrors = getShaderErrors( glFragmentShader, "fragment" );

                    println( "GLProgram: shader error: ${GL11.glGetError()} ${GL20.GL_VALIDATE_STATUS} ${ GL20.glGetProgrami( program, GL20.GL_VALIDATE_STATUS )} glGetProgramInfoLog  $programLog $vertexErrors $fragmentErrors" );

                } else if ( programLog != "" ) {

                    println( "GLProgram: gl.getProgramInfoLog() $programLog" );

                }
            }

            GL20.glDeleteShader(glVertexShader);
            GL20.glDeleteShader(glFragmentShader);

        }

    }

    private lateinit var cachedUniforms: GLUniforms
    private lateinit var cachedAttributes: Map<String, Int>

    fun getUniforms(): GLUniforms {

        if (!::cachedUniforms.isInitialized) {
            cachedUniforms = GLUniforms(program)
        }

        return cachedUniforms

    }

    fun getAttributes(): Map<String, Int> {

        if (!::cachedAttributes.isInitialized) {
            cachedAttributes = fetchAttributeLocations(program)
        }

        return cachedAttributes

    }

    fun destroy() {
        GL20.glDeleteProgram(program)
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

        fun getShaderErrors(shader: Int, type: String): String {

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

        private fun generateDefines(defines: Map<String, Any>): String {

            return defines.mapNotNull { (key, value) ->
                "#define $key $value"
            }.joinToString { "\n" }

        }

        private fun fetchAttributeLocations(program: Int): Map<String, Int> {
            val attributes = mutableMapOf<String, Int>()

            val n = GL20.glGetProgrami(program, GL20.GL_ACTIVE_ATTRIBUTES)

            for (i in 0 until n) {
                val sizeBuffer = BufferUtils.createIntBuffer(1)
                val typeBuffer = BufferUtils.createIntBuffer(1)
                val name = GL20.glGetActiveAttrib(program, i, sizeBuffer, typeBuffer)
                attributes[name] = GL20.glGetAttribLocation(program, name)
            }

            return attributes
        }

        private fun replaceLightNums(string: String, parameters: GLPrograms.Parameters): String {
            return string
                .replace("NUM_DIR_LIGHTS".toRegex(), "${parameters.numDirLights}")
                .replace("NUM_SPOT_LIGHTS".toRegex(), "${parameters.numSpotLights}")
                .replace("NUM_RECT_AREA_LIGHTS".toRegex(), "${parameters.numRectAreaLights}")
                .replace("NUM_POINT_LIGHTS".toRegex(), "${parameters.numPointLights}")
                .replace("NUM_HEMI_LIGHTS".toRegex(), "${parameters.numHemiLights}");
        }

        private fun replaceClippingPlaneNums(string: String, parameters: GLPrograms.Parameters): String {

            return string
                .replace("NUM_CLIPPING_PLANES", "${parameters.numClippingPlanes}")
                .replace("UNION_CLIPPING_PLANES", "${(parameters.numClippingPlanes - parameters.numClipIntersection)}");

        }

        private fun parseIncludes(string: String): String {

            val regex = "^[ \\t]*#include +<([\\w\\d./]+)>".toRegex(RegexOption.MULTILINE)

            return regex.replace(string) { m ->

                val include = m.groups[1]!!.value
                parseIncludes(
                    ShaderChunk[include] ?: throw IllegalArgumentException(
                        "Can not resolve #include < $include >"
                    )
                )

            }

        }

    }

}
