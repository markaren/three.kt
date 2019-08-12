package info.laht.threekt.objects

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.math.Vector3
import kotlin.jvm.JvmOverloads

open class LineSegments @JvmOverloads constructor(
    geometry: BufferGeometry? = null,
    material: LineBasicMaterial? = null
) : Line(geometry, material) {

    override fun computeLineDistances(): LineSegments {

        val start = Vector3()
        val end = Vector3()

        if (geometry.index == null) {

            val positionAttribute = geometry.attributes.position!!
            val lineDistances = FloatArray(positionAttribute.count * 2)

            for (i in 0 until positionAttribute.count step 2) {
                positionAttribute.toVector3(i, start)
                positionAttribute.toVector3(i + 1, end)

                lineDistances[i] = if (i == 0) 0f else lineDistances[i - 1]
                lineDistances[i + 1] = lineDistances[i] + start.distanceTo(end)
            }

            geometry.addAttribute("lineDistance", FloatBufferAttribute(lineDistances, 1))

        } else {

            println("LineSegments.computeLineDistances(): Computation only possible with non-indexed BufferGeometry.")

        }

        return this
    }

}
