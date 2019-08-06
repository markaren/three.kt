package info.laht.threekt.input

interface MouseListener {

    fun onMouseDown(event: MouseEvent)

    fun onMouseUp(event: MouseEvent)

    fun onMouseMove(event: MouseEvent)

    fun onMouseWheel(event: MouseWheelEvent)
}

abstract class MouseAdapter : MouseListener {

    override fun onMouseDown(event: MouseEvent) {}

    override fun onMouseUp(event: MouseEvent) {}

    override fun onMouseMove(event: MouseEvent) {}

    override fun onMouseWheel(event: MouseWheelEvent) {}

}


class MouseWheelEvent(
    val deltaX: Float,
    val deltaY: Float
) {

    override fun toString(): String {
        return "MouseWheelEvent(deltaX=$deltaX, deltaY=$deltaY)"
    }

}

class MouseEvent {

    var clientX = 0
        private set
    private var lastClientX = 0

    var clientY = 0
        private set

    private var lastClientY = 0

    var button: Int = 0
        internal set

    internal fun updateCoordinates(clientX: Int, clientY: Int) {
        this.lastClientX = this.clientX
        this.lastClientY = this.clientY

        this.clientX = clientX
        this.clientY = clientY
    }

    override fun toString(): String {
        return "MouseEvent(clientX=$clientX, clientY=$clientY, button=$button)"
    }

}
