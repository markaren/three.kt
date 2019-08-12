package info.laht.threekt.controls

import info.laht.threekt.Logger
import info.laht.threekt.cameras.Camera
import info.laht.threekt.cameras.CameraWithZoom
import info.laht.threekt.cameras.OrthographicCamera
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.core.EventDispatcher
import info.laht.threekt.core.EventDispatcherImpl
import info.laht.threekt.getLogger
import info.laht.threekt.input.*
import info.laht.threekt.math.*
import kotlin.math.*

private const val EPS = 0.000001f

class OrbitControls(
    private val camera: Camera,
    private val eventSource: PeripheralsEventSource
) : EventDispatcher by EventDispatcherImpl() {

    // Set to false to disable this control
    var enabled = true

    // "target" sets the location of focus, where the object orbits around
    var target = Vector3()

    // How far you can dolly in and out ( PerspectiveCamera only )
    var minDistance = 0f
    var maxDistance = Float.POSITIVE_INFINITY

    // How far you can zoom in and out ( OrthographicCamera only )
    var minZoom = 0f
    var maxZoom = Float.POSITIVE_INFINITY

    // How far you can orbit vertically, upper and lower limits.
    // Range is 0 to Math.PI radians.
    var minPolarAngle = 0f // radians
    var maxPolarAngle = PI.toFloat() // radians

    // How far you can orbit horizontally, upper and lower limits.
    // If set, must be a sub-interval of the interval [ - Math.PI, Math.PI ].
    var minAzimuthAngle = Float.NEGATIVE_INFINITY // radians
    var maxAzimuthAngle = Float.POSITIVE_INFINITY // radians

    // Set to true to enable damping (inertia)
    // If damping is enabled, you must call controls.update() in your animation loop
    var enableDamping = false
    var dampingFactor = 0.05f

    // This option actually enables dollying in and out; left as "zoom" for backwards compatibility.
    // Set to false to disable zooming
    var enableZoom = true
    var zoomSpeed = 1.0f

    // Set to false to disable rotating
    var enableRotate = true
    var rotateSpeed = 1.0f

    // Set to false to disable panning
    var enablePan = true
    var panSpeed = 1.0f
    var screenSpacePanning = false // if true, pan in screen-space
    var keyPanSpeed = 7f    // pixels moved per arrow key push

    // Set to true to automatically rotate around the target
    // If auto-rotate is enabled, you must call controls.update() in your animation loop
    var autoRotate = false
    var autoRotateSpeed = 2.0f // 30 seconds per round when fps is 60

    // Set to false to disable use of the keys
    var enableKeys = true

    // for reset
    private var target0 = target.clone()
    private var position0 = camera.position.clone()
    private var zoom0 = (camera as CameraWithZoom).zoom

    // current position in spherical coordinates
    private val spherical = Spherical()
    private val sphericalDelta = Spherical()

    private var scale = 1f
    private val panOffset = Vector3()
    private var zoomChanged = false

    private val rotateStart = Vector2()
    private val rotateEnd = Vector2()
    private val rotateDelta = Vector2()

    private val panStart = Vector2()
    private val panEnd = Vector2()
    private val panDelta = Vector2()

    private val dollyStart = Vector2()
    private val dollyEnd = Vector2()
    private val dollyDelta = Vector2()

    private var state = State.NONE

    init {

        update()

        eventSource.addKeyListener(MyKeyListener())
        eventSource.addMouseListener(MyMouseListener())

    }

    fun getPolarAngle(): Float {

        return spherical.phi

    }

    fun getAzimuthalAngle(): Float {

        return spherical.theta

    }

    fun saveState() {

        this.target0.copy(this.target)
        this.position0.copy(this.camera.position)
        this.zoom0 = (this.camera as CameraWithZoom).zoom

    }

    fun reset() {

        this.target.copy(this.target0)
        this.camera.position.copy(this.position0)
        (this.camera as CameraWithZoom).zoom = this.zoom0

        when (camera) {
            is PerspectiveCamera -> camera.updateProjectionMatrix()
            is OrthographicCamera -> camera.updateProjectionMatrix()
            else -> throw UnsupportedOperationException()
        }

        this.dispatchEvent("change", this)

        this.update()

        state = State.NONE

    }

    // this method is exposed, but perhaps it would be better if we can make it private...
    fun update(): Boolean {

        val offset = Vector3()

        // so camera.up is the orbit axis
        val quat = Quaternion().setFromUnitVectors(camera.up, Vector3(0, 1, 0))
        val quatInverse = quat.clone().inverse()

        val lastPosition = Vector3()
        val lastQuaternion = Quaternion()

        val position = this.camera.position

        offset.copy(position).sub(this.target)

        // rotate offset to "y-axis-is-up" space
        offset.applyQuaternion(quat)

        // angle from z-axis around y-axis
        spherical.setFromVector3(offset)

        if (this.autoRotate && state == State.NONE) {

            rotateLeft(getAutoRotationAngle())

        }

        if (this.enableDamping) {

            spherical.theta += sphericalDelta.theta * this.dampingFactor
            spherical.phi += sphericalDelta.phi * this.dampingFactor

        } else {

            spherical.theta += sphericalDelta.theta
            spherical.phi += sphericalDelta.phi

        }

        // restrict theta to be between desired limits
        spherical.theta = max(this.minAzimuthAngle, min(this.maxAzimuthAngle, spherical.theta))

        // restrict phi to be between desired limits
        spherical.phi = max(this.minPolarAngle, min(this.maxPolarAngle, spherical.phi))

        spherical.makeSafe()


        spherical.radius *= scale

        // restrict radius to be between desired limits
        spherical.radius = max(this.minDistance, min(this.maxDistance, spherical.radius))

        // move target to panned location

        if (this.enableDamping) {

            this.target.addScaledVector(panOffset, this.dampingFactor)

        } else {

            this.target.add(panOffset)

        }

        offset.setFromSpherical(spherical)

        // rotate offset back to "camera-up-vector-is-up" space
        offset.applyQuaternion(quatInverse)

        position.copy(this.target).add(offset)

        this.camera.lookAt(this.target)

        if (this.enableDamping) {

            sphericalDelta.theta *= (1 - this.dampingFactor)
            sphericalDelta.phi *= (1 - this.dampingFactor)

            panOffset.multiplyScalar(1 - this.dampingFactor)

        } else {

            sphericalDelta.set(0f, 0f, 0f)

            panOffset.set(0, 0, 0)

        }

        scale = 1f

        // update condition is:
        // min(camera displacement, camera rotation in radians)^2 > EPS
        // using small-angle approximation cos(x/2) = 1 - x^2 / 8

        if (zoomChanged ||
            lastPosition.distanceToSquared(this.camera.position) > EPS ||
            8 * (1 - lastQuaternion.dot(this.camera.quaternion)) > EPS
        ) {

            this.dispatchEvent("change", this)

            lastPosition.copy(this.camera.position)
            lastQuaternion.copy(this.camera.quaternion)
            zoomChanged = false

            return true

        }

        return false

    }

    fun getAutoRotationAngle(): Float {

        return 2 * PI.toFloat() / 60 / 60 * this.autoRotateSpeed

    }

    fun getZoomScale(): Float {

        return 0.95f.pow(this.zoomSpeed)

    }

    fun rotateLeft(angle: Float) {

        sphericalDelta.theta -= angle

    }

    fun rotateUp(angle: Float) {

        sphericalDelta.phi -= angle

    }

    fun panLeft(distance: Float, objectMatrix: Matrix4) {

        val v = Vector3()

        v.setFromMatrixColumn(objectMatrix, 0) // get X column of objectMatrix
        v.multiplyScalar(-distance)

        panOffset.add(v)

    }

    fun panUp(distance: Float, objectMatrix: Matrix4) {

        val v = Vector3()

        if (this.screenSpacePanning) {

            v.setFromMatrixColumn(objectMatrix, 1)

        } else {

            v.setFromMatrixColumn(objectMatrix, 0)
            v.crossVectors(this.camera.up, v)

        }

        v.multiplyScalar(distance)

        panOffset.add(v)

    }

    // deltaX and deltaY are in pixels; right and down are positive
    fun pan(deltaX: Float, deltaY: Float) {

        val offset = Vector3()

        when {
            this.camera is PerspectiveCamera -> {

                // perspective
                val position = this.camera.position
                offset.copy(position).sub(this.target)
                var targetDistance = offset.length()

                // half of the fov is center to top of screen
                targetDistance *= tan((this.camera.fov / 2) * PI.toFloat() / 180f)

                // we use only clientHeight here so aspect ratio does not distort speed
                panLeft(2 * deltaX * targetDistance / eventSource.height, this.camera.matrix)
                panUp(2 * deltaY * targetDistance / eventSource.height, this.camera.matrix)

            }
            this.camera is OrthographicCamera -> {
                // orthographic
                panLeft(
                    deltaX * (this.camera.right - this.camera.left) / this.camera.zoom / eventSource.width,
                    this.camera.matrix
                )
                panUp(
                    deltaY * (this.camera.top - this.camera.bottom) / this.camera.zoom / eventSource.height,
                    this.camera.matrix
                )
            }
            else -> {

                // camera neither orthographic nor perspective
                LOG.warn("encountered an unknown camera type - pan disabled.")
                this.enablePan = false

            }
        }

    }

    fun dollyIn(dollyScale: Float) {

        when {
            this.camera is PerspectiveCamera -> scale /= dollyScale
            this.camera is OrthographicCamera -> {

                this.camera.zoom = max(this.minZoom, min(this.maxZoom, this.camera.zoom * dollyScale))
                this.camera.updateProjectionMatrix()
                zoomChanged = true

            }
            else -> {

                LOG.warn("encountered an unknown camera type - dolly/zoom disabled.")
                this.enableZoom = false

            }
        }

    }

    fun dollyOut(dollyScale: Float) {

        when {
            this.camera is PerspectiveCamera -> scale *= dollyScale
            this.camera is OrthographicCamera -> {

                this.camera.zoom = max(this.minZoom, min(this.maxZoom, this.camera.zoom / dollyScale))
                this.camera.updateProjectionMatrix()
                zoomChanged = true

            }
            else -> {

                LOG.warn("encountered an unknown camera type - dolly/zoom disabled.")
                this.enableZoom = false

            }
        }

    }

    private fun handleKeyDown(event: KeyEvent) {

        var needsUpdate = true

        when (event.keyCode) {
            Keys.UP -> {
                pan(0f, keyPanSpeed)
            }
            Keys.BOTTOM -> {
                pan(0f, -keyPanSpeed)
            }
            Keys.LEFT -> {
                pan(keyPanSpeed, 0f)
            }
            Keys.RIGHT -> {
                pan(-keyPanSpeed, 0f)
            }
            else -> needsUpdate = false
        }

        if (needsUpdate) {
            this.update()
        }

    }

    private fun handleMouseDownRotate(event: MouseEvent) {
        rotateStart.set(event.clientX.toFloat(), event.clientY.toFloat())
    }

    private fun handleMouseDownDolly(event: MouseEvent) {
        dollyStart.set(event.clientX.toFloat(), event.clientY.toFloat())
    }

    private fun handleMouseDownPan(event: MouseEvent) {
        panStart.set(event.clientX.toFloat(), event.clientY.toFloat())
    }


    private fun handleMouseMoveRotate(event: MouseEvent) {
        rotateEnd.set(event.clientX.toFloat(), event.clientY.toFloat())

        rotateDelta.subVectors(rotateEnd, rotateStart).multiplyScalar(rotateSpeed)

        rotateLeft(2 * PI.toFloat() * rotateDelta.x / eventSource.width) // yes, height

        rotateUp(2 * PI.toFloat() * rotateDelta.y / eventSource.height)

        rotateStart.copy(rotateEnd)

        update()
    }

    private fun handleMouseMoveDolly(event: MouseEvent) {
        dollyEnd.set(event.clientX.toFloat(), event.clientY.toFloat())

        dollyDelta.subVectors(dollyEnd, dollyStart)

        if (dollyDelta.y > 0) {

            dollyIn(getZoomScale())

        } else if (dollyDelta.y < 0) {

            dollyOut(getZoomScale())

        }

        dollyStart.copy(dollyEnd)

        update()
    }

    private fun handleMouseMovePan(event: MouseEvent) {
        panEnd.set(event.clientX.toFloat(), event.clientY.toFloat())

        panDelta.subVectors(panEnd, panStart).multiplyScalar(panSpeed)

        pan(panDelta.x, panDelta.y)

        panStart.copy(panEnd)

        update()
    }

    private fun handleMouseWheel(event: MouseWheelEvent) {

        if (event.deltaY < 0) {

            dollyOut(getZoomScale())

        } else if (event.deltaY > 0) {

            dollyIn(getZoomScale())

        }

        update()

    }

    private inner class MyKeyListener : KeyListener {

        override fun onKeyPressed(event: KeyEvent) {
            if (enabled && enableKeys && enablePan) {
                handleKeyDown(event)
            }
        }
    }

    private inner class MyMouseListener : MouseAdapter() {

        override fun onMouseDown(event: MouseEvent) {
            if (enabled) {
                when (event.button) {
                    MouseButtons.LEFT -> {
                        if (enableRotate) {
                            handleMouseDownRotate(event)
                            state = State.ROTATE
                        }
                    }
                    MouseButtons.MIDDLE -> {
                        if (enableZoom) {
                            handleMouseDownDolly(event)
                            state = State.DOLLY
                        }
                    }
                    MouseButtons.RIGHT -> {
                        if (enablePan) {
                            handleMouseDownRotate(event)
                            handleMouseDownPan(event)
                            state = State.PAN
                        }
                    }
                }

                if (state != State.NONE) {

                    val mouseMoveListener = MyMouseMoveListener()
                    eventSource.addMouseListener(mouseMoveListener)
                    eventSource.addMouseListener(MyMouseUpListener(mouseMoveListener))

                    dispatchEvent("start", this)

                }

            }
        }

        override fun onMouseWheel(event: MouseWheelEvent) {
            if (enabled && enableZoom && !(state != State.NONE && state != State.ROTATE)) {
                handleMouseWheel(event)
            }
        }

    }

    private inner class MyMouseMoveListener : MouseAdapter() {

        override fun onMouseMove(event: MouseEvent) {
            if (enabled) {

                when (state) {
                    State.ROTATE -> {
                        if (enableRotate) {
                            handleMouseMoveRotate(event)
                        }
                    }
                    State.DOLLY -> {
                        if (enableZoom) {
                            handleMouseMoveDolly(event)
                        }
                    }
                    State.PAN -> {
                        if (enablePan)
                            handleMouseMovePan(event)
                    }
                    State.NONE -> TODO()
                }

            }
        }
    }

    private inner class MyMouseUpListener(
        private val moveListener: MyMouseMoveListener
    ) : MouseAdapter() {

        override fun onMouseUp(event: MouseEvent) {
            if (enabled) {

                eventSource.removeMouseListener(moveListener)
                eventSource.removeMouseListener(this)

                dispatchEvent("end", this)
                state = State.NONE

            }
        }
    }

    private companion object {

        val LOG: Logger = getLogger(OrbitControls::class)

    }

}

private object Keys {
    const val LEFT = 263
    const val UP = 265
    const val RIGHT = 262
    const val BOTTOM = 264
}

private object MouseButtons {

    const val LEFT: Int = 0
    const val RIGHT: Int = 1
    const val MIDDLE: Int = 2

}

private enum class State {

    NONE,
    ROTATE,
    DOLLY,
    PAN,

}
