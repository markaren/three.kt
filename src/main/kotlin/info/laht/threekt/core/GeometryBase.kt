package info.laht.threekt.core

import info.laht.threekt.math.*

abstract class GeometryBase<E>: EventDispatcher() {

    var name = ""
    val uuid = generateUUID()

    var boundingBox: Box3? = null
        protected set
    var boundingSphere: Sphere? = null
        protected set

    abstract fun applyMatrix(matrix: Matrix4): E

    fun rotateX(angle: Double): E {

        // rotate geometry around world x-axis
        val m1 = Matrix4()
        m1.makeRotationX(angle)
        this.applyMatrix(m1)

        return this as E

    }

    fun rotateY(angle: Double): E {

        // rotate geometry around world y-axis
        val m1 = Matrix4()
        m1.makeRotationY(angle)
        this.applyMatrix(m1)

        return this as E

    }

    fun rotateZ(angle: Double): E {

        // rotate geometry around world z-axis
        val m1 = Matrix4()
        m1.makeRotationZ(angle)
        this.applyMatrix(m1)

        return this as E

    }

    fun translate(x: Double, y: Double, z: Double): E {

        // translate geometry
        val m1 = Matrix4()
        m1.makeTranslation(x, y, z)
        this.applyMatrix(m1)

        return this as E

    }

    fun scale(x: Double, y: Double, z: Double): E {

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

        this.computeBoundingBox();
        this.boundingBox!!.getCenter(offset).negate()

        this.translate(offset.x, offset.y, offset.z)

        return this as E

    }

    fun normalize(): E {
        this.computeBoundingSphere()

        val center = this.boundingSphere!!.center
        val radius = this.boundingSphere!!.radius

        val s = if (radius == 0.0) 1.0 else 1.0 / radius

        val matrix = Matrix4()
        matrix.set(
            s, 0.0, 0.0, -s * center.x,
            0.0, s, 0.0, -s * center.y,
            0.0, 0.0, s, -s * center.z,
            0.0, 0.0, 0.0, 1.0
        );

        this.applyMatrix(matrix)

        return this as E
    }


    abstract fun computeBoundingBox()

    abstract fun computeBoundingSphere()


}