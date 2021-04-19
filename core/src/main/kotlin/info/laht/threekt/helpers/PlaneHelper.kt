package info.laht.threekt.helpers

import info.laht.threekt.Side
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Plane
import info.laht.threekt.objects.Line
import info.laht.threekt.objects.Mesh
import kotlin.math.abs

class PlaneHelper(
        private val plane: Plane,
        private val size: Float = 1f,
        hex: Int = 0xffff00
) : Line(createGeometry(), LineBasicMaterial().apply { this.color.set(hex) }) {

    init {

        val positions2 = floatArrayOf(
                1f, 1f, 1f, -1f, 1f, 1f,
                -1f, -1f, 1f, 1f, 1f, 1f,
                -1f, -1f, 1f, 1f, -1f, 1f
        )
        val geometry2 = BufferGeometry().apply {
            addAttribute("position", FloatBufferAttribute(positions2, 3))
            computeBoundingSphere()
        }

        val material = MeshBasicMaterial().apply {
            color.set(hex)
            opacity = 0.2f
            transparent = true
            depthWrite = true
        }
        this.add(Mesh(geometry2, material))

    }

    override fun updateMatrixWorld(force: Boolean) {

        var scale = -this.plane.constant

        if (abs(scale) < 1e-8f) {
            scale = 1e-8f // sign does not matter
        }

        this.scale.set(0.5f * this.size, 0.5f * this.size, scale)

        val child = this.children[0] as Mesh
        child.material.side = if (scale < 0) Side.Back else Side.Front

        this.lookAt(this.plane.normal)

        super.updateMatrixWorld(force)
    }

    private companion object {

        fun createGeometry(): BufferGeometry {

            val positions = floatArrayOf(
                    1f, -1f, 1f, -1f, 1f, 1f,
                    -1f, -1f, 1f, 1f, 1f, 1f,
                    -1f, 1f, 1f, -1f, -1f, 1f,
                    1f, -1f, 1f, 1f, 1f, 1f,
                    0f, 0f, 1f, 0f, 0f, 0f
            )

            return BufferGeometry().apply {
                addAttribute("position", FloatBufferAttribute(positions, 3))
                computeBoundingSphere()
            }

        }

    }

}
