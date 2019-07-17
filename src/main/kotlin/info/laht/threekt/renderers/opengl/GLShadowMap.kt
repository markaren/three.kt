package info.laht.threekt.renderers.opengl

import info.laht.threekt.materials.Material
import info.laht.threekt.materials.MeshDepthMaterial
import info.laht.threekt.materials.MeshDistanceMaterial
import info.laht.threekt.math.Frustrum
import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Vector2i
import info.laht.threekt.math.Vector3
import info.laht.threekt.renderers.GLRenderer

class GLShadowMap internal constructor(
    private val renderer: GLRenderer,
    private val `objects`: GLObjects,
    private val maxTextureSize: Int
) {

    private val frustrum = Frustrum()
    private  val projScreenMatrix = Matrix4()

    private  val shadowMapSize = Vector2i()
    private val maxShadowMapSize = Vector2i( maxTextureSize, maxTextureSize )

    private  val lookTarget = Vector3()
    private  val lightPositionWorld = Vector3()

    private val MorphingFlag = 1
    private  val SkinningFlag = 2

    private val NumberOfMaterialVariants = (MorphingFlag or SkinningFlag) + 1

    private val depthMaterials = mutableListOf<MeshDepthMaterial>()
    private val distanceMaterials = mutableListOf<MeshDistanceMaterial>()

    private val materialCache = mutableMapOf<String, MutableMap<String, Material>>()

}
