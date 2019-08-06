package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import kotlin.jvm.JvmOverloads
import kotlin.math.cos
import kotlin.math.sin

class Matrix3(
    val elements: FloatArray = floatArrayOf(
        1f, 0f, 0f,
        0f, 1f, 0f,
        0f, 0f, 1f
    )
) : Cloneable, Flattable {

    override val size = 9

    fun set(
        n11: Float, n12: Float, n13: Float,
        n21: Float, n22: Float, n23: Float,
        n31: Float, n32: Float, n33: Float
    ): Matrix3 {
        val te = this.elements

        te[0] = n11; te[1] = n21; te[2] = n31
        te[3] = n12; te[4] = n22; te[5] = n32
        te[6] = n13; te[7] = n23; te[8] = n33

        return this
    }

    fun identity(): Matrix3 {
        return set(
            1f, 0f, 0f,
            0f, 1f, 0f,
            0f, 0f, 1f
        )
    }

    override fun clone(): Matrix3 {
        return Matrix3().copy(this)
    }

    fun copy(m: Matrix3): Matrix3 {
        val te = this.elements
        val me = m.elements

        te[0] = me[0]; te[1] = me[1]; te[2] = me[2]
        te[3] = me[3]; te[4] = me[4]; te[5] = me[5]
        te[6] = me[6]; te[7] = me[7]; te[8] = me[8]

        return this
    }

    fun setFromMatrix4(m: Matrix4): Matrix3 {
        val me = m.elements

        this.set(

            me[0], me[4], me[8],
            me[1], me[5], me[9],
            me[2], me[6], me[10]

        )

        return this
    }

    /**
     * Multiplies this matrix by m.
     */
    fun multiply(m: Matrix3): Matrix3 {
        return this.multiplyMatrices(this, m)
    }

    fun premultiply(m: Matrix3): Matrix3 {
        return this.multiplyMatrices(m, this)
    }

    /**
     * Sets this matrix to a x b.
     */
    fun multiplyMatrices(a: Matrix3, b: Matrix3): Matrix3 {
        val ae = a.elements
        val be = b.elements
        val te = this.elements

        val a11 = ae[0]
        val a12 = ae[3]
        val a13 = ae[6]
        val a21 = ae[1]
        val a22 = ae[4]
        val a23 = ae[7]
        val a31 = ae[2]
        val a32 = ae[5]
        val a33 = ae[8]

        val b11 = be[0]
        val b12 = be[3]
        val b13 = be[6]
        val b21 = be[1]
        val b22 = be[4]
        val b23 = be[7]
        val b31 = be[2]
        val b32 = be[5]
        val b33 = be[8]

        te[0] = a11 * b11 + a12 * b21 + a13 * b31
        te[3] = a11 * b12 + a12 * b22 + a13 * b32
        te[6] = a11 * b13 + a12 * b23 + a13 * b33

        te[1] = a21 * b11 + a22 * b21 + a23 * b31
        te[4] = a21 * b12 + a22 * b22 + a23 * b32
        te[7] = a21 * b13 + a22 * b23 + a23 * b33

        te[2] = a31 * b11 + a32 * b21 + a33 * b31
        te[5] = a31 * b12 + a32 * b22 + a33 * b32
        te[8] = a31 * b13 + a32 * b23 + a33 * b33

        return this
    }

    fun multiplyScalar(s: Float): Matrix3 {
        val te = this.elements

        te[0] *= s; te[3] *= s; te[6] *= s
        te[1] *= s; te[4] *= s; te[7] *= s
        te[2] *= s; te[5] *= s; te[8] *= s

        return this
    }

    fun determinant(): Float {
        val te = this.elements

        val a = te[0]
        val b = te[1]
        val c = te[2]
        val d = te[3]
        val e = te[4]
        val f = te[5]
        val g = te[6]
        val h = te[7]
        val i = te[8]

        return a * e * i - a * f * h - b * d * i + b * f * g + c * d * h - c * e * g
    }

    @JvmOverloads
    fun getInverse(matrix: Matrix3, throwOnDegenerate: Boolean = false): Matrix3 {
        val me = matrix.elements
        val te = this.elements

        val n11 = me[0]
        val n21 = me[1]
        val n31 = me[2]
        val n12 = me[3]
        val n22 = me[4]
        val n32 = me[5]
        val n13 = me[6]
        val n23 = me[7]
        val n33 = me[8]

        val t11 = n33 * n22 - n32 * n23
        val t12 = n32 * n13 - n33 * n12
        val t13 = n23 * n12 - n22 * n13

        val det = n11 * t11 + n21 * t12 + n31 * t13

        if (det == 0f) {

            val msg = "Matrix3.getInverse() can't invert matrix, determinant is 0!"
            if (throwOnDegenerate) {
                throw IllegalStateException(msg)
            } else {
                println(msg)
            }

            return this.identity()

        }

        val detInv = 1f / det

        te[0] = t11 * detInv
        te[1] = (n31 * n23 - n33 * n21) * detInv
        te[2] = (n32 * n21 - n31 * n22) * detInv

        te[3] = t12 * detInv
        te[4] = (n33 * n11 - n31 * n13) * detInv
        te[5] = (n31 * n12 - n32 * n11) * detInv

        te[6] = t13 * detInv
        te[7] = (n21 * n13 - n23 * n11) * detInv
        te[8] = (n22 * n11 - n21 * n12) * detInv

        return this
    }

    /**
     * Transposes this matrix in place.
     */
    fun transpose(): Matrix3 {
        var tmp: Float
        val m = this.elements

        tmp = m[1]; m[1] = m[3]; m[3] = tmp
        tmp = m[2]; m[2] = m[6]; m[6] = tmp
        tmp = m[5]; m[5] = m[7]; m[7] = tmp

        return this
    }

    fun getNormalMatrix(matrix4: Matrix4): Matrix3 {
        return this.setFromMatrix4(matrix4).getInverse(this).transpose()
    }

    /**
     * Transposes this matrix into the supplied array r, and returns itself.
     */
    fun transposeIntoArray(r: FloatArray): Matrix3 {
        val m = this.elements

        r[0] = m[0]
        r[1] = m[3]
        r[2] = m[6]
        r[3] = m[1]
        r[4] = m[4]
        r[5] = m[7]
        r[6] = m[2]
        r[7] = m[5]
        r[8] = m[8]

        return this
    }

    fun setUvTransform(tx: Float, ty: Float, sx: Float, sy: Float, rotation: Float, cx: Float, cy: Float) {

        val c = cos(rotation)
        val s = sin(rotation)

        this.set(
            sx * c, sx * s, -sx * (c * cx + s * cy) + cx + tx,
            -sy * s, sy * c, -sy * (-s * cx + c * cy) + cy + ty,
            0f, 0f, 1f
        )

    }

    fun scale(sx: Float, sy: Float): Matrix3 {

        val te = this.elements

        te[0] *= sx; te[3] *= sx; te[6] *= sx
        te[1] *= sy; te[4] *= sy; te[7] *= sy

        return this

    }

    fun rotate(theta: Float): Matrix3 {

        val c = cos(theta)
        val s = sin(theta)

        val te = this.elements

        val a11 = te[0]
        val a12 = te[3]
        val a13 = te[6]
        val a21 = te[1]
        val a22 = te[4]
        val a23 = te[7]

        te[0] = c * a11 + s * a21
        te[3] = c * a12 + s * a22
        te[6] = c * a13 + s * a23

        te[1] = -s * a11 + c * a21
        te[4] = -s * a12 + c * a22
        te[7] = -s * a13 + c * a23

        return this

    }

    fun translate(tx: Float, ty: Float): Matrix3 {

        val te = this.elements

        te[0] += tx * te[2]; te[3] += tx * te[5]; te[6] += tx * te[8]
        te[1] += ty * te[2]; te[4] += ty * te[5]; te[7] += ty * te[8]

        return this

    }

    @JvmOverloads
    fun fromArray(array: FloatArray, offset: Int = 0): Matrix3 {
        array.copyInto(elements, offset)
        return this
    }

    override fun toArray(array: FloatArray?, offset: Int): FloatArray {
        return elements.copyInto(array ?: FloatArray(9), offset)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Matrix3

        if (!elements.contentEquals(other.elements)) return false

        return true
    }

    override fun hashCode(): Int {
        return elements.contentHashCode()
    }

}
