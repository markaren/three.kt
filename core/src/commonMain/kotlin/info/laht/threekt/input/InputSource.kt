package info.laht.threekt.input

interface PeripheralsEventSource {

    val width: Int
    val height: Int

    fun addKeyListener(keyListener: KeyListener)

    fun removeKeyListener(listener: KeyListener): Boolean

    fun addMouseListener(mouseListener: MouseListener)

    fun removeMouseListener(mouseListener: MouseListener): Boolean

}

abstract class AbstractPeripheralsEventSource : PeripheralsEventSource {

    protected var keyListeners: MutableList<KeyListener>? = null
    protected var mouseListeners: MutableList<MouseListener>? = null

    override fun addKeyListener(listener: KeyListener) {
        keyListeners?.add(listener) ?: run {
            keyListeners = mutableListOf(listener)
        }
    }

    override fun addMouseListener(listener: MouseListener) {
        mouseListeners?.add(listener) ?: run {
            mouseListeners = mutableListOf(listener)
        }
    }

    override fun removeKeyListener(listener: KeyListener): Boolean {
        return keyListeners?.remove(listener) ?: false
    }

    override fun removeMouseListener(listener: MouseListener): Boolean {
        return mouseListeners?.remove(listener) ?: false
    }

}
