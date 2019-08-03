package info.laht.threekt.core

import info.laht.threekt.math.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

open class BufferGeometry : Cloneable, EventDispatcher by EventDispatcherImpl() {

    internal val id = geometryIdCount++

    var name = ""
    val uuid = generateUUID()

    var boundingBox: Box3? = null
        protected set

    var boundingSphere: Sphere? = null
        protected set

    var index: IntBufferAttribute? = null
        private set

    val attributes = BufferAttributes()

    internal val groups = mutableListOf<GeometryGroup>()

    internal var drawRange = DrawRange(0, Int.MAX_VALUE/2)

    fun setIndex(index: IntBufferAttribute): BufferGeometry {
        this.index = index
        return this
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
        this.drawRange.start = start
        this.drawRange.count = count
    }

    fun rotateX(angle: Float): BufferGeometry {

        // rotate geometry around world x-axis
        val m1 = Matrix4()
        m1.makeRotationX(angle)
        this.applyMatrix(m1)

        return this

    }

    fun rotateY(angle: Float): BufferGeometry {

        // rotate geometry around world y-axis
        val m1 = Matrix4()
        m1.makeRotationY(angle)
        this.applyMatrix(m1)

        return this

    }

    fun rotateZ(angle: Float): BufferGeometry {

        // rotate geometry around world z-axis
        val m1 = Matrix4()
        m1.makeRotationZ(angle)
        this.applyMatrix(m1)

        return this

    }

    fun translate(x: Float, y: Float, z: Float): BufferGeometry {

        // translate geometry
        val m1 = Matrix4()
        m1.makeTranslation(x, y, z)
        this.applyMatrix(m1)

        return this

    }

    fun scale(scale: Float): BufferGeometry {
        return scale(scale, scale, scale)
    }

    fun scale(x: Float, y: Float, z: Float): BufferGeometry {

        // scale geometry
        val m1 = Matrix4()
        m1.makeScale(x, y, z)
        this.applyMatrix(m1)

        return this

    }

    fun lookAt(vector: Vector3): BufferGeometry {
        val obj = Object3DImpl()
        obj.lookAt(vector)
        obj.updateMatrix()
        this.applyMatrix(obj.matrix)

        return this
    }

    fun center(): BufferGeometry {
        val offset = Vector3()

        this.computeBoundingBox()
        boundingBox!!.getCenter(offset).negate()

        this.translate(offset.x, offset.y, offset.z)

        return this

    }

    fun setFromPoints ( points: List<Vector3> ): BufferGeometry {

        val position = FloatBufferAttribute(points.size*3, 3)

        points.forEachIndexed {i, v ->

            position[i+0] = v.x
            position[i+1] = v.y
            position[i+2] = v.z
        }

        this.addAttribute( "position",  position )

        return this

    }

    fun normalize(): BufferGeometry {
        this.computeBoundingSphere()

        val center = boundingSphere!!.center
        val radius = boundingSphere!!.radius

        val s = if (radius == 0f) 1f else 1f / radius

        val matrix = Matrix4()
        matrix.set(
            s, 0f, 0f, -s * center.x,
            0f, s, 0f, -s * center.y,
            0f, 0f, s, -s * center.z,
            0f, 0f, 0f, 1f
        )

        this.applyMatrix(matrix)

        return this
    }


    /**
     * Bakes matrix transform directly into vertex coordinates.
     */
    fun applyMatrix(matrix: Matrix4): BufferGeometry {
        this.attributes.position?.also { position ->
            position.applyMatrix4(matrix)
            position.needsUpdate = true
        }

        this.attributes.normal?.also { normal ->
            val normalMatrix = Matrix3().getNormalMatrix(matrix)

            normal.applyMatrix3(normalMatrix)
            normal.needsUpdate = true
        }


        this.attributes.tangent?.also { tangent ->
            val normalMatrix = Matrix3().getNormalMatrix(matrix)

            // Tangent is vec4, but the '.w' component is a sign value (+1/-1).
            tangent.applyMatrix3(normalMatrix)
            tangent.needsUpdate = true
        }

        if (this.boundingBox != null) {
            this.computeBoundingBox()
        }

        if (this.boundingSphere != null) {
            this.computeBoundingSphere()
        }

        return this
    }

    /**
     * Computes bounding box of the geometry, updating Geometry.boundingBox attribute.
     * Bounding boxes aren't computed by default. They need to be explicitly computed, otherwise they are null.
     */
    fun computeBoundingBox() {

        if (boundingBox == null) {
            boundingBox = Box3()
        }

        val bb = boundingBox!!

        val position = this.attributes.position
        if (position != null) {
            position.toBox3(bb)

        } else {
            bb.makeEmpty()
        }

        if (bb.min.x.isNaN() || bb.min.y.isNaN() || bb.min.z.isNaN()) {
            println("BufferGeometry.computeBoundingBox: Computed min/max have NaN values. The 'position' attribute is likely to have NaN values.")
        }

    }

    /**
     * Computes bounding sphere of the geometry, updating Geometry.boundingSphere attribute.
     * Bounding spheres aren't' computed by default. They need to be explicitly computed, otherwise they are null.
     */
    fun computeBoundingSphere() {

        val sphere = boundingSphere ?: Sphere()

        val box = Box3()
//        val boxMorphTargets = Box3()
        val vector = Vector3()

        val position = this.attributes.position
        if (position != null) {

            // first, find the center of the bounding sphere
            val center = sphere.center

            position.toBox3(box)
            box.getCenter(center)

            // second, try to find a boundingSphere with a radius smaller than the
            // boundingSphere of the boundingBox: sqrt(3) smaller in the best case

            var maxRadiusSq = 0.toFloat()
            for (i in 0 until position.count) {
                position.toVector3(i, vector)
                maxRadiusSq = max(maxRadiusSq, center.distanceToSquared(vector))
            }

            sphere.radius = sqrt(maxRadiusSq).toFloat()

            if (sphere.radius.isNaN()) {
                println("THREE.BufferGeometry.computeBoundingSphere(): Computed radius is NaN. The 'position' attribute is likely to have NaN values.")
            }

        }

        boundingSphere = sphere

    }

    /**
     * Computes vertex normals by averaging face normals.
     */
    fun computeVertexNormals() {

        val index = this.index
        val attributes = this.attributes

        if (attributes.position != null) {

            val positions = attributes.position!!

            if (attributes.normal == null) {

                this.addAttribute("normal", FloatBufferAttribute(positions.size, 3))

            } else {

                // reset existing normals to zero
                val array = attributes.normal!!
                for (i in 0 until array.size) {
                    array[i] = 0f
                }

            }

            val normals = attributes.normal!!

            var vA: Int
            var vB: Int
            var vC: Int
            val pA = Vector3()
            val pB = Vector3()
            val pC = Vector3()
            val cb = Vector3()
            val ab = Vector3()

            // indexed elements

            if (index != null) {

                val indices = index

                for (i in 0 until index.count step 3) {

                    vA = indices[i + 0] * 3
                    vB = indices[i + 1] * 3
                    vC = indices[i + 2] * 3

                    positions.toVector3(vA, pA)
                    positions.toVector3(vB, pB)
                    positions.toVector3(vC, pC)

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

                    positions.toVector3(i, pA)
                    positions.toVector3(i + 3, pB)
                    positions.toVector3(i + 6, pC)

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

            attributes.normal!!.needsUpdate = true

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
            }

            val attribute2 = geometry.attributes[key]!!
            val l2 = when (attribute2) {
                is IntBufferAttribute -> attribute2.size
                is FloatBufferAttribute -> attribute2.size
            }

            val attributeOffset = attribute2.itemSize * offset
            val length = min(l2, l1 - attributeOffset)

            var j = attributeOffset
            for (i in 0 until length) {
                when (attribute1) {
                    is IntBufferAttribute -> attribute1[j] = (attribute2 as IntBufferAttribute)[i]
                    is FloatBufferAttribute -> attribute1[j] = (attribute2 as FloatBufferAttribute)[i]
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

    fun copy( source: BufferGeometry ): BufferGeometry {

        // reset

        this.index = null
        this.attributes.clear()
        //TODO        this.morphAttributes = {};
        this.groups.clear()
        this.boundingBox = null
        this.boundingSphere = null

        // name

        this.name = source.name

        // index

        source.index?.also {
            this.setIndex( it.clone() )
        }

        // attributes

        val attributes = source.attributes
        for ( name in attributes.keys ) {

            attributes[ name ]?.also { attribute ->
                this.addAttribute(name, attribute.clone())
            }

        }

        // morph attributes

//        val morphAttributes = source.morphAttributes;
//
//        for ( name in morphAttributes ) {
//
//            var array = [];
//            var morphAttribute = morphAttributes[ name ]; // morphAttribute: array of Float32BufferAttributes
//
//            for ( i = 0, l = morphAttribute.length; i < l; i ++ ) {
//
//                array.push( morphAttribute[ i ].clone() );
//
//            }
//
//            this.morphAttributes[ name ] = array;
//
//        }

        // groups
        source.groups.forEach { group ->

            this.addGroup( group.start, group.count, group.materialIndex )

        }

        val boundingBox = source.boundingBox

        if ( boundingBox != null ) {

            this.boundingBox = boundingBox.clone()

        }

        // bounding sphere

        val boundingSphere = source.boundingSphere

        if ( boundingSphere != null ) {

            this.boundingSphere = boundingSphere.clone()

        }


        // draw range

        this.drawRange.start = source.drawRange.start
        this.drawRange.count = source.drawRange.count

        // user data

        return this

    }

    override fun clone(): BufferGeometry {
        return BufferGeometry().copy(this)
    }

    fun dispose() {
        dispatchEvent("dispose", this)
    }

    private companion object {
        var geometryIdCount = 0 // BufferGeometry uses odd numbers as Id
    }

}
