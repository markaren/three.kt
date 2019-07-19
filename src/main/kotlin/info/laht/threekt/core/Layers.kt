package info.laht.threekt.core

class Layers {

    internal var mask = 1 or 0

    fun set(channel: Int) {
        this.mask = 1 shl channel
    }

    fun enable(channel: Int) {
        this.mask = this.mask or (1 shl channel)
    }

    fun toggle(channel: Int) {
        this.mask = this.mask xor (1 shl channel)
    }

    fun disable(channel: Int) {
        this.mask = this.mask and (1 shl channel).inv()
    }

    fun test(layers: Layers): Boolean {
        return this.mask and layers.mask != 0
    }

}
