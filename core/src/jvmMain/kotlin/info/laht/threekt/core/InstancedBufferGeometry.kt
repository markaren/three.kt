package info.laht.threekt.core

class InstancedBufferGeometry : BufferGeometry() {

    var maxInstancedCount: Int = 0

    fun copy(source: InstancedBufferGeometry): InstancedBufferGeometry {

        super.copy(source)
        this.maxInstancedCount = source.maxInstancedCount

        return this

    }

    override fun clone(): InstancedBufferGeometry {
        return InstancedBufferGeometry().copy(this)
    }

}
