package info.laht.threekt.cameras

import info.laht.threekt.core.Object3D
import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Vector3

abstract class Camera: Object3D() {

    val matrixWorldInverse = Matrix4()

    val projectionMatrix = Matrix4()
    val projectionMatrixInverse = Matrix4()

    fun copy( source: Camera, recursive: Boolean): Camera {

        super.copy(source, recursive)

        this.matrixWorldInverse.copy( source.matrixWorldInverse )

        this.projectionMatrix.copy( source.projectionMatrix )
        this.projectionMatrixInverse.copy( source.projectionMatrixInverse )

        return this

    }

    abstract override fun clone(): Camera

    override fun getWorldDirection( target: Vector3 ): Vector3 {

        this.updateMatrixWorld( true )

        val e = this.matrixWorld.elements

        return target.set( - e[ 8 ], - e[ 9 ], - e[ 10 ] ).normalize()

    }

    override fun updateMatrixWorld ( force: Boolean ) {

        super.updateMatrixWorld( force )

        this.matrixWorldInverse.getInverse( this.matrixWorld );

    }

}
