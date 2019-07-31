package info.laht.threekt.objects

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.materials.Material
import info.laht.threekt.math.Matrix4

class SkinnedMesh(
    geometry: BufferGeometry,
    material: Material
) : Mesh(geometry, material) {

    var bindMode = "attached"
    val bindMatrix = Matrix4()
    val bindMatrixInverse = Matrix4()

    fun pose() {
        TODO()
    }

    fun normalizeSkinWeights() {
        TODO()
    }

    override fun updateMatrixWorld(force: Boolean ) {
        super.updateMatrixWorld(force)

        when {
            this.bindMode == "attached" -> this.bindMatrixInverse.getInverse( this.matrixWorld )
            this.bindMode == "detached" -> this.bindMatrixInverse.getInverse( this.bindMatrix )
            else -> println( "SkinnedMesh: Unrecognized bindMode: $bindMode" )
        }

    }

    override fun clone(): SkinnedMesh {
        return SkinnedMesh(geometry, material).copy(this) as SkinnedMesh
    }

}
