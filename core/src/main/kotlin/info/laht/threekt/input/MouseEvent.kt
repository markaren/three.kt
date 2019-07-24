package info.laht.threekt.input

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

class MouseWheelEvent(
    val deltaX: Float,
    val deltaY: Float
)
