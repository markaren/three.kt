package info.laht.threekt.controls

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.EventDispatcher
import info.laht.threekt.core.EventDispatcherImpl
import info.laht.threekt.input.*
import info.laht.threekt.math.*
import kotlin.jvm.JvmOverloads
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min

class FirstPersonControls(
        private val camera: Camera,
        private val eventSource: PeripheralsEventSource
) : EventDispatcher by EventDispatcherImpl() {

    // Set to false to disable this control
    var enabled = true

    // How fast we move as first person
    var movementSpeed = 1.0f

    // How fast the camera has to adjust when moving your mouse
    var lookSpeed = 0.005f

    var lookVertical = true
    var autoForward = false

    var activeLook = true

    var heightSpeed = false
    var heightCoef = 1.0f
    var heightMin = 0.0f
    var heightMax = 1.0f

    var constrainVertical = false
    var verticalMin = 0.0f
    var verticalMax = PI_FLOAT

    var mouseDragOn = false

    // internals

    var autoSpeedFactor = 0.0f

    var mouseX = 0
    var mouseY = 0

    var moveForward = false
    var moveBackward = false
    var moveLeft = false
    var moveRight = false
    var moveUp = false
    var moveDown = false

    var viewHalfX = 0
    var viewHalfY = 0

    // private variables

    private var lat = 0
    private var lon = 0

    var lookDirection = Vector3()
    var spherical = Spherical()
    var target = Vector3()

    private val defaultKeyListener = MyKeyListener()
    private val defaultMouseListener = MyMouseListener()

    init {
        handleResize()
        setOrientation(this)

        eventSource.addKeyListener(defaultKeyListener)
        eventSource.addMouseListener(defaultMouseListener)
    }

    private fun handleResize() {
        viewHalfX = eventSource.size.width / 2
        viewHalfY = eventSource.size.height / 2
    }

    fun lookAt(vector3: Vector3): FirstPersonControls {
        target.copy(vector3)
        camera.lookAt(target)
        setOrientation(this)
        return this
    }

    fun lookAt(x: Int, y: Int, z: Int): FirstPersonControls {
        target.set(x, y, z)
        camera.lookAt(target)
        setOrientation(this)
        return this
    }

    //this function has to be called in the animation loop
    @JvmOverloads
    fun update(delta: Float = 0.5f) {
        val targetPosition = Vector3()

        if (!enabled) return

        if (heightSpeed) {

            val y = clamp(camera.position.y, heightMin, heightMax)
            val heightDelta = y - heightMin

            this.autoSpeedFactor = delta * (heightDelta * heightCoef)

        } else {

            this.autoSpeedFactor = 0.0f

        }

        val actualMoveSpeed = delta * movementSpeed

        if (moveForward || (autoForward && !moveBackward)) {
            camera.translateZ(-(actualMoveSpeed + autoSpeedFactor))
        }
        if (moveBackward) camera.translateZ(actualMoveSpeed)

        if (moveLeft) camera.translateX(-actualMoveSpeed)
        if (moveRight) camera.translateX(actualMoveSpeed)

        if (moveUp) camera.translateY(-actualMoveSpeed)
        if (moveDown) camera.translateY(actualMoveSpeed)

        var actualLookSpeed = delta * lookSpeed

        if (!activeLook) {

            actualLookSpeed = 0.0f

        }

        var verticalLookRatio = 1.0

        if (constrainVertical) {

            verticalLookRatio = PI / (verticalMax - verticalMin)

        }

        lon -= (mouseX * actualLookSpeed).toInt()
        if (lookVertical) lat -= (mouseY * actualLookSpeed * verticalLookRatio).toInt()

        lat = max(-85, min(85, lat))

        var phi = degToRad(90 - lat)
        val theta = degToRad(lon)

        if (constrainVertical) {

            phi = mapLinear(phi, 0.0f, PI_FLOAT, verticalMin, verticalMax)

        }

        val position = camera.position

        targetPosition.setFromSphericalCoords(1.0f, phi, theta).add(position)

        camera.lookAt(targetPosition)
    }

    fun dispose() {
        eventSource.removeKeyListener(defaultKeyListener)
        eventSource.removeMouseListener(defaultMouseListener)
    }

    private fun setOrientation(controls: FirstPersonControls) {
        val quaternion = controls.camera.quaternion

        lookDirection.set(0, 0, -1).applyQuaternion(quaternion)
        spherical.setFromVector3(lookDirection)

        lat = (90 - radToDeg(spherical.phi)).toInt()
        lon = radToDeg(spherical.theta).toInt()
    }

    private inner class MyKeyListener : KeyAdapter() {
        override fun onKeyPressed(event: KeyEvent) {
            when (event.keyCode) {
                38, 87, 265 -> moveForward = true //Up, W (265 = up)
                37, 65, 263 -> moveLeft = true //Left, A (263 = left)
                40, 83, 264 -> moveBackward = true //Down S (264 = down)
                39, 68, 262 -> moveRight = true //Right D (262 = right)
                82 -> moveUp = true //R
                70 -> moveDown = true //F
            }
        }

        override fun onKeyReleased(event: KeyEvent) {
            when (event.keyCode) {
                38, 87, 265 -> moveForward = false //Up, W (265 = up)
                37, 65, 263 -> moveLeft = false //Left, A (263 = left)
                40, 83, 264 -> moveBackward = false //Down S (264 = down)
                39, 68, 262 -> moveRight = false //Right D (262 = right)
                82 -> moveUp = false //R
                70 -> moveDown = false //F
            }
        }

    }

    private inner class MyMouseListener : MouseAdapter() {
        override fun onMouseDown(button: Int, event: MouseEvent) {
            if (activeLook) {
                when (button) {
                    0 -> moveForward = true //Left click
                    1 -> moveBackward = true //Right click
                }
            }
            mouseDragOn = true
        }

        override fun onMouseUp(button: Int, event: MouseEvent) {
            if (activeLook) {
                when (button) {
                    0 -> moveForward = false //Left click
                    1 -> moveBackward = false //Right click
                }
            }
            mouseDragOn = false
        }

        override fun onMouseMove(event: MouseEvent) {
            mouseX = event.clientX - viewHalfX
            mouseY = event.clientY - viewHalfY
        }
    }

    companion object {
        private const val PI_FLOAT = PI.toFloat()
    }

}
