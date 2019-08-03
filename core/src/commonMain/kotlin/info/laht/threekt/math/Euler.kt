package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2

data class Euler(
    private var _x: Float = 0f,
    private var _y: Float = 0f,
    private var _z: Float = 0f,
    private var _order: EulerOrder = EulerOrder.defaultOrder
) : Cloneable {

    var x: Float
        get() =  _x
        set(value) {
            _x = value
            onChangeCallback?.invoke()
        }

    var y: Float
        get() = _y
        set(value) {
            _y = value
            onChangeCallback?.invoke()
        }

    var z: Float
        get() = _z
        set(value) {
            _z = value
            onChangeCallback?.invoke()
        }

    var order = _order
        set(value) {
            field = value
            onChangeCallback?.invoke()
        }

    internal var onChangeCallback: (() -> Unit)? = null

    fun set(x: Number, y: Number, z: Number, order: EulerOrder?): Euler {
        this._x = x.toFloat()
        this._y = y.toFloat()
        this._z = z.toFloat()
        this._order = order ?: this._order
        this.onChangeCallback?.invoke()
        return this
    }

    override fun clone() = copy()

    fun copy(euler: Euler): Euler {
        return set(euler._x, euler._y, euler._z, euler._order)
    }

    fun setFromRotationMatrix(m: Matrix4, order: EulerOrder? = null, update: Boolean = true): Euler {
        // assumes the upper 3x3 of m is a pure rotation matrix (i.e, unscaled)

        val te = m.elements
        val m11 = te[0]
        val m12 = te[4]
        val m13 = te[8]
        val m21 = te[1]
        val m22 = te[5]
        val m23 = te[9]
        val m31 = te[2]
        val m32 = te[6]
        val m33 = te[10]

        @Suppress("NAME_SHADOWING")
        val order = order ?: this._order

        if (order == EulerOrder.XYZ) {

            this._y = asin(clamp(m13, -1f, 1f))

            if (abs(m13) < 0.99999) {

                this._x = atan2(-m23, m33)
                this._z = atan2(-m12, m11)

            } else {

                this._x = atan2(m32, m22)
                this._z = 0f

            }

        } else if (order == EulerOrder.YXZ) {

            this._x = asin(-clamp(m23, -1, 1))

            if (abs(m23) < 0.99999) {

                this._y = atan2(m13, m33)
                this._z = atan2(m21, m22)

            } else {

                this._y = atan2(-m31, m11)
                this._z = 0f

            }

        } else if (order == EulerOrder.ZXY) {

            this._x = asin(clamp(m32, -1, 1))

            if (abs(m32) < 0.99999) {

                this._y = atan2(-m31, m33)
                this._z = atan2(-m12, m22)

            } else {

                this._y = 0f
                this._z = atan2(m21, m11)

            }

        } else if (order == EulerOrder.ZYX) {

            this._y = asin(-clamp(m31, -1, 1))

            if (abs(m31) < 0.99999) {

                this._x = atan2(m32, m33)
                this._z = atan2(m21, m11)

            } else {

                this._x = 0f
                this._z = atan2(-m12, m22)

            }

        } else if (order == EulerOrder.YZX) {

            this._z = asin(clamp(m21, -1, 1))

            if (abs(m21) < 0.99999) {

                this._x = atan2(-m23, m22)
                this._y = atan2(-m31, m11)

            } else {

                this._x = 0f
                this._y = atan2(m13, m33)

            }

        } else if (order == EulerOrder.XZY) {

            this._z = asin(-clamp(m12, -1, 1))

            if (abs(m12) < 0.99999) {

                this._x = atan2(m32, m22)
                this._y = atan2(m13, m11)

            } else {

                this._x = atan2(-m23, m33)
                this._y = 0f

            }

        } else {

            throw IllegalArgumentException("unsupported order: $order")

        }

        this._order = order

        if (update) {
            this.onChangeCallback?.invoke()
        }

        return this
    }

    fun setFromQuaternion(q: Quaternion, order: EulerOrder? = null, update: Boolean = true): Euler {
        val matrix = Matrix4()
        matrix.makeRotationFromQuaternion(q)

        return this.setFromRotationMatrix(matrix, order, update)
    }

    fun setFromVector3(v: Vector3, order: EulerOrder? = null): Euler {
        return this.set(v.x, v.y, v.z, order ?: this._order)
    }

    fun reorder(newOrder: EulerOrder): Euler {
        // WARNING: this discards revolution information -bhouston

        val q = Quaternion()

        q.setFromEuler(this)

        return this.setFromQuaternion(q, newOrder)
    }

    fun toVector3(optionalResult: Vector3 = Vector3()): Vector3 {
        return optionalResult.set(_x, _y, _z)
    }

}

enum class EulerOrder {
    XYZ,
    YZX,
    ZXY,
    XZY,
    YXZ,
    ZYX;

    companion object {

        val defaultOrder: EulerOrder = XYZ

    }

}
