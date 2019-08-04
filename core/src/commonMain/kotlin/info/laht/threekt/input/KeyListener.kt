package info.laht.threekt.input

interface KeyListener {

    fun onKeyPressed(event: KeyEvent)

}

class KeyEvent(
    val keyCode: Int,
    val action: KeyAction
) {

    override fun toString(): String {
        return "KeyEvent(keyCode=$keyCode, action=$action)"
    }

}

enum class KeyAction {
    RELEASE,
    PRESS,
    REPEAT;
}
