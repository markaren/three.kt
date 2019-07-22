package info.laht.threekt.core

import info.laht.threekt.math.*

abstract class GeometryBase<E> : EventDispatcher(), Cloneable {

    var name = ""
    val uuid = generateUUID()

    var boundingBox: Box3? = null
        protected set

    var boundingSphere: Sphere? = null
        protected set

    abstract fun applyMatrix(matrix: Matrix4): E

    fun rotateX(angle: Float): E {

        // rotate geometry around world x-axis
        val m1 = Matrix4()
        m1.makeRotationX(angle)
        this.applyMatrix(m1)

        return this as E

    }

    fun rotateY(angle: Float): E {

        // rotate geometry around world y-axis
        val m1 = Matrix4()
        m1.makeRotationY(angle)
        this.applyMatrix(m1)

        return this as E

    }

    fun rotateZ(angle: Float): E {

        // rotate geometry around world z-axis
        val m1 = Matrix4()
        m1.makeRotationZ(angle)
        this.applyMatrix(m1)

        return this as E

    }

    fun translate(x: Float, y: Float, z: Float): E {

        // translate geometry
        val m1 = Matrix4()
        m1.makeTranslation(x, y, z)
        this.applyMatrix(m1)

        return this as E

    }

    fun scale(x: Float, y: Float, z: Float): E {

        // scale geometry
        val m1 = Matrix4()
        m1.makeScale(x, y, z)
        this.applyMatrix(m1)

        return this as E

    }

    fun lookAt(vector: Vector3): E {
        val obj = Object3D()
        obj.lookAt(vector)
        obj.updateMatrix()
        this.applyMatrix(obj.matrix)

        return this as E
    }

    fun center(): E {
        val offset = Vector3()

        this.computeBoundingBox()
        boundingBox!!.getCenter(offset).negate()

        this.translate(offset.x, offset.y, offset.z)

        return this as E

    }

    fun normalize(): E {
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

        return this as E
    }


    abstract fun computeBoundingBox()

    abstract fun computeBoundingSphere()

}
