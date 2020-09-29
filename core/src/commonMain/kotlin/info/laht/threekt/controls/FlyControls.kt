package info.laht.threekt.controls

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.EventDispatcher
import info.laht.threekt.core.EventDispatcherImpl
import info.laht.threekt.input.*
import info.laht.threekt.math.Quaternion
import info.laht.threekt.math.Vector3
import kotlin.jvm.JvmOverloads

class FlyControls(private val camera: Camera, private val eventSource: PeripheralsEventSource) : EventDispatcher by EventDispatcherImpl() {

	var movementSpeed = 1.0
	var rollSpeed = 0.005
	var dragToLook = false
	var autoForward = false

	private val tmpQuaternion = Quaternion()
	private var mouseStatus = 0
	private var moveStateUp = 0
	private var moveStateDown = 0
	private var moveStateLeft = 0
	private var moveStateRight = 0
	private var moveStateForward = 0
	private var moveStateBack = 0
	private var moveStatePitchUp = 0
	private var moveStatePitchDown = 0.0
	private var moveStateYawLeft = 0.0
	private var moveStateYawRight = 0
	private var moveStateRollLeft = 0
	private var moveStateRollRight = 0

	private var moveVector = Vector3()
	private var rotationVector = Vector3()

	private var movementSpeedMultiplier = 0.0

	private val defaultKeyListener = MyKeyListener()
	private val defaultMouseListener = MyMouseListener()

	init {
		eventSource.addKeyListener(defaultKeyListener)
		eventSource.addMouseListener(defaultMouseListener)
		updateMovementVector()
		updateRotationVector()
	}

	private fun updateMovementVector() {
		val forward = if (moveStateForward != 0 || autoForward && moveStateBack == 0) 1 else 0

		moveVector.x = -moveStateLeft.toFloat() + moveStateRight
		moveVector.y = -moveStateDown.toFloat() + moveStateUp
		moveVector.z = -forward + moveStateBack.toFloat()
	}

	private fun updateRotationVector() {
		rotationVector.x = -moveStatePitchDown.toFloat() + moveStatePitchUp
		rotationVector.y = -moveStateYawRight.toFloat() + moveStateYawLeft.toFloat()
		rotationVector.z = -moveStateRollRight.toFloat() + moveStateRollLeft
	}

	//this function has to be called in the animation loop
	@JvmOverloads
	fun update(delta: Float = 0.5f) {
		val lastQuaternion = Quaternion()
		val lastPosition = Vector3()
		val moveMult = delta * movementSpeed
		val rotMult = delta * rollSpeed

		camera.translateX(moveVector.x * moveMult.toFloat())
		camera.translateY(moveVector.y * moveMult.toFloat())
		camera.translateZ(moveVector.z * moveMult.toFloat())

		tmpQuaternion.set(rotationVector.x * rotMult, rotationVector.y * rotMult, rotationVector.z * rotMult, 1).normalize()
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
			var update = true
			when (event.keyCode) {
				16 -> movementSpeedMultiplier = .1
				87 -> moveStateForward = 1
				83 -> moveStateBack = 1
				65 -> moveStateLeft = 1
				68 -> moveStateRight = 1
				82 -> moveStateUp = 1
				70 -> moveStateDown = 1
				38, 265 -> moveStatePitchUp = 1
				40, 264 -> moveStatePitchDown = 1.0
				37 -> moveStateYawLeft = 1.0
				39 -> moveStateYawRight = 1
				81 -> moveStateRollLeft = 1
				69 -> moveStateRollRight = 1
				342 -> return //alt key
				else -> update = false
			}
			updateMovementVector()
			updateRotationVector()
			if (update) {
				update()
			}
		}

		override fun onKeyReleased(event: KeyEvent) {
			var needUpdate = true
			when (event.keyCode) {
				16 -> movementSpeedMultiplier = 1.0
				87 -> moveStateForward = 0
				83 -> moveStateBack = 0
				65 -> moveStateLeft = 0
				68 -> moveStateRight = 0
				82 -> moveStateUp = 0
				70 -> moveStateDown = 0
				38, 265 -> moveStatePitchUp = 0
				40, 264 -> moveStatePitchDown = 0.0
				37 -> moveStateYawLeft = 0.0
				39 -> moveStateYawRight = 0
				81 -> moveStateRollLeft = 0
				69 -> moveStateRollRight = 0
				342 -> return //alt key
				else -> needUpdate = false
			}
			updateMovementVector()
			updateRotationVector()
			if (needUpdate) {
				update()
			}
		}
	}

	private inner class MyMouseListener : MouseAdapter() {
		override fun onMouseDown(button: Int, event: MouseEvent) {
			if (dragToLook) {
				mouseStatus++
			} else {
				when (button) {
					0 -> moveStateForward = 1
					1 -> moveStateBack = 1
				}
				updateMovementVector()
			}
			update()
		}

		override fun onMouseUp(button: Int, event: MouseEvent) {
			if (dragToLook) {
				mouseStatus--
				moveStatePitchDown = 0.0
				moveStateYawLeft = moveStatePitchDown
			} else {
				when (button) {
					0 -> moveStateForward = 0
					1 -> moveStateBack = 0
				}
				updateMovementVector()
			}
			updateRotationVector()
			update()
		}

		override fun onMouseMove(event: MouseEvent) {
			if (!dragToLook || mouseStatus > 0) {
				val halfWidth = eventSource.size.width / 2
				val halfHeight = eventSource.size.height / 2
				moveStateYawLeft = -(event.clientX - halfWidth) / halfWidth.toDouble()
				moveStatePitchDown = (event.clientY - halfHeight) / halfHeight.toDouble()
				updateRotationVector()
				update()
			}
		}
	}

	companion object {
		private const val EPS = 0.000001
	}

}