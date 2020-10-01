package info.laht.threekt.controls

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.EventDispatcher
import info.laht.threekt.core.EventDispatcherImpl
import info.laht.threekt.input.*
import info.laht.threekt.math.Quaternion
import info.laht.threekt.math.Vector3
import kotlin.jvm.JvmOverloads

class FlyControls(
        private val camera: Camera,
        private val eventSource: PeripheralsEventSource
) : EventDispatcher by EventDispatcherImpl() {

    var movementSpeed = 1.0f
    var rollSpeed = 0.005f
    var dragToLook = false
    var autoForward = false

    private val tmpQuaternion = Quaternion()
    private var mouseStatus = 0
    private var moveStateUp = 0
    private var moveStateDown = 0.0f
    private var moveStateLeft = 0.0f
    private var moveStateRight = 0.0f
    private var moveStateForward = 0
    private var moveStateBack = 0
    private var moveStatePitchUp = 0
    private var moveStatePitchDown = 0.0f
    private var moveStateYawLeft = 0.0f
    private var moveStateYawRight = 0.0f
    private var moveStateRollLeft = 0.0f
    private var moveStateRollRight = 0

    private var moveVector = Vector3()
    private var rotationVector = Vector3()

    private var movementSpeedMultiplier = 0.0

    private val defaultKeyListener = MyKeyListener()
    private val defaultMouseListener = MyMouseListener()

    //used for updating
    private val lastQuaternion = Quaternion()
    private val lastPosition = Vector3()

    init {
        eventSource.addKeyListener(defaultKeyListener)
        eventSource.addMouseListener(defaultMouseListener)
        updateMovementVector()
        updateRotationVector()
    }

    private fun updateMovementVector() {
        val forward = if (moveStateForward != 0 || autoForward && moveStateBack == 0) 1.0f else 0.0f

        moveVector.x = -moveStateLeft + moveStateRight
        moveVector.y = -moveStateDown + moveStateUp
        moveVector.z = -forward + moveStateBack
    }

    private fun updateRotationVector() {
        rotationVector.x = -moveStatePitchDown + moveStatePitchUp
        rotationVector.y = -moveStateYawRight + moveStateYawLeft
        rotationVector.z = -moveStateRollRight + moveStateRollLeft
    }

    //this function has to be called in the animation loop
    @JvmOverloads
    fun update(delta: Float = 0.5f) {
        val moveMult = delta * movementSpeed
        val rotMult = delta * rollSpeed

        camera.translateX(moveVector.x * moveMult)
        camera.translateY(moveVector.y * moveMult)
        camera.translateZ(moveVector.z * moveMult)

        tmpQuaternion.set(rotationVector.x * rotMult, rotationVector.y * rotMult, rotationVector.z * rotMult, 1)
                .normalize()
        camera.quaternion.multiply(tmpQuaternion)

        if (lastPosition.distanceToSquared(camera.position) > EPS || 8 * (1 - lastQuaternion.dot(camera.quaternion)) > EPS) {
            dispatchEvent("change", this)
            lastQuaternion.copy(camera.quaternion)
            lastPosition.copy(camera.position)
        }
    }

    fun dispose() {
        eventSource.removeKeyListener(defaultKeyListener)
        eventSource.removeMouseListener(defaultMouseListener)
    }

    private inner class MyKeyListener : KeyListener {
        override fun onKeyPressed(event: KeyEvent) {
            when (event.keyCode) {
                16 -> movementSpeedMultiplier = 0.1 //Shift
                87 -> moveStateForward = 1 //W
                83 -> moveStateBack = 1 //S
                65 -> moveStateLeft = 1.0f //A
                68 -> moveStateRight = 1.0f //D
                82 -> moveStateUp = 1 //R
                70 -> moveStateDown = 1.0f //F
                38, 265 -> moveStatePitchUp = 1 //Up
                40, 264 -> moveStatePitchDown = 1.0f //Down
                37, 263 -> moveStateYawLeft = 1.0f //Left
                39, 262 -> moveStateYawRight = 1.0f //Right
                81 -> moveStateRollLeft = 1.0f //Q
                69 -> moveStateRollRight = 1 //E
                else -> return
            }
            updateMovementVector()
            updateRotationVector()
        }

        override fun onKeyReleased(event: KeyEvent) {
            when (event.keyCode) {
                16 -> movementSpeedMultiplier = 1.0 //Shift
                87 -> moveStateForward = 0 //W
                83 -> moveStateBack = 0 //S
                65 -> moveStateLeft = 0.0f //A
                68 -> moveStateRight = 0.0f //D
                82 -> moveStateUp = 0 //R
                70 -> moveStateDown = 0.0f //F
                38, 265 -> moveStatePitchUp = 0 //Up
                40, 264 -> moveStatePitchDown = 0.0f //Down
                37, 263 -> moveStateYawLeft = 0.0f //Left
                39, 262 -> moveStateYawRight = 0.0f //Right
                81 -> moveStateRollLeft = 0.0f //Q
                69 -> moveStateRollRight = 0 //E
                342 -> return
            }
            updateMovementVector()
            updateRotationVector()
        }
    }

    private inner class MyMouseListener : MouseAdapter() {
        override fun onMouseDown(button: Int, event: MouseEvent) {
            if (dragToLook) {
                mouseStatus++
            } else {
                when (button) {
                    0 -> moveStateForward = 1 //Left click
                    1 -> moveStateBack = 1 //Right click
                }
                updateMovementVector()
            }
        }

        override fun onMouseUp(button: Int, event: MouseEvent) {
            if (dragToLook) {
                mouseStatus--
                moveStatePitchDown = 0.0f
                moveStateYawLeft = moveStatePitchDown
            } else {
                when (button) {
                    0 -> moveStateForward = 0
                    1 -> moveStateBack = 0
                }
                updateMovementVector()
            }
            updateRotationVector()
        }

        override fun onMouseMove(event: MouseEvent) {
            if (!dragToLook || mouseStatus > 0) {
                val halfWidth = eventSource.size.width / 2
                val halfHeight = eventSource.size.height / 2
                moveStateYawLeft = -(event.clientX - halfWidth) / halfWidth.toFloat()
                moveStatePitchDown = (event.clientY - halfHeight) / halfHeight.toFloat()
                updateRotationVector()
            }
        }
    }

    companion object {
        private const val EPS = 0.000001
    }

}
