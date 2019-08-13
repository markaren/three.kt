package info.laht.threekt.lights

import info.laht.threekt.cameras.Camera
import info.laht.threekt.cameras.CameraWithNearAndFar
import info.laht.threekt.cameras.OrthographicCamera
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.Cloneable
import info.laht.threekt.math.*
import info.laht.threekt.renderers.RenderTarget

open class LightShadow<E : CameraWithNearAndFar>(
        val camera: E
) : Cloneable {

    var bias = 0f
    var radius = 1f

    var mapSize = Vector2(512, 512)

    var map: RenderTarget? = null
    var matrix = Matrix4()

    val frustum = Frustum()
    var viewportCount = 1
        protected set

    internal val viewports = mutableListOf(
            Vector4(0, 0, 1, 1)
    )
    internal val frameExtents = Vector2(1, 1)
    internal val projScreenMatrix = Matrix4()
    internal val lightPositionWorld = Vector3()
    internal val lookTarget = Vector3()

    open fun updateMatrices(light: Light, viewCamera: Camera, viewportIndex: Int) {
        val shadowCamera = this.camera
        val shadowMatrix = this.matrix

        projScreenMatrix.multiplyMatrices(shadowCamera.projectionMatrix, shadowCamera.matrixWorldInverse)
        this.frustum.setFromMatrix(projScreenMatrix)

        shadowMatrix.set(
                0.5f, 0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.0f, 0.5f,
                0.0f, 0.0f, 0.5f, 0.5f,
                0.0f, 0.0f, 0.0f, 1.0f
        )

        shadowMatrix.multiply(shadowCamera.projectionMatrix)
        shadowMatrix.multiply(shadowCamera.matrixWorldInverse)
    }

    fun copy(source: LightShadow<E>): LightShadow<E> {

        this.camera.copy(source.camera, true)

        this.bias = source.bias
        this.radius = source.radius

        this.mapSize.copy(source.mapSize)

        return this
    }

    override fun clone(): LightShadow<E> {
        return LightShadow(camera).copy(this)
    }

}

class PointLightShadow : LightShadow<PerspectiveCamera>(PerspectiveCamera(90, 1, 0.5, 500)) {

    private val cubeDirections = listOf(
            Vector3(1, 0, 0), Vector3(-1, 0, 0), Vector3(0, 0, 1),
            Vector3(0, 0, -1), Vector3(0, 1, 0), Vector3(0, -1, 0)
    )

    private val cubeUps = listOf(
            Vector3(0, 1, 0), Vector3(0, 1, 0), Vector3(0, 1, 0),
            Vector3(0, 1, 0), Vector3(0, 0, 1), Vector3(0, 0, -1)
    )

    init {

        frameExtents.set(4f, 2f)
        viewportCount = 6

        with(viewports) {
            clear()
            addAll(listOf(
                    // These viewports map a cube-map onto a 2D texture with the
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
                    Vector4(2, 1, 1, 1),
                    // negative X
                    Vector4(0, 1, 1, 1),
                    // positive Z
                    Vector4(3, 1, 1, 1),
                    // negative Z
                    Vector4(1, 1, 1, 1),
                    // positive Y
                    Vector4(3, 0, 1, 1),
                    // negative Y
                    Vector4(1, 0, 1, 1)
            ))

        }

    }

    override fun updateMatrices(light: Light, viewCamera: Camera, viewportIndex: Int) {

        lightPositionWorld.setFromMatrixPosition(light.matrixWorld)
        camera.position.copy(lightPositionWorld)

        lookTarget.copy(camera.position)
        lookTarget.add(cubeDirections[viewportIndex])
        camera.up.copy(cubeUps[viewportIndex])
        camera.lookAt(lookTarget)
        camera.updateMatrixWorld()

        matrix.makeTranslation(-lightPositionWorld.x, -lightPositionWorld.y, -lightPositionWorld.z)

        projScreenMatrix.multiplyMatrices(camera.projectionMatrix, camera.matrixWorldInverse)
        this.frustum.setFromMatrix(projScreenMatrix)

    }

}

class SpotLightShadow : LightShadow<PerspectiveCamera>(PerspectiveCamera(50, 1f, 0.5f, 500f)) {

    override fun updateMatrices(light: Light, viewCamera: Camera, viewportIndex: Int) {

        light as SpotLight

        val fov = RAD2DEG * 2 * light.angle
        val aspect = this.mapSize.width / this.mapSize.height
        val far = light.distance

        if (fov != camera.fov || aspect != camera.aspect || far != camera.far) {

            camera.fov = fov
            camera.aspect = aspect
            camera.far = far
            camera.updateProjectionMatrix()

        }

        lightPositionWorld.setFromMatrixPosition(light.matrixWorld)
        camera.position.copy(lightPositionWorld)

        lookTarget.setFromMatrixPosition(light.target.matrixWorld)
        camera.lookAt(lookTarget)
        camera.updateMatrixWorld()

        super.updateMatrices(light, viewCamera, viewportIndex)

    }

    override fun clone(): SpotLightShadow {
        return SpotLightShadow().copy(this) as SpotLightShadow
    }

}

class DirectionalLightShadow : LightShadow<OrthographicCamera>(OrthographicCamera(-5f, 5f, 5f, -5f, 0.5f, 500f)) {

    override fun updateMatrices(light: Light, viewCamera: Camera, viewportIndex: Int) {

        light as DirectionalLight

        lightPositionWorld.setFromMatrixPosition(light.matrixWorld)
        camera.position.copy(lightPositionWorld)

        lookTarget.setFromMatrixPosition(light.target.matrixWorld)
        camera.lookAt(lookTarget)
        camera.updateMatrixWorld()

        super.updateMatrices(light, viewCamera, viewportIndex)

    }

}
