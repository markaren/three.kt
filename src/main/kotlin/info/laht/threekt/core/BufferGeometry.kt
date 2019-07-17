package info.laht.threekt.core

import info.laht.threekt.math.*
import java.util.concurrent.atomic.AtomicInteger
import info.laht.threekt.math.Box3
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt


open class BufferGeometry : GeometryBase<BufferGeometry>() {

    internal val id = geometryIdCount.getAndAdd(2)

    var index: IntBufferAttribute? = null
        private set

    val attributes = BufferAttributes()

    internal val groups = mutableListOf<GeometryGroup>()

    internal var drawRange = DrawRange(0, Int.MAX_VALUE)

    fun setIndex(index: IntBufferAttribute) {
        this.index = index
    }

    fun setIndex(index: IntArray) {
        this.index = IntBufferAttribute(index, 1)
    }

    fun addAttribute(
        name: String, attribute: BufferAttribute
    ): BufferGeometry {
        attributes[name] = attribute
        return this
    }

    fun getAttribute(name: String): BufferAttribute? {
        return attributes[name]
    }

    fun removeAttribute(name: String) {
        attributes.remove(name)
    }

    @JvmOverloads
    fun addGroup(start: Int, count: Int, materialIndex: Int = 0) {
        groups.add(GeometryGroup(start, count, materialIndex))
    }

    fun clearGroups() {
        groups.clear()
    }

    fun setDrawRange(start: Int, count: Int) {
        this.drawRange.start = start;
        this.drawRange.count = count;
    }

    /**
     * Bakes matrix transform directly into vertex coordinates.
     */
    override fun applyMatrix(matrix: Matrix4): BufferGeometry {
        this.attributes["position"]?.also { position ->
            matrix.applyToBufferAttribute(position)
            position.needsUpdate = true
        }

        this.attributes["normal"]?.also { normal ->
            val normalMatrix = Matrix3().getNormalMatrix(matrix)

            normalMatrix.applyToBufferAttribute(normal)
            normal.needsUpdate = true
        }


        this.attributes["tangent"]?.also { tangent ->
            val normalMatrix = Matrix3().getNormalMatrix(matrix);

            // Tangent is vec4, but the '.w' component is a sign value (+1/-1).
            normalMatrix.applyToBufferAttribute(tangent);
            tangent.needsUpdate = true;
        }

        if (this.boundingBox != null) {
            this.computeBoundingBox();
        }

        if (this.boundingSphere != null) {
            this.computeBoundingSphere();
        }

        return this;
    }

    /**
     * Computes bounding box of the geometry, updating Geometry.boundingBox attribute.
     * Bounding boxes aren't computed by default. They need to be explicitly computed, otherwise they are null.
     */
    override fun computeBoundingBox() {

        if (this.boundingBox == null) {
            this.boundingBox = Box3()
        }

        val boundingBox = this.boundingBox!!

        val position = this.attributes.position;
        if (position != null) {
            boundingBox.setFromBufferAttribute(position);

        } else {
            boundingBox.makeEmpty();
        }

        if (boundingBox.min.x.isNaN() || boundingBox.min.y.isNaN() || boundingBox.min.z.isNaN()) {
            println("THREE.BufferGeometry.computeBoundingBox: Computed min/max have NaN values. The 'position' attribute is likely to have NaN values.")
        }
    }

    /**
     * Computes bounding sphere of the geometry, updating Geometry.boundingSphere attribute.
     * Bounding spheres aren't' computed by default. They need to be explicitly computed, otherwise they are null.
     */
    override fun computeBoundingSphere() {

        val box = Box3()
        val boxMorphTargets = Box3()
        val vector = Vector3()

        if (this.boundingSphere == null) {
            this.boundingSphere = Sphere()
        }

        val position = this.attributes.position
        if (position != null) {

            // first, find the center of the bounding sphere
            val center = this.boundingSphere!!.center;

            box.setFromBufferAttribute(position)
            box.getCenter(center)

            // second, try to find a boundingSphere with a radius smaller than the
            // boundingSphere of the boundingBox: sqrt(3) smaller in the best case

            var maxRadiusSq = 0.toFloat()
            for (i in 0 until position.count) {
                vector.fromBufferAttribute(position, i)
                maxRadiusSq = max(maxRadiusSq, center.distanceToSquared(vector));
            }

            this.boundingSphere!!.radius = sqrt(maxRadiusSq).toFloat()

            if (this.boundingSphere!!.radius.isNaN()) {
                println("THREE.BufferGeometry.computeBoundingSphere(): Computed radius is NaN. The 'position' attribute is likely to have NaN values.");
            }

        }
    }

    /**
     * Computes vertex normals by averaging face normals.
     */
    fun computeVertexNormals() {

        val index = this.index;
        val attributes = this.attributes;

        if (attributes.position != null) {

            val positions = attributes.position.array

            if (attributes.normal == null) {

                this.addAttribute("normal", DoubleBufferAttribute(DoubleArray(positions.size), 3));

            } else {

                // reset existing normals to zero
                val array = attributes.normal.array;
                for (i in 0 until array.size) {
                    array[i] = 0.toFloat()
                }

            }

            val normals = attributes.normal!!.array;

            var vA: Int
            var vB: Int
            var vC: Int
            val pA = Vector3()
            val pB = Vector3()
            val pC = Vector3()
            val cb = Vector3()
            val ab = Vector3();

            // indexed elements

            if (index != null) {

                val indices = index.array

                for (i in 0 until index.count) {

                    vA = indices[i + 0] * 3
                    vB = indices[i + 1] * 3
                    vC = indices[i + 2] * 3

                    pA.fromArray(positions, vA)
                    pB.fromArray(positions, vB)
                    pC.fromArray(positions, vC)

                    cb.subVectors(pC, pB)
                    ab.subVectors(pA, pB)
                    cb.cross(ab)

                    normals[vA] += cb.x
                    normals[vA + 1] += cb.y
                    normals[vA + 2] += cb.z

                    normals[vB] += cb.x
                    normals[vB + 1] += cb.y
                    normals[vB + 2] += cb.z

                    normals[vC] += cb.x
                    normals[vC + 1] += cb.y
                    normals[vC + 2] += cb.z

                }

            } else {

                // non-indexed elements (unconnected triangle soup)
                for (i in 0 until positions.size step 9) {

                    pA.fromArray(positions, i)
                    pB.fromArray(positions, i + 3)
                    pC.fromArray(positions, i + 6)

                    cb.subVectors(pC, pB)
                    ab.subVectors(pA, pB)
                    cb.cross(ab)

                    normals[i] = cb.x
                    normals[i + 1] = cb.y
                    normals[i + 2] = cb.z

                    normals[i + 3] = cb.x
                    normals[i + 4] = cb.y
                    normals[i + 5] = cb.z

                    normals[i + 6] = cb.x
                    normals[i + 7] = cb.y
                    normals[i + 8] = cb.z

                }

            }

            this.normalizeNormals()

            attributes.normal.needsUpdate = true

        }
    }

    @JvmOverloads
    fun merge(geometry: BufferGeometry, offset: Int = 0): BufferGeometry {

        for (key in attributes.keys) {
            if (geometry.attributes[key] == null) {
                continue
            }

            val attribute1 = attributes[key]!!
            val l1 = when (attribute1) {
                is IntBufferAttribute -> attribute1.size
                is FloatBufferAttribute -> attribute1.size
                is DoubleBufferAttribute -> attribute1.size
            }

            val attribute2 = geometry.attributes[key]!!
            val l2 = when (attribute2) {
                is IntBufferAttribute -> attribute2.size
                is FloatBufferAttribute -> attribute2.size
                is DoubleBufferAttribute -> attribute2.size
            }

            val attributeOffset = attribute2.itemSize * offset
            val length = min(l2, l1 - attributeOffset)

            var j = attributeOffset
            for (i in 0 until length) {
                when (attribute1) {
                    is IntBufferAttribute -> attribute1.array[j] = (attribute2 as IntBufferAttribute).array[i]
                    is FloatBufferAttribute -> attribute1.array[j] = (attribute2 as FloatBufferAttribute).array[i]
                    is DoubleBufferAttribute -> attribute1.array[j] = (attribute2 as DoubleBufferAttribute).array[i]
                }
                j++
            }
        }

        return this
    }

    fun normalizeNormals() {

        val vector = Vector3()
        val normals = this.attributes.normal!!

        for (i in 0 until normals.count) {

            vector.x = normals.getX(i)
            vector.y = normals.getY(i)
            vector.z = normals.getZ(i)

            vector.normalize()

            normals.setXYZ(i, vector.x, vector.y, vector.z)

        }


    }

    private companion object {
        val geometryIdCount = AtomicInteger(1) // BufferGeometry uses odd numbers as Id
    }

}