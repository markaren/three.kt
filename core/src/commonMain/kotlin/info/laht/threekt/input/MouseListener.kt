package info.laht.threekt.input

interface MouseListener {

    fun onMouseDown(button: Int, event: MouseEvent)

    fun onMouseUp(button: Int, event: MouseEvent)

    fun onMouseMove(event: MouseEvent)

    fun onMouseWheel(event: MouseWheelEvent)
}

abstract class MouseAdapter : MouseListener {

    override fun onMouseDown(button: Int, event: MouseEvent) {}

    override fun onMouseUp(button: Int, event: MouseEvent) {}

    override fun onMouseMove(event: MouseEvent) {}

    override fun onMouseWheel(event: MouseWheelEvent) {}

}

interface MouseWheelEvent {
    val deltaX: Float
    val deltaY: Float
}


class MouseWheelEventImpl : MouseWheelEvent {

    override var deltaX: Float = 0f
    override var deltaY: Float = 0f

    internal fun update(deltaX: Float, deltaY: Float) {
        this.deltaX = deltaX
        this.deltaY = deltaY
    }

    override fun toString(): String {
        return "MouseWheelEventImpl(deltaX=$deltaX, deltaY=$deltaY)"
    }

}


interface MouseEvent {

    val clientX: Int
    val clientY: Int

}

class MouseEventImpl : MouseEvent {

    override var clientX = 0
        private set

    override var clientY = 0
        private set

    internal fun updateCoordinates(clientX: Int, clientY: Int) {
        this.clientX = clientX
        this.clientY = clientY
    }

    override fun toString(): String {
        return "MouseEventImpl(clientX=$clientX, clientY=$clientY)"
    }

}
