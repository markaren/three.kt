package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import java.lang.IllegalArgumentException
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2

class Euler(
    x: Float = 0f,
    y: Float = 0f,
    z: Float = 0f,
    order: EulerOrder = EulerOrder.defaultOrder
) : Cloneable {

    var x = x
        set(value) {
            field = value
            onChangeCallback?.invoke()
        }

    var y = y
        set(value) {
            field = value
            onChangeCallback?.invoke()
        }

    var z = z
        set(value) {
            field = value
            onChangeCallback?.invoke()
        }

    var order = order
        set(value) {
            field = value
            onChangeCallback?.invoke()
        }

    internal var onChangeCallback: (() -> Unit)? = null

    fun set(x: Number, y: Number, z: Number, order: EulerOrder?): Euler {
        this.x = x.toFloat()
        this.y = x.toFloat()
        this.z = x.toFloat()
        this.order = order ?: this.order
        this.onChangeCallback?.invoke()
        return this
    }

    override fun clone(): Euler {
        return Euler().copy(this)
    }

    fun copy(euler: Euler): Euler {
        return set(euler.x, euler.y, euler.z, euler.order)
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
        val order = order ?: this.order

        if (order == EulerOrder.XYZ) {

            this.y = asin(clamp(m13, -1f, 1f));

            if (abs(m13) < 0.99999) {

                this.x = atan2(-m23, m33);
                this.z = atan2(-m12, m11);

            } else {

                this.x = atan2(m32, m22);
                this.z = 0f

            }

        } else if (order == EulerOrder.YXZ) {

            this.x = asin(-clamp(m23, -1, 1));

            if (abs(m23) < 0.99999) {

                this.y = atan2(m13, m33);
                this.z = atan2(m21, m22);

            } else {

                this.y = atan2(-m31, m11);
                this.z = 0f

            }

        } else if (order == EulerOrder.ZXY) {

            this.x = asin(clamp(m32, -1, 1));

            if (abs(m32) < 0.99999) {

                this.y = atan2(-m31, m33);
                this.z = atan2(-m12, m22);

            } else {

                this.y = 0f
                this.z = atan2(m21, m11);

            }

        } else if (order == EulerOrder.ZYX) {

            this.y = asin(-clamp(m31, -1, 1));

            if (abs(m31) < 0.99999) {

                this.x = atan2(m32, m33);
                this.z = atan2(m21, m11);

            } else {

                this.x = 0f
                this.z = atan2(-m12, m22);

            }

        } else if (order == EulerOrder.YZX) {

            this.z = asin(clamp(m21, -1, 1));

            if (abs(m21) < 0.99999) {

                this.x = atan2(-m23, m22);
                this.y = atan2(-m31, m11);

            } else {

                this.x = 0f
                this.y = atan2(m13, m33);

            }

        } else if (order == EulerOrder.XZY) {

            this.z = asin(-clamp(m12, -1, 1));

            if (abs(m12) < 0.99999) {

                this.x = atan2(m32, m22);
                this.y = atan2(m13, m11);

            } else {

                this.x = atan2(-m23, m33);
                this.y = 0f

            }

        } else {

            throw IllegalArgumentException("unsupported order: $order");

        }

        this.order = order;

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
        return this.set(v.x, v.y, v.z, order ?: this.order);
    }

    fun reorder(newOrder: EulerOrder): Euler {
        // WARNING: this discards revolution information -bhouston

        val q = Quaternion()

        q.setFromEuler(this)

        return this.setFromQuaternion(q, newOrder)
    }

    fun toVector3(optionalResult: Vector3 = Vector3()): Vector3 {
        return optionalResult.set(x, y, z)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Euler

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false
        if (order != other.order) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        result = 31 * result + order.hashCode()
        return result
    }

    override fun toString(): String {
        return "Euler(x=$x, y=$y, z=$z, order=$order)"
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
