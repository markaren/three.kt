package info.laht.threekt.renderers.opengl

import info.laht.threekt.*
import info.laht.threekt.core.Object3D
import info.laht.threekt.materials.*
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.renderers.shaders.ShaderLib
import info.laht.threekt.scenes.FogExp2
import info.laht.threekt.scenes._Fog
import info.laht.threekt.textures.Texture

internal class GLPrograms(
    private val renderer: GLRenderer,
    private val capabilities: GLCapabilities
) {

    private val programs = mutableListOf<GLProgram>()

    fun getTextureEncodingFromMap(map: Texture?, gammaOverrideLinear: Boolean): Int {

        var encoding = map?.encoding ?: LinearEncoding

        if (encoding == LinearEncoding && gammaOverrideLinear) {
            encoding = GammaEncoding
        }

        return encoding

    }

    internal fun getParameters(
        material: Material,
        lights: GLLights.GLLightsState,
        shadows: List<Object3D>,
        fog: _Fog?,
        nClipPlanes: Int,
        nClipIntersection: Int,
        `object`: Object3D
    ): Parameters {
        return Parameters(
            material,
            lights,
            shadows,
            fog,
            nClipPlanes,
            nClipIntersection,
            `object`
        )
    }

    internal fun getProgramCode(material: Material, parameters: Parameters): String {

        val array = mutableListOf<String>()

        if (parameters.shaderID != null) {

            array.add(parameters.shaderID)

        } else {

            array.add(material.fragmentShader)
            array.add(material.vertexShader)

        }

        for ((name, value) in material.defines) {

            array.add(name)
            array.add(value.toString())

        }

        parameterNames.forEach { name ->
            array.add(parameters[name] ?: "null")
        }

        array.add(renderer.gammaOutput.toString())
        array.add(renderer.gammaFactor.toString())

        return array.joinToString(",")

    }

    internal fun acquireProgram(
        material: Material,
        shader: ShaderLib.Shader,
        parameters: Parameters,
        code: String
    ): GLProgram {

        var program: GLProgram? = null

        // Check if code has been already compiled
        for (i in 0 until programs.size) {

            val programInfo = programs[i]

            if (programInfo.code === code) {

                program = programInfo
                ++program.usedTimes

                break

            }

        }

        if (program == null) {

            program = GLProgram(renderer, code, material, shader, parameters)
            programs.add(program)

        }

        return program

    }

    fun releaseProgram(program: GLProgram) {

        if (--program.usedTimes == 0) {

            // Remove from unordered set
            val i = programs.indexOf(program)
            programs[i] = programs[programs.size - 1]
            programs.removeAt(programs.size - 1) //pop

            // Free GL resources
            program.destroy()

        }

    }

    object ShaderIds {

        private val map = mapOf(
            "MeshDepthMaterial" to "depth",
            "MeshDistanceMaterial" to "distanceRGBA",
            "MeshNormalMaterial" to "normal",
            "MeshBasicMaterial" to "basic",
            "MeshLambertMaterial" to "lambert",
            "MeshPhongMaterial" to "phong",
            "MeshToonMaterial" to "phong",
            "MeshStandardMaterial" to "physical",
            "MeshPhysicalMaterial" to "physical",
            "MeshMatcapMaterial" to "matcap",
            "LineBasicMaterial" to "basic",
            "LineDashedMaterial" to "dashed",
            "PointsMaterial" to "points",
            "ShadowMaterial" to "shadow",
            "SpriteMaterial" to "sprite"
        )

        operator fun get(key: String): String? {
            return map[key]
        }

    }

    internal inner class Parameters(
        material: Material,
        lights: GLLights.GLLightsState,
        shadows: List<Object3D>,
        fog: _Fog?,
        nClipPlanes: Int,
        nClipIntersection: Int,
        `object`: Object3D
    ) {

        val shaderID = ShaderIds[material::class.java.simpleName]

        val maxBones = 0 // TODO
        val precision = "highp"

        val supportsVertexTextures = capabilities.vertexTextures
        val outputEncoding = getTextureEncodingFromMap(renderer.getRenderTarget()?.texture, renderer.gammaOutput)

        val map = material.map != null
        val mapEncoding = getTextureEncodingFromMap(material.map, renderer.gammaInput)
        val matcap = material.matcap != null
        val matcapEncoding = getTextureEncodingFromMap(material.matcap, renderer.gammaInput)
        val envMap = material.envMap != null
        val envMapMode = envMap && material.envMap?.mapping != null
        val envMapEncoding = getTextureEncodingFromMap(material.envMap, renderer.gammaInput)
        val envMapCubeUV =
            (envMap) && ((material.envMap?.mapping == CubeUVReflectionMapping) || (material.envMap?.mapping == CubeUVRefractionMapping))
        val lightMap = material.lightMap != null
        val aoMap = material.aoMap != null
        val emissiveMap = material.emissiveMap != null
        val emissiveMapEncoding = getTextureEncodingFromMap(material.emissiveMap, renderer.gammaInput)
        val bumpMap = material.bumpMap != null
        val normalMap = material.normalMap != null
        val objectSpaceNormalMap = material.normalMapType == ObjectSpaceNormalMap
        val displacementMap = material.displacementMap != null
        val roughnessMap = material.roughnessMap != null
        val metalnessMap = material.metalnessMap != null
        val specularMap = material.specularMap != null
        val alphaMap = material.alphaMap != null
        val gradientMap = material.gradientMap != null

        val combine = material.combine

        val vertexTangents = (material.normalMap != null && material.vertexTangents)
        val vertexColors = material.vertexColors

        val fog: Boolean = fog != null
        val useFog = material.fog
        val fogExp = (this.fog && (fog is FogExp2))

        val flatShading = material.flatShading

        val sizeAttenuation = if (material is MaterialWithSizeAttenuation) material.sizeAttenuation else false
        val logarithmicDepthBuffer = false

        val skinning = (material is MaterialWithSkinning && material.skinning && maxBones > 0)
        val useVertexTexture = capabilities.floatVertexTextures

        val morphTargets = if (material is MaterialWithMorphTarget) material.morphTargets else false
        val morphNormals = if (material is MaterialWithMorphNormals) material.morphNormals else false
        val maxMorphTargets = renderer.maxMorphTargets
        val maxMorphNormals = renderer.maxMorphNormals
        //
        val numDirLights = lights.directional.size
        val numPointLights = lights.point.size
        val numSpotLights = lights.spot.size
        val numRectAreaLights = lights.rectArea.size
        val numHemiLights = lights.hemi.size

        val numClippingPlanes = nClipPlanes
        val numClipIntersection = nClipIntersection

        val dithering = material.dithering

        val shadowMapEnabled = renderer.shadowMap.enabled && `object`.receiveShadow && shadows.isNotEmpty()
        val shadowMapType = renderer.shadowMap.type

        val toneMapping = renderer.toneMapping
        val physicallyCorrectLights = renderer.physicallyCorrectLights

        val premultipliedAlpha = material.premultipliedAlpha

        val alphaTest = material.alphaTest
        val doubleSided = material.side == DoubleSide
        val flipSided = material.side == BackSide

        val depthPacking = if (material is MeshDepthMaterial) material.depthPacking else false

        operator fun get(name: String): String? {
            return Parameters::class.java.getDeclaredField(name)?.get(this).toString()
        }

    }

}

private val parameterNames = listOf(
    "precision",
    "supportsVertexTextures",
    "map",
    "mapEncoding",
    "matcap",
    "matcapEncoding",
    "envMap",
    "envMapMode",
    "envMapEncoding",
    "lightMap",
    "aoMap",
    "emissiveMap",
    "emissiveMapEncoding",
    "bumpMap",
    "normalMap",
    "objectSpaceNormalMap",
    "displacementMap",
    "specularMap",
    "roughnessMap",
    "metalnessMap",
    "gradientMap",
    "alphaMap",
    "combine",
    "vertexColors",
    "vertexTangents",
    "fog",
    "useFog",
    "fogExp",
    "flatShading",
    "sizeAttenuation",
    "logarithmicDepthBuffer",
    "skinning",
    "maxBones",
    "useVertexTexture",
    "morphTargets",
    "morphNormals",
    "maxMorphTargets",
    "maxMorphNormals",
    "premultipliedAlpha",
    "numDirLights",
    "numPointLights",
    "numSpotLights",
    "numHemiLights",
    "numRectAreaLights",
    "shadowMapEnabled",
    "shadowMapType",
    "toneMapping",
    "physicallyCorrectLights",
    "alphaTest",
    "doubleSided",
    "flipSided",
    "numClippingPlanes",
    "numClipIntersection",
    "depthPacking",
    "dithering"
)
