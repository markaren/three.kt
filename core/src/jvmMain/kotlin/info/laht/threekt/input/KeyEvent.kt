package info.laht.threekt.input

class KeyEvent(
    val keyCode: Int,
    val action: KeyAction
)

enum class KeyAction {
    RELEASE,
    PRESS,
    REPEAT;
}