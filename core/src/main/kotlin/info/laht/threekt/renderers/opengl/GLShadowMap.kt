package info.laht.threekt.renderers.opengl

import info.laht.threekt.*
import info.laht.threekt.cameras.Camera
import info.laht.threekt.cameras.CameraWithNearAndFar
import info.laht.threekt.cameras.OrthographicCamera
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.GeometryObject
import info.laht.threekt.core.MaterialObject
import info.laht.threekt.core.MaterialsObject
import info.laht.threekt.core.Object3D
import info.laht.threekt.lights.*
import info.laht.threekt.materials.*
import info.laht.threekt.math.*
import info.laht.threekt.objects.Line
import info.laht.threekt.objects.Mesh
import info.laht.threekt.objects.Points
import info.laht.threekt.renderers.GLRenderTarget
import info.laht.threekt.renderers.GLRenderer
import info.laht.threekt.scenes.Scene
import kotlin.math.roundToInt

class GLShadowMap internal constructor(
    private val renderer: GLRenderer,
    private val objects: GLObjects,
    maxTextureSize: Int
) {

    private val frustum = Frustum()
    private val projScreenMatrix = Matrix4()

    private val shadowMapSize = Vector2()
    private val maxShadowMapSize = Vector2(maxTextureSize, maxTextureSize)

    private val lookTarget = Vector3()
    private val lightPositionWorld = Vector3()

    private val depthMaterials = mutableListOf<MeshDepthMaterial>()
    private val distanceMaterials = mutableListOf<MeshDistanceMaterial>()

    private val materialCache = mutableMapOf<String, MutableMap<String, Material>>()

    private val shadowSide = mapOf(0 to BackSide, 1 to FrontSide, 2 to DoubleSide)

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

    private val MorphingFlag = 1
    private val SkinningFlag = 2

    var enabled = false

    var autoUpdate = true
    var needsUpdate = false

    var type = PCFShadowMap

    init {

        val NumberOfMaterialVariants = (MorphingFlag or SkinningFlag) + 1

        for (i in 0 until NumberOfMaterialVariants) {
            val useMorphing = (i and MorphingFlag) != 0
            val useSkinning = (i and SkinningFlag) != 0

            val depthMaterial = MeshDepthMaterial().apply {

                depthPacking = RGBADepthPacking

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

    fun render(lights: List<Object3D>, scene: Scene, camera: Camera) {

        if (!enabled) return
        if (!(autoUpdate || needsUpdate)) return

        if (lights.isEmpty()) return

        val currentRenderTarget = renderer.getRenderTarget()
        val activeCubeFace = renderer.getActiveCubeFace()
        val activeMipmapLevel = renderer.getActiveMipmapLevel()

        val state = renderer.state

        // Set GL state for depth map.
        state.setBlending(NoBlending)
        state.colorBuffer.setClear(1f, 1f, 1f, 1f)
        state.depthBuffer.setTest(true)
        state.setScissorTest(false)

        // render depth map

        var faceCount: Int

        lights.forEach { light ->

            if (light is LightWithShadow) {
                val shadow = light.shadow
                val isPointLight = light is PointLight

                val shadowCamera = shadow.camera

                shadowMapSize.copy(shadow.mapSize)
                shadowMapSize.min(maxShadowMapSize)

                if (isPointLight) {

                    val vpWidth = shadowMapSize.x
                    val vpHeight = shadowMapSize.y

                    // These viewports map a cube-map onto a 2D renderTargetCube with the
                    // following orientation:
                    //
                    //  xzXZ
                    //   y Y
                    //
                    // X - Positive x direction
                    // x - Negative x direction
                    // Y - Positive y direction
                    // y - Negative y direction
                    // Z - Positive z direction
                    // z - Negative z direction

                    // positive X
                    cube2DViewPorts[0].set(vpWidth * 2, vpHeight, vpWidth, vpHeight)
                    // negative X
                    cube2DViewPorts[1].set(0f, vpHeight, vpWidth, vpHeight)
                    // positive Z
                    cube2DViewPorts[2].set(vpWidth * 3, vpHeight, vpWidth, vpHeight)
                    // negative Z
                    cube2DViewPorts[3].set(vpWidth, vpHeight, vpWidth, vpHeight)
                    // positive Y
                    cube2DViewPorts[4].set(vpWidth * 3, 0f, vpWidth, vpHeight)
                    // negative Y
                    cube2DViewPorts[5].set(vpWidth, 0f, vpWidth, vpHeight)

                    shadowMapSize.x *= 4
                    shadowMapSize.y *= 2

                }

                if (shadow.map == null) {

                    shadow.map = GLRenderTarget(
                        shadowMapSize.x.roundToInt(), shadowMapSize.y.roundToInt(), GLRenderTarget.Options(
                            minFilter = NearestFilter,
                            magFilter = NearestFilter,
                            format = RGBAFormat
                        )
                    )
                    shadow.map?.texture?.name = light.name + ".shadowMap"

                    when (shadowCamera) {
                        is PerspectiveCamera -> shadowCamera.updateProjectionMatrix()
                        is OrthographicCamera -> shadowCamera.updateProjectionMatrix()
                        else -> throw IllegalStateException()
                    }

                }

                if (shadow is SpotLightShadow) {

                    shadow.update(light as SpotLight);

                }

                val shadowMap = shadow.map
                val shadowMatrix = shadow.matrix

                lightPositionWorld.setFromMatrixPosition(light.matrixWorld)
                shadowCamera.position.copy(lightPositionWorld)

                if (isPointLight) {

                    faceCount = 6

                    // for point lights we set the shadow matrix to be a translation-only matrix
                    // equal to inverse of the light's position

                    shadowMatrix.makeTranslation(-lightPositionWorld.x, -lightPositionWorld.y, -lightPositionWorld.z)

                } else {

                    light as LightWithTarget

                    faceCount = 1

                    lookTarget.setFromMatrixPosition(light.target.matrixWorld)
                    shadowCamera.lookAt(lookTarget)
                    shadowCamera.updateMatrixWorld()

                    // compute shadow matrix

                    shadowMatrix.set(
                        0.5f, 0f, 0f, 0.5f,
                        0f, 0.5f, 0f, 0.5f,
                        0f, 0f, 0.5f, 0.5f,
                        0f, 0f, 0f, 1f
                    )

                    shadowMatrix.multiply(shadowCamera.projectionMatrix)
                    shadowMatrix.multiply(shadowCamera.matrixWorldInverse)

                }

                renderer.setRenderTarget(shadowMap)
                renderer.clear()

                // render shadow map for each cube face (if omni-directional) or
                // run a single pass if not

                for (face in 0 until faceCount) {

                    if (isPointLight) {

                        lookTarget.copy(shadowCamera.position)
                        lookTarget.add(cubeDirections[face])
                        shadowCamera.up.copy(cubeUps[face])
                        shadowCamera.lookAt(lookTarget)
                        shadowCamera.updateMatrixWorld()

                        val vpDimensions = cube2DViewPorts[face]
                        state.viewport(vpDimensions)

                    }

                    // update camera matrices and frustum

                    projScreenMatrix.multiplyMatrices(shadowCamera.projectionMatrix, shadowCamera.matrixWorldInverse)
                    frustum.setFromMatrix(projScreenMatrix)

                    // set object matrices & frustum culling

                    renderObject(scene, camera, shadowCamera, isPointLight)

                }
            }
        }

        needsUpdate = false

        renderer.setRenderTarget(currentRenderTarget, activeCubeFace, activeMipmapLevel)

    }

    private fun getDepthMaterial(
        `object`: Object3D,
        material: Material,
        isPointLight: Boolean,
        lightPositionWorld: Vector3,
        shadowCameraNear: Float,
        shadowCameraFar: Float
    ): Material {

        var materialVariants: MutableList<out Material> = depthMaterials
        var customMaterial: Material? = `object`.customDepthMaterial

        if (isPointLight) {
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
                variantIndex = variantIndex or MorphingFlag
            }
            if (useSkinning) {
                variantIndex = variantIndex or SkinningFlag
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

        result.side = material.shadowSide ?: shadowSide.getValue(material.side)

        result.clipShadows = material.clipShadows
        result.clippingPlanes = material.clippingPlanes
        result.clipIntersection = material.clipIntersection

        if (result is MaterialWithLineWidth && material is MaterialWithLineWidth) {
            result.linewidth = material.linewidth
        }

        if (isPointLight && result is MeshDistanceMaterial) {

            result.referencePosition.copy(lightPositionWorld)
            result.nearDistance = shadowCameraNear
            result.farDistance = shadowCameraFar

        }

        return result
    }

    private fun renderObject(
        `object`: Object3D,
        camera: Camera,
        shadowCamera: CameraWithNearAndFar,
        isPointLight: Boolean
    ) {
        if (!`object`.visible) return

        val visible = `object`.layers.test(camera.layers)

        if (visible && (`object` is Mesh || `object` is Line || `object` is Points)) {

            `object` as MaterialObject

            if (`object`.castShadow && (!`object`.frustumCulled || frustum.intersectsObject(`object`))) {

                `object`.modelViewMatrix.multiplyMatrices(shadowCamera.matrixWorldInverse, `object`.matrixWorld)

                val geometry = objects.update(`object`)


                if (`object` is MaterialsObject && `object`.isMultiMaterial) {

                    val groups = geometry.groups

                    for (k in 0 until groups.size) {

                        val group = groups[k]
                        val groupMaterial = `object`.materials.getOrNull(group.materialIndex)

                        if (groupMaterial != null && groupMaterial.visible) {

                            val depthMaterial = getDepthMaterial(
                                `object`,
                                groupMaterial,
                                isPointLight,
                                lightPositionWorld,
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
                        isPointLight,
                        lightPositionWorld,
                        shadowCamera.near,
                        shadowCamera.far
                    )
                    renderer.renderBufferDirect(shadowCamera, null, geometry, depthMaterial, `object`, null)

                }

            }

        }

        `object`.children.forEach { child ->

            renderObject(child, camera, shadowCamera, isPointLight)

        }


    }

}
