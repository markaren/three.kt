package info.laht.threekt.math

import info.laht.threekt.core.BufferAttribute
import java.lang.IllegalStateException

class Matrix3(
    val elements: DoubleArray = DoubleArray(9)
) {

    fun set(
        n11: Double, n12: Double, n13: Double,
        n21: Double, n22: Double, n23: Double,
        n31: Double, n32: Double, n33: Double
    ): Matrix3 {
        val te = this.elements;

        te[0] = n11; te[1] = n21; te[2] = n31;
        te[3] = n12; te[4] = n22; te[5] = n32;
        te[6] = n13; te[7] = n23; te[8] = n33;

        return this;
    }

    fun identity(): Matrix3 {
        return set(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0,
            0.0, 0.0, 1.0
        )
    }

    fun clone(): Matrix3 {
        return Matrix3().copy(this)
    }

    fun copy(m: Matrix3): Matrix3 {
        var te = this.elements;
        var me = m.elements;

        te[0] = me[0]; te[1] = me[1]; te[2] = me[2];
        te[3] = me[3]; te[4] = me[4]; te[5] = me[5];
        te[6] = me[6]; te[7] = me[7]; te[8] = me[8];

        return this;
    }

    fun setFromMatrix4(m: Matrix4): Matrix3 {
        var me = m.elements;

        this.set(

            me[0], me[4], me[8],
            me[1], me[5], me[9],
            me[2], me[6], me[10]

        );

        return this;
    }

    fun applyToBufferAttribute(attribute: BufferAttribute): BufferAttribute {
        TODO()
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

        val a11 = ae[0];
        val a12 = ae[3];
        val a13 = ae[6]
        val a21 = ae[1];
        val a22 = ae[4];
        val a23 = ae[7]
        val a31 = ae[2];
        val a32 = ae[5];
        val a33 = ae[8]

        val b11 = be[0];
        val b12 = be[3];
        val b13 = be[6]
        val b21 = be[1];
        val b22 = be[4];
        val b23 = be[7]
        val b31 = be[2];
        val b32 = be[5];
        val b33 = be[8]

        te[0] = a11 * b11 + a12 * b21 + a13 * b31;
        te[3] = a11 * b12 + a12 * b22 + a13 * b32;
        te[6] = a11 * b13 + a12 * b23 + a13 * b33;

        te[1] = a21 * b11 + a22 * b21 + a23 * b31;
        te[4] = a21 * b12 + a22 * b22 + a23 * b32;
        te[7] = a21 * b13 + a22 * b23 + a23 * b33;

        te[2] = a31 * b11 + a32 * b21 + a33 * b31;
        te[5] = a31 * b12 + a32 * b22 + a33 * b32;
        te[8] = a31 * b13 + a32 * b23 + a33 * b33;

        return this;
    }

    fun multiplyScalar(s: Double): Matrix3 {
        var te = this.elements;

        te[0] *= s; te[3] *= s; te[6] *= s;
        te[1] *= s; te[4] *= s; te[7] *= s;
        te[2] *= s; te[5] *= s; te[8] *= s;

        return this;
    }

    fun determinant(): Double {
        TODO()
    }

    @JvmOverloads
    fun getInverse(matrix: Matrix3, throwOnDegenerate: Boolean = false): Matrix3 {
        var me = matrix.elements
        val te = this.elements

        val n11 = me[0];
        val n21 = me[1];
        val n31 = me[2]
        val n12 = me[3];
        val n22 = me[4];
        val n32 = me[5]
        val n13 = me[6];
        val n23 = me[7];
        val n33 = me[8]

        val t11 = n33 * n22 - n32 * n23
        val t12 = n32 * n13 - n33 * n12
        val t13 = n23 * n12 - n22 * n13

        val det = n11 * t11 + n21 * t12 + n31 * t13;

        if (det == 0.0) {

            val msg = "getInverse() can't invert matrix, determinant is 0";

            if (throwOnDegenerate) {

                throw IllegalStateException(msg)

            } else {

                println(msg);

            }

            return this.identity()

        }

        val detInv = 1.0 / det

        te[0] = t11 * detInv
        te[1] = (n31 * n23 - n33 * n21) * detInv
        te[2] = (n32 * n21 - n31 * n22) * detInv

        te[3] = t12 * detInv;
        te[4] = (n33 * n11 - n31 * n13) * detInv
        te[5] = (n31 * n12 - n32 * n11) * detInv

        te[6] = t13 * detInv
        te[7] = (n21 * n13 - n23 * n11) * detInv
        te[8] = (n22 * n11 - n21 * n12) * detInv

        return this;
    }

    /**
     * Transposes this matrix in place.
     */
    fun transpose(): Matrix3 {
        var tmp: Double
        val m = this.elements

        tmp = m[ 1 ]; m[ 1 ] = m[ 3 ]; m[ 3 ] = tmp
        tmp = m[ 2 ]; m[ 2 ] = m[ 6 ]; m[ 6 ] = tmp
        tmp = m[ 5 ]; m[ 5 ] = m[ 7 ]; m[ 7 ] = tmp

        return this
    }

    fun getNormalMatrix(matrix4: Matrix4): Matrix3 {
        return this.setFromMatrix4( matrix4 ).getInverse( this ).transpose();
    }

    /**
     * Transposes this matrix into the supplied array r, and returns itself.
     */
    fun transposeIntoArray(r: DoubleArray): Matrix3 {
        val m = this.elements;

        r[ 0 ] = m[ 0 ];
        r[ 1 ] = m[ 3 ];
        r[ 2 ] = m[ 6 ];
        r[ 3 ] = m[ 1 ];
        r[ 4 ] = m[ 4 ];
        r[ 5 ] = m[ 7 ];
        r[ 6 ] = m[ 2 ];
        r[ 7 ] = m[ 5 ];
        r[ 8 ] = m[ 8 ];

        return this;
    }

    @JvmOverloads
    fun fromArray(array: DoubleArray, offset: Int = 0): Matrix3 {
        array.copyInto(elements, offset)
        return this
    }

    fun toArray(): DoubleArray {
        return elements.clone()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix3

        if (!elements.contentEquals(other.elements)) return false

        return true
    }

    override fun hashCode(): Int {
        return elements.contentHashCode()
    }

}
