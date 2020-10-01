package info.laht.threekt.input

interface KeyListener {

    fun onKeyPressed(event: KeyEvent)
    fun onKeyReleased(event: KeyEvent)
    fun onKeyRepeat(event: KeyEvent)

}

data class KeyEvent(val keyCode: Int)
