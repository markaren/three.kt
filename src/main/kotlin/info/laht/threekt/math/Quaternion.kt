package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import kotlin.math.*

class Quaternion(
    internal var x: Float = 0f,
    internal var y: Float = 0f,
    internal var z: Float = 0f,
    internal var w: Float = 1f
): Cloneable {

    internal var onChangeCallback: (() -> Unit)? = null

    /**
     * Sets values of this quaternion.
     */
    fun set(x: Number, y: Number, z: Number, w: Number): Quaternion {
        this.x = x.toFloat()
        this.y = y.toFloat()
        this.z = z.toFloat()
        this.w = w.toFloat()
        this.onChangeCallback?.invoke()
        return this
    }

    /**
     * Clones this quaternion.
     */
    override fun clone(): Quaternion {
        return Quaternion(x, y, z, w)
    }

    /**
     * Copies values of q to this quaternion.
     */
    fun copy(q: Quaternion): Quaternion {
        return set(q.x, q.y, q.z, q.w)
    }

    /**
     * Sets this quaternion from rotation specified by Euler angles.
     */
    @JvmOverloads
    fun setFromEuler(euler: Euler, update: Boolean = true): Quaternion {
        val x = euler.x
        val y = euler.y
        val z = euler.z

        // http://www.mathworks.com/matlabcentral/fileexchange/
        // 	20696-function-to-convert-between-dcm-euler-angles-quaternions-and-euler-vectors/
        //	content/SpinCalc.m

        val c1 = cos(x / 2);
        val c2 = cos(y / 2);
        val c3 = cos(z / 2);

        val s1 = sin(x / 2);
        val s2 = sin(y / 2);
        val s3 = sin(z / 2);

        if (euler.order == EulerOrder.XYZ) {

            this.x = s1 * c2 * c3 + c1 * s2 * s3;
            this.y = c1 * s2 * c3 - s1 * c2 * s3;
            this.z = c1 * c2 * s3 + s1 * s2 * c3;
            this.w = c1 * c2 * c3 - s1 * s2 * s3;

        } else if (euler.order == EulerOrder.YXZ) {

            this.x = s1 * c2 * c3 + c1 * s2 * s3;
            this.y = c1 * s2 * c3 - s1 * c2 * s3;
            this.z = c1 * c2 * s3 - s1 * s2 * c3;
            this.w = c1 * c2 * c3 + s1 * s2 * s3;

        } else if (euler.order == EulerOrder.ZXY) {

            this.x = s1 * c2 * c3 - c1 * s2 * s3;
            this.y = c1 * s2 * c3 + s1 * c2 * s3;
            this.z = c1 * c2 * s3 + s1 * s2 * c3;
            this.w = c1 * c2 * c3 - s1 * s2 * s3;

        } else if (euler.order == EulerOrder.ZYX) {

            this.x = s1 * c2 * c3 - c1 * s2 * s3;
            this.y = c1 * s2 * c3 + s1 * c2 * s3;
            this.z = c1 * c2 * s3 - s1 * s2 * c3;
            this.w = c1 * c2 * c3 + s1 * s2 * s3;

        } else if (euler.order == EulerOrder.YZX) {

            this.x = s1 * c2 * c3 + c1 * s2 * s3;
            this.y = c1 * s2 * c3 + s1 * c2 * s3;
            this.z = c1 * c2 * s3 - s1 * s2 * c3;
            this.w = c1 * c2 * c3 - s1 * s2 * s3;

        } else if (euler.order == EulerOrder.XZY) {

            this.x = s1 * c2 * c3 - c1 * s2 * s3;
            this.y = c1 * s2 * c3 - s1 * c2 * s3;
            this.z = c1 * c2 * s3 + s1 * s2 * c3;
            this.w = c1 * c2 * c3 + s1 * s2 * s3;

        }

        if (update) {
            this.onChangeCallback?.invoke()
        }

        return this;
    }

    fun setFromAxisAngle(axisX: Float, axisY: Float, axisZ: Float, angle: Float): Quaternion {
        val halfAngle = angle / 2
        val s = sin(halfAngle);

        this.x = axisX * s;
        this.y = axisY * s;
        this.z = axisZ * s;
        this.w = cos(halfAngle);

        this.onChangeCallback?.invoke()

        return this;
    }

    /**
     * Sets this quaternion from rotation specified by axis and angle.
     * Adapted from http://www.euclideanspace.com/maths/geometry/rotations/conversions/angleToQuaternion/index.htm.
     * Axis have to be normalized, angle is in radians.
     */
    fun setFromAxisAngle(axis: Vector3, angle: Float): Quaternion {
        return this.setFromAxisAngle(axis.x, axis.y, axis.z, angle)
    }

    /**
     * Sets this quaternion from rotation component of m. Adapted from http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/index.htm.
     */
    fun setFromRotationMatrix(m: Matrix4): Quaternion {
        // http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/index.htm

        // assumes the upper 3x3 of m is a pure rotation matrix (i.e, unscaled)

        val te = m.elements

        val m11 = te[0];
        val m12 = te[4];
        val m13 = te[8]
        val m21 = te[1];
        val m22 = te[5];
        val m23 = te[9]
        val m31 = te[2];
        val m32 = te[6];
        val m33 = te[10]

        val trace = m11 + m22 + m33
        val s: Float

        if (trace > 0) {

            s = 0.5.toFloat() / sqrt(trace + 1.toFloat());

            this.w = 0.25.toFloat() / s;
            this.x = (m32 - m23) * s;
            this.y = (m13 - m31) * s;
            this.z = (m21 - m12) * s;

        } else if (m11 > m22 && m11 > m33) {

            s = 2.toFloat() * sqrt(1.toFloat() + m11 - m22 - m33);

            this.w = (m32 - m23) / s;
            this.x = 0.25.toFloat() * s;
            this.y = (m12 + m21) / s;
            this.z = (m13 + m31) / s;

        } else if (m22 > m33) {

            s = 2.toFloat() * sqrt(1.toFloat() + m22 - m11 - m33);

            this.w = (m13 - m31) / s;
            this.x = (m12 + m21) / s;
            this.y = 0.25.toFloat() * s;
            this.z = (m23 + m32) / s;

        } else {

            s = 2.toFloat() * sqrt(1.toFloat() + m33 - m11 - m22);

            this.w = (m21 - m12) / s;
            this.x = (m13 + m31) / s;
            this.y = (m23 + m32) / s;
            this.z = 0.25.toFloat() * s;

        }

        this.onChangeCallback?.invoke()

        return this;
    }

    fun setFromUnitVectors(vFrom: Vector3, vTo: Vector3): Quaternion {
        // assumes direction vectors vFrom and vTo are normalized

        val EPS = 0.000001.toFloat()

        var r = vFrom.dot(vTo) + 1

        if (r < EPS) {

            r = 0.toFloat()

            if (abs(vFrom.x) > abs(vFrom.z)) {

                this.x = -vFrom.y
                this.y = vFrom.x
                this.z = 0.toFloat()
                this.w = r

            } else {

                this.x = 0.toFloat()
                this.y = -vFrom.z
                this.z = vFrom.y
                this.w = r

            }

        } else {

            // crossVectors( vFrom, vTo ); // inlined to avoid cyclic dependency on Vector3

            this.x = vFrom.y * vTo.z - vFrom.z * vTo.y
            this.y = vFrom.z * vTo.x - vFrom.x * vTo.z
            this.z = vFrom.x * vTo.y - vFrom.y * vTo.x
            this.w = r

        }

        return this.normalize()
    }

    fun angleTo(q: Quaternion): Float {
        return 2 * acos(abs(clamp(this.dot(q), -1, 1)));
    }

    fun rotateTowards(q: Quaternion, step: Float): Quaternion {
        val angle = this.angleTo(q);

        if (angle == 0.toFloat()) return this;

        val t = min(1.toFloat(), step / angle);

        this.slerp(q, t);

        return this;
    }

    /**
     * Inverts this quaternion.
     */
    fun inverse(): Quaternion {
        return this.conjugate();
    }

    fun conjugate(): Quaternion {
        this.x *= -1
        this.y *= -1
        this.z *= -1

        this.onChangeCallback?.invoke()

        return this;
    }

    fun dot(v: Quaternion): Float {
        return this.x * v.x + this.y * v.y + this.z * v.z + this.w * v.w
    }

    fun lengthSq(): Float {
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w
    }

    /**
     * Computes length of this quaternion.
     */
    fun length(): Float {
        return sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w)

    }

    /**
     * Normalizes this quaternion.
     */
    fun normalize(): Quaternion {
        var l = this.length();

        if (l == 0.toFloat()) {

            this.x = 0.toFloat()
            this.y = 0.toFloat()
            this.z = 0.toFloat()
            this.w = 1.toFloat()

        } else {

            l = 1.toFloat() / l

            this.x = this.x * l
            this.y = this.y * l
            this.z = this.z * l
            this.w = this.w * l

        }

        this.onChangeCallback?.invoke()

        return this;
    }

    /**
     * Multiplies this quaternion by b.
     */
    fun multiply(q: Quaternion): Quaternion {
        return this.multiplyQuaternions(this, q);
    }

    fun premultiply(q: Quaternion): Quaternion {
        return this.multiplyQuaternions(q, this);
    }

    /**
     * Sets this quaternion to a x b
     * Adapted from http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/code/index.htm.
     */
    fun multiplyQuaternions(a: Quaternion, b: Quaternion): Quaternion {
        val qax = a.x;
        val qay = a.y;
        val qaz = a.z;
        val qaw = a.w;
        val qbx = b.x;
        val qby = b.y;
        val qbz = b.z;
        val qbw = b.w;

        this.x = qax * qbw + qaw * qbx + qay * qbz - qaz * qby;
        this.y = qay * qbw + qaw * qby + qaz * qbx - qax * qbz;
        this.z = qaz * qbw + qaw * qbz + qax * qby - qay * qbx;
        this.w = qaw * qbw - qax * qbx - qay * qby - qaz * qbz;

        this.onChangeCallback?.invoke()

        return this;
    }

    fun slerp(qb: Quaternion, t: Float): Quaternion {
        if (t == 0.toFloat()) {
            return this
        }
        if (t == 1.toFloat()) {
            return this.copy(qb)
        }

        val x = this.x;
        val y = this.y;
        val z = this.z;
        val w = this.w

        // http://www.euclideanspace.com/maths/algebra/realNormedAlgebra/quaternions/slerp/

        var cosHalfTheta = w * qb.w + x * qb.x + y * qb.y + z * qb.z

        if (cosHalfTheta < 0) {

            this.w = -qb.w
            this.x = -qb.x
            this.y = -qb.y
            this.z = -qb.z

            cosHalfTheta = -cosHalfTheta

        } else {

            this.copy(qb)

        }

        if (cosHalfTheta >= 1.0) {

            this.w = w
            this.x = x
            this.y = y
            this.z = z

            return this;

        }

        val sqrSinHalfTheta = 1.0 - cosHalfTheta * cosHalfTheta

        if (sqrSinHalfTheta <= 1e-6) {

            val s = 1 - t
            this.w = s * w + t * this.w
            this.x = s * x + t * this.x
            this.y = s * y + t * this.y
            this.z = s * z + t * this.z

            this.normalize();
            this.onChangeCallback?.invoke()

            return this;

        }

        val sinHalfTheta = sqrt(sqrSinHalfTheta).toFloat()
        val halfTheta = atan2(sinHalfTheta, cosHalfTheta)
        val ratioA = sin((1 - t) * halfTheta) / sinHalfTheta
        val ratioB = sin(t * halfTheta) / sinHalfTheta;

        this.w = (w * ratioA + this.w * ratioB);
        this.x = (x * ratioA + this.x * ratioB);
        this.y = (y * ratioA + this.y * ratioB);
        this.z = (z * ratioA + this.z * ratioB);

        this.onChangeCallback?.invoke()

        return this;
    }


    @JvmOverloads
    fun fromArray(array: FloatArray, offset: Int = 0): Quaternion {
        x = array[offset + 0]
        y = array[offset + 1]
        z = array[offset + 2]
        w = array[offset + 3]
        return this
    }

    @JvmOverloads
    fun toArray(array: FloatArray = FloatArray(4), offset: Int = 0): FloatArray {
        array[offset + 0] = x
        array[offset + 1] = y
        array[offset + 2] = z
        array[offset + 3] = w
        return array
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Quaternion

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false
        if (w != other.w) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        result = 31 * result + w.hashCode()
        return result
    }

    override fun toString(): String {
        return "Quaternion(x=$x, y=$y, z=$z, w=$w)"
    }

}
