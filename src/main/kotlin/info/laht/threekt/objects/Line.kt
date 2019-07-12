package info.laht.threekt.objects

import info.laht.threekt.core.*
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.materials.Material
import info.laht.threekt.math.Vector3

class Line(

    val geometry: BufferGeometry = BufferGeometry(),
    val material: Material = LineBasicMaterial()

): Object3D() {

    fun computeLineDistances(): Line {

        val start = Vector3()
        val end = Vector3()

        // we assume non-indexed geometry
        if (geometry.index == null) {

            val positionAttribute = geometry.attributes.position!!
            val lineDistances = mutableListOf(0.0)

            for (i in 0 until positionAttribute.count) {
                start.fromBufferAttribute(positionAttribute, i - 1);
                end.fromBufferAttribute(positionAttribute, i);

                lineDistances[i] = lineDistances[i - 1];
                lineDistances[i] += start.distanceTo(end);
            }

            geometry.addAttribute("lineDistance", DoubleBufferAttribute(lineDistances.toDoubleArray(), 1));

        } else {
            println( "THREE.Line.computeLineDistances(): Computation only possible with non-indexed BufferGeometry." )
        }

        return this
    }

    override fun raycast(raycaster: Raycaster, intersects: List<Intersection>) {

    }

}
