package info.laht.threekt.renderers.opengl

import info.laht.threekt.*
import info.laht.threekt.core.Object3D
import info.laht.threekt.lights.Light
import info.laht.threekt.lights.LightShadow
import info.laht.threekt.materials.Material
import info.laht.threekt.materials.MeshDepthMaterial
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Fog
import info.laht.threekt.scenes.FogExp2
import info.laht.threekt.scenes._Fog
import info.laht.threekt.textures.Texture

class GLPrograms internal constructor(
    private val renderer: GLRenderer,
    private val capabilities: GLCapabilities
) {

    val programs = mutableListOf<Int>()

    val shaderIds = ShaderIds()

    val parameterNames = listOf(
        "precision",
        "supportsVertexTextures",
        "additionalDetails",
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

    fun allocateBones(`object`: Object3D) {
        TODO()
    }

    fun getTextureEncodingFromMap( map: Texture?, gammaOverrideLinear: Boolean): Int {

        var encoding = if (map == null) {
            LinearEncoding
        } else {
            map.encoding
        }

        if (encoding == LinearEncoding && gammaOverrideLinear) {
            encoding = GammaEncoding
        }

        return encoding

    }

    inner class ShaderIds {

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

        operator fun get(key: String): String {
            return map[key] ?: throw IllegalArgumentException("No such key: '$key' in ${map.keys} ")
        }

        val MeshDepthMaterial: String = "depth"
        val MeshDistanceMaterial: String = "distanceRGBA"
        val MeshNormalMaterial: String = "normal"
        val MeshBasicMaterial: String = "basic"
        val MeshLambertMaterial: String = "lambert"
        val MeshPhongMaterial: String = "phong"
        val MeshToonMaterial: String = "phong"
        val MeshStandardMaterial: String = "physical"
        val MeshPhysicalMaterial: String = "physical"
        val MeshMatcapMaterial: String = "matcap"
        val LineBasicMaterial: String = "basic"
        val LineDashedMaterial: String = "dashed"
        val PointsMaterial: String = "points"
        val ShadowMaterial: String = "shadow"
        val SpriteMaterial: String = "sprite"

    }

    inner class Parameters(
        material: Material,
        lights: List<Light>,
        shadows: List<LightShadow>,
        fog: _Fog,
        nClipPlanes: Int,
        nClipIntersection: Int,
        `object`: Object3D
    ) {

        val shaderId = shaderIds[material::class.java.simpleName]

        val maxBones = 0 // TODO
        var precision = capabilities.precision
            private set

        val supportsVertexTextures = capabilities.vertexTextures
        val outputEncoding = getTextureEncodingFromMap( renderer.getRenderTarget()?.texture, renderer.gammaOutput )

//        val map = material.map != null
//        val mapEncoding = getTextureEncodingFromMap( material.map, renderer.gammaInput )
//        val matcap = !! material.matcap
//        val matcapEncoding = getTextureEncodingFromMap( material.matcap, renderer.gammaInput )
//        val envMap = !! material.envMap
//        val envMapMode = material.envMap && material.envMap.mapping
//        val envMapEncoding = getTextureEncodingFromMap( material.envMap, renderer.gammaInput )
//        val envMapCubeUV = ( !! material.envMap ) && ( ( material.envMap.mapping == CubeUVReflectionMapping ) || ( material.envMap.mapping == CubeUVRefractionMapping ) )
//        val lightMap = !! material.lightMap
//        val aoMap = !! material.aoMap
//        val emissiveMap = !! material.emissiveMap
//        val emissiveMapEncoding = getTextureEncodingFromMap( material.emissiveMap, renderer.gammaInput )
//        val bumpMap = !! material.bumpMap
//        val normalMap = !! material.normalMap
//        val objectSpaceNormalMap = material.normalMapType == ObjectSpaceNormalMap
//        val displacementMap = !! material.displacementMap
//        val roughnessMap = !! material.roughnessMap
//        val metalnessMap = !! material.metalnessMap
//        val specularMap = !! material.specularMap
//        val alphaMap = !! material.alphaMap
//
//        val gradientMap = material.gradientMap != null
//
//        val combine = material.combine
//
//        val vertexTangents = ( material.normalMap && material.vertexTangents )
        val  vertexColors = material.vertexColors

        val fog: Boolean = fog != null
        val useFog = material.fog
        val fogExp = ( this.fog && ( fog is FogExp2 ) )

        val flatShading = material.flatShading

//        val sizeAttenuation = material.sizeAttenuation
//        val logarithmicDepthBuffer = capabilities.logarithmicDepthBuffer
//
//        val skinning = material.skinning && maxBones > 0
        val useVertexTexture = capabilities.floatVertexTextures

//        val morphTargets = material.morphTargets
//        val  morphNormals = material.morphNormals
        val maxMorphTargets = renderer.maxMorphTargets
        val maxMorphNormals = renderer.maxMorphNormals
//
//        val numDirLights = lights.directional.size
//        val numPointLights = lights.point.size
//        val numSpotLights = lights.spot.size
//        val numRectAreaLights = lights.rectArea.size
//        val numHemiLights = lights.hemi.size

        val numClippingPlanes = nClipPlanes
        val numClipIntersection = nClipIntersection

        val dithering = material.dithering

//        val shadowMapEnabled = renderer.shadowMap.enabled && `object`.receiveShadow && shadows.size > 0
//        val shadowMapType = renderer.shadowMap.type

        val toneMapping = renderer.toneMapping
        val physicallyCorrectLights = renderer.physicallyCorrectLights

        val premultipliedAlpha = material.premultipliedAlpha

        val alphaTest = material.alphaTest
        val doubleSided = material.side == DoubleSide
        val flipSided = material.side == BackSide

        val depthPacking =  if ( material is MeshDepthMaterial ) material.depthPacking else false

        init {

            material.precision?.also {
                precision = capabilities.getMaxPrecision(it)

                if (precision != it) {
                    println("GLProgram.Parameters: $it not supported, using '$precision' instead.")
                }

            }


        }

    }

}
