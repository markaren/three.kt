package info.laht.threekt.helpers

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.core.IntBufferAttribute
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.math.Box3
import info.laht.threekt.objects.LineSegments

class Box3Helper(
        private val box: Box3,
        private val color: Int = 0xffff00
) : LineSegments(createGeometry(), LineBasicMaterial().apply { this.color.set(color) }) {

    init {

        this.geometry.computeBoundingSphere()

    }

    override fun updateMatrixWorld(force: Boolean) {

        val box = this.box

        if (box.isEmpty()) return

        box.getCenter(this.position)

        box.getSize(this.scale)

        this.scale.multiplyScalar(0.5f)


        super.updateMatrixWorld(force)
    }

    private companion object {

        fun createGeometry(): BufferGeometry {
            val indices = intArrayOf(
                    0, 1, 1, 2, 2, 3,
                    3, 0, 4, 5, 5, 6,
                    6, 7, 7, 4, 0, 4,
                    1, 5, 2, 6, 3, 7
            )

            val positions = floatArrayOf(
                    1f, 1f, 1f, -1f, 1f, 1f,
                    -1f, -1f, 1f, 1f, -1f, 1f,
                    1f, 1f, -1f, -1f, 1f, -1f,
                    -1f, -1f, -1f, 1f, -1f, -1f)

            val geometry = BufferGeometry()

            geometry.setIndex(IntBufferAttribute(indices, 1))

            geometry.addAttribute("position", FloatBufferAttribute(positions, 3))

            return geometry
        }

    }

}
