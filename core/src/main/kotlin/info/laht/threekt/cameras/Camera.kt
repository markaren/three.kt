package info.laht.threekt.cameras

import info.laht.threekt.core.Object3D
import info.laht.threekt.core.Object3DImpl
import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Vector3

interface Camera : Object3D {

    val matrixWorldInverse: Matrix4

    val projectionMatrix: Matrix4
    val projectionMatrixInverse: Matrix4

    override fun getWorldDirection(target: Vector3): Vector3 {

        this.updateMatrixWorld(true)

        val e = this.matrixWorld.elements

        return target.set(-e[8], -e[9], -e[10]).normalize()

    }

    override fun updateMatrixWorld(force: Boolean) {

        super.updateMatrixWorld(force)

        this.matrixWorldInverse.getInverse(this.matrixWorld)

    }

    fun copy(source: Camera, recursive: Boolean): Camera {

        super.copy(source, recursive)

        this.matrixWorldInverse.copy(source.matrixWorldInverse)

        this.projectionMatrix.copy(source.projectionMatrix)
        this.projectionMatrixInverse.copy(source.projectionMatrixInverse)

        return this

    }

    override fun clone(): Camera

}

open class AbstractCamera : Camera, Object3DImpl() {

    override val matrixWorldInverse = Matrix4()

    override val projectionMatrix = Matrix4()
    override val projectionMatrixInverse = Matrix4()

    override fun clone(): AbstractCamera {
        return AbstractCamera().apply {
            copy(this, true)
        }
    }

}


interface CameraWithZoom : Camera {

    var zoom: Float

}

interface CameraWithNearAndFar : Camera {

    var near: Float
    var far: Float

}

interface CameraCanUpdateProjectionMatrix : Camera {

    fun updateProjectionMatrix()

}


fun Vector3.project(camera: Camera): Vector3 {
    return this.applyMatrix4(camera.matrixWorldInverse).applyMatrix4(camera.projectionMatrix)
}

fun Vector3.unproject(camera: Camera): Vector3 {
    return this.applyMatrix4(camera.projectionMatrixInverse).applyMatrix4(camera.matrixWorld)
}

