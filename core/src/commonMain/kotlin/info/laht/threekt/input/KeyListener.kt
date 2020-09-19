package info.laht.threekt.input

fun interface KeyListener {

    fun onKeyPressed(event: KeyEvent)

}

data class KeyEvent(
        val keyCode: Int,
        val action: KeyAction
)

enum class KeyAction {
    RELEASE,
    PRESS,
    REPEAT;
}
