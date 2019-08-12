package info.laht.threekt.renderers.opengl

import info.laht.threekt.*
import info.laht.threekt.cameras.Camera
import info.laht.threekt.cameras.CameraWithNearAndFar
import info.laht.threekt.core.MaterialObject
import info.laht.threekt.core.MaterialsObject
import info.laht.threekt.core.Object3D
import info.laht.threekt.lights.Light
import info.laht.threekt.lights.LightShadow
import info.laht.threekt.lights.LightWithShadow
import info.laht.threekt.lights.PointLight
import info.laht.threekt.materials.*
import info.laht.threekt.math.Frustum
import info.laht.threekt.math.Vector2
import info.laht.threekt.math.Vector3
import info.laht.threekt.math.Vector4
import info.laht.threekt.objects.Line
import info.laht.threekt.objects.Mesh
import info.laht.threekt.objects.Points
import info.laht.threekt.renderers.GLRenderTarget
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import kotlin.math.floor
import kotlin.math.roundToInt

private const val morphingFlag = 1
private const val skinningFlag = 2

class GLShadowMap internal constructor(
        private val renderer: GLRenderer,
        private val maxTextureSize: Int
) {

    private val frustum = Frustum()


    private val shadowMapSize = Vector2()
    private val viewportSize = Vector2()

    private val viewport = Vector4()

    private val depthMaterials = mutableListOf<MeshDepthMaterial>()
    private val distanceMaterials = mutableListOf<MeshDistanceMaterial>()

    private val materialCache = mutableMapOf<String, MutableMap<String, Material>>()

    private val shadowSide = mapOf(0 to Side.Back, 1 to Side.Front, 2 to Side.Double)

    private var cubeDirections = listOf(
        Vector3(1f, 0f, 0f), Vector3(-1f, 0f, 0f), Vector3(0f, 0f, 1f),
        Vector3(0f, 0f, -1f), Vector3(0f, 1f, 0f), Vector3(0f, -1f, 0f)
    )

    private var cubeUps = listOf(
        Vector3(0f, 1f, 0f), Vector3(0f, 1f, 0f), Vector3(0f, 1f, 0f),
        Vector3(0f, 1f, 0f), Vector3(0f, 0f, 1f), Vector3(0f, 0f, -1f)
    )

    private var cube2DViewPorts = listOf(
        Vector4(), Vector4(), Vector4(),
        Vector4(), Vector4(), Vector4()
    )

    var enabled = false

    var autoUpdate = true
    var needsUpdate = false

    var type = ShadowType.PCF

    init {

        val numberOfMaterialVariants = (morphingFlag or skinningFlag) + 1

        for (i in 0 until numberOfMaterialVariants) {
            val useMorphing = (i and morphingFlag) != 0
            val useSkinning = (i and skinningFlag) != 0

            val depthMaterial = MeshDepthMaterial().apply {

                depthPacking = TextureEncoding.RGBADepthPacking

                morphTargets = useMorphing
                skinning = useSkinning

            }

            depthMaterials.add(depthMaterial)

            val distanceMaterial = MeshDistanceMaterial().apply {

                morphTargets = useMorphing
                skinning = useSkinning

            }

            distanceMaterials.add(distanceMaterial)
        }

    }

    fun render(lights: List<Light>, scene: Scene, camera: Camera) {

        if (!enabled) return
        if (!(autoUpdate || needsUpdate)) return

        if (lights.isEmpty()) return

        val currentRenderTarget = renderer.getRenderTarget()
        val activeCubeFace = renderer.getActiveCubeFace()
        val activeMipmapLevel = renderer.getActiveMipmapLevel()

        val state = renderer.state

        // Set GL state for depth map.
        state.setBlending(Blending.None)
        state.colorBuffer.setClear(1f, 1f, 1f, 1f)
        state.depthBuffer.setTest(true)
        state.setScissorTest(false)

        // render depth map

        lights.forEach { light ->

            if (light is LightWithShadow) {

                val shadow = light.shadow as LightShadow<CameraWithNearAndFar>

                shadowMapSize.copy(shadow.mapSize)

                val shadowFrameExtents = shadow.frameExtents
                shadowMapSize.multiply(shadowFrameExtents)

                viewportSize.copy(shadow.mapSize)

                if (shadowMapSize.x > maxTextureSize || shadowMapSize.y > maxTextureSize) {

                    println("GLShadowMap:' $light, 'has shadow exceeding max texture size, reducing")

                    if (shadowMapSize.y > maxTextureSize) {

                        viewportSize.y = floor(maxTextureSize / shadowFrameExtents.y)
                        shadowMapSize.y = viewportSize.y * shadowFrameExtents.y
                        shadow.mapSize.y = viewportSize.y

                    }

                }

                if (shadow.map == null) {

                    shadow.map = GLRenderTarget(
                            shadowMapSize.x.roundToInt(), shadowMapSize.y.roundToInt(), GLRenderTarget.Options(
                            minFilter = TextureFilter.Nearest,
                            magFilter = TextureFilter.Nearest,
                            format = TextureFormat.RGBA
                    )
                    ).apply {
                        texture.name = light.name + ".shadowMap"
                    }

                }

                renderer.setRenderTarget(shadow.map as GLRenderTarget)
                renderer.clear()

                shadow.viewports.forEachIndexed { vp, viewport ->

                    viewport.set(
                            viewportSize.x * viewport.x,
                            viewportSize.y * viewport.y,
                            viewportSize.x * viewport.z,
                            viewportSize.y * viewport.w
                    )

                    state.viewport(viewport)

                    shadow.updateMatrices(light, camera as CameraWithNearAndFar, vp)

                    frustum.copy(shadow.frustum)

                    renderObject(scene, camera, shadow.camera, light)

                }
            }
        }

        needsUpdate = false

        renderer.setRenderTarget(currentRenderTarget, activeCubeFace, activeMipmapLevel)

    }

    private fun getDepthMaterial(
            `object`: Object3D,
            material: Material,
            light: Light,
            shadowCameraNear: Float,
            shadowCameraFar: Float
    ): Material {

        var materialVariants: MutableList<out Material> = depthMaterials
        var customMaterial: Material? = `object`.customDepthMaterial

        if (light is PointLight) {
            materialVariants = distanceMaterials
            customMaterial = `object`.customDistanceMaterial
        }

        var result = customMaterial ?: run {

            val useMorphing = false
//            if (material is MorphTargetMaterial && material.morphTargets) {
//                useMorphing = geometry.morphAttributes && geometry.morphAttributes.position && geometry.morphAttributes.position.length > 0
//            }
//
//            if (`object` is SkinnedMesh && !(material is SkinningMaterial && material.skinning)) {
//                println("GLShadowMap: SkinnedMesh with material.skinning set to false:  $`object`")
//            }

            val useSkinning = false // `object` is SkinnedMesh && (material is SkinningMaterial && material.skinning)
//
            var variantIndex = 0

            if (useMorphing) {
                variantIndex = variantIndex or morphingFlag
            }
            if (useSkinning) {
                variantIndex = variantIndex or skinningFlag
            }

            materialVariants[variantIndex]

        }

        if (renderer.localClippingEnabled &&
            material.clipShadows &&
            material.clippingPlanes?.isNotEmpty() == true
        ) {

            // in this case we need a unique material instance reflecting the
            // appropriate state

            val keyA = result.uuid
            val keyB = material.uuid

            val materialsForVariant = materialCache[keyA] ?: mutableMapOf<String, Material>().also {
                materialCache[keyA] = it
            }

            val cachedMaterial = materialsForVariant[keyB] ?: result.clone().also {
                materialsForVariant[keyB] = it
            }

            result = cachedMaterial

        }

        result.visible = material.visible
        if (result is MaterialWithWireframe && material is MaterialWithWireframe) {
            result.wireframe = material.wireframe
            result.wireframeLinewidth = material.wireframeLinewidth
        }

        result.side = material.shadowSide ?: shadowSide.getValue(material.side.value)

        result.clipShadows = material.clipShadows
        result.clippingPlanes = material.clippingPlanes
        result.clipIntersection = material.clipIntersection

        if (result is MaterialWithLineWidth && material is MaterialWithLineWidth) {
            result.linewidth = material.linewidth
        }

        if (light is PointLight && result is MeshDistanceMaterial) {

            result.referencePosition.setFromMatrixPosition(light.matrixWorld)
            result.nearDistance = shadowCameraNear
            result.farDistance = shadowCameraFar

        }

        return result
    }

    private fun renderObject(
            `object`: Object3D,
            camera: Camera,
            shadowCamera: CameraWithNearAndFar,
            light: Light
    ) {
        if (!`object`.visible) return

        val visible = `object`.layers.test(camera.layers)

        if (visible && (`object` is Mesh || `object` is Line || `object` is Points)) {

            `object` as MaterialObject

            if (`object`.castShadow && (!`object`.frustumCulled || frustum.intersectsObject(`object`))) {

                `object`.modelViewMatrix.multiplyMatrices(shadowCamera.matrixWorldInverse, `object`.matrixWorld)

                val geometry = renderer.objects.update(`object`)


                if (`object` is MaterialsObject && `object`.isMultiMaterial) {

                    val groups = geometry.groups

                    for (k in 0 until groups.size) {

                        val group = groups[k]
                        val groupMaterial = `object`.materials.getOrNull(group.materialIndex)

                        if (groupMaterial != null && groupMaterial.visible) {

                            val depthMaterial = getDepthMaterial(
                                `object`,
                                groupMaterial,
                                    light,
                                shadowCamera.near,
                                shadowCamera.far
                            )
                            renderer.renderBufferDirect(shadowCamera, null, geometry, depthMaterial, `object`, group)

                        }

                    }

                } else if (`object`.material.visible) {

                    val depthMaterial = getDepthMaterial(
                        `object`,
                        `object`.material,
                            light,
                        shadowCamera.near,
                        shadowCamera.far
                    )
                    renderer.renderBufferDirect(shadowCamera, null, geometry, depthMaterial, `object`, null)

                }

            }

        }

        `object`.children.forEach { child ->

            renderObject(child, camera, shadowCamera, light)

        }


    }

}
