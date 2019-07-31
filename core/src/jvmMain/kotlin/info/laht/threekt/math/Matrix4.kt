package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import info.laht.threekt.core.FloatBufferAttribute
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import java.util.*
import kotlin.math.*

class Matrix4(
        val elements: FloatArray = floatArrayOf(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
        )
) : Cloneable, Flattable {

    override val size = 16


    /**
     * Sets all fields of this matrix.
     */
    fun set(
            n11: Float,
            n12: Float,
            n13: Float,
            n14: Float,
            n21: Float,
            n22: Float,
            n23: Float,
            n24: Float,
            n31: Float,
            n32: Float,
            n33: Float,
            n34: Float,
            n41: Float,
            n42: Float,
            n43: Float,
            n44: Float
    ): Matrix4 {
        val te = this.elements

        te[0] = n11; te[4] = n12; te[8] = n13; te[12] = n14
        te[1] = n21; te[5] = n22; te[9] = n23; te[13] = n24
        te[2] = n31; te[6] = n32; te[10] = n33; te[14] = n34
        te[3] = n41; te[7] = n42; te[11] = n43; te[15] = n44

        return this
    }

    /**
     * Resets this matrix to identity.
     */
    fun identity(): Matrix4 {
        return set(

                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f

        )
    }

    override fun clone(): Matrix4 {
        return Matrix4(elements.clone())
    }

    fun copy(m: Matrix4): Matrix4 {
        m.elements.copyInto(elements)
        return this
    }

    fun copyPosition(m: Matrix4): Matrix4 {
        val te = this.elements
        val me = m.elements

        te[12] = me[12]
        te[13] = me[13]
        te[14] = me[14]

        return this
    }

    fun extractBasis(xAxis: Vector3, yAxis: Vector3, zAxis: Vector3): Matrix4 {
        xAxis.setFromMatrixColumn(this, 0)
        yAxis.setFromMatrixColumn(this, 1)
        zAxis.setFromMatrixColumn(this, 2)

        return this
    }

    fun makeBasis(xAxis: Vector3, yAxis: Vector3, zAxis: Vector3): Matrix4 {
        return set(
                xAxis.x, yAxis.x, zAxis.x, 0f,
                xAxis.y, yAxis.y, zAxis.y, 0f,
                xAxis.z, yAxis.z, zAxis.z, 0f,
                0f, 0f, 0f, 1f
        )
    }

    /**
     * Copies the rotation component of the supplied matrix m into this matrix rotation component.
     */
    fun extractRotation(m: Matrix4): Matrix4 {

        val v1 = Vector3()

        val te = this.elements
        val me = m.elements

        val scaleX = 1f / v1.setFromMatrixColumn(m, 0).length()
        val scaleY = 1f / v1.setFromMatrixColumn(m, 1).length()
        val scaleZ = 1f / v1.setFromMatrixColumn(m, 2).length()

        te[0] = me[0] * scaleX
        te[1] = me[1] * scaleX
        te[2] = me[2] * scaleX
        te[3] = 0f

        te[4] = me[4] * scaleY
        te[5] = me[5] * scaleY
        te[6] = me[6] * scaleY
        te[7] = 0f

        te[8] = me[8] * scaleZ
        te[9] = me[9] * scaleZ
        te[10] = me[10] * scaleZ
        te[11] = 0f

        te[12] = 0f
        te[13] = 0f
        te[14] = 0f
        te[15] = 1f

        return this
    }

    fun makeRotationFromEuler(euler: Euler): Matrix4 {
        val te = this.elements

        val x = euler.x
        val y = euler.y
        val z = euler.z
        val a = cos(x)
        val b = sin(x)
        val c = cos(y)
        val d = sin(y)
        val e = cos(z)
        val f = sin(z)

        if (euler.order == EulerOrder.XYZ) {

            val ae = a * e
            val af = a * f
            val be = b * e
            val bf = b * f

            te[0] = c * e
            te[4] = -c * f
            te[8] = d

            te[1] = af + be * d
            te[5] = ae - bf * d
            te[9] = -b * c

            te[2] = bf - ae * d
            te[6] = be + af * d
            te[10] = a * c

        } else if (euler.order == EulerOrder.YXZ) {

            val ce = c * e
            val cf = c * f
            val de = d * e
            val df = d * f

            te[0] = ce + df * b
            te[4] = de * b - cf
            te[8] = a * d

            te[1] = a * f
            te[5] = a * e
            te[9] = -b

            te[2] = cf * b - de
            te[6] = df + ce * b
            te[10] = a * c

        } else if (euler.order == EulerOrder.ZXY) {

            val ce = c * e
            val cf = c * f
            val de = d * e
            val df = d * f

            te[0] = ce - df * b
            te[4] = -a * f
            te[8] = de + cf * b

            te[1] = cf + de * b
            te[5] = a * e
            te[9] = df - ce * b

            te[2] = -a * d
            te[6] = b
            te[10] = a * c

        } else if (euler.order == EulerOrder.ZYX) {

            val ae = a * e
            val af = a * f
            val be = b * e
            val bf = b * f

            te[0] = c * e
            te[4] = be * d - af
            te[8] = ae * d + bf

            te[1] = c * f
            te[5] = bf * d + ae
            te[9] = af * d - be

            te[2] = -d
            te[6] = b * c
            te[10] = a * c

        } else if (euler.order == EulerOrder.YZX) {

            val ac = a * c
            val ad = a * d
            val bc = b * c
            val bd = b * d

            te[0] = c * e
            te[4] = bd - ac * f
            te[8] = bc * f + ad

            te[1] = f
            te[5] = a * e
            te[9] = -b * e

            te[2] = -d * e
            te[6] = ad * f + bc
            te[10] = ac - bd * f

        } else if (euler.order == EulerOrder.XZY) {

            val ac = a * c
            val ad = a * d
            val bc = b * c
            val bd = b * d

            te[0] = c * e
            te[4] = -f
            te[8] = d * e

            te[1] = ac * f + bd
            te[5] = a * e
            te[9] = ad * f - bc

            te[2] = bc * f - ad
            te[6] = b * e
            te[10] = bd * f + ac

        }

        // bottom row
        te[3] = 0f
        te[7] = 0f
        te[11] = 0f

        // last column
        te[12] = 0f
        te[13] = 0f
        te[14] = 0f
        te[15] = 1f

        return this
    }

    fun makeRotationFromQuaternion(q: Quaternion): Matrix4 {
        val zero = Vector3(0f, 0f, 0f)
        val one = Vector3(1f, 1f, 1f)

        return this.compose(zero, q, one)


    }

    /**
     * Constructs a rotation matrix, looking from eye towards center with defined up vector.
     */
    fun lookAt(eye: Vector3, target: Vector3, up: Vector3): Matrix4 {
        val x = Vector3()
        val y = Vector3()
        val z = Vector3()

        val te = this.elements

        z.subVectors(eye, target)

        if (z.lengthSq() == 0f) {

            // eye and target are in the same position

            z.z = 1f

        }

        z.normalize()
        x.crossVectors(up, z)

        if (x.lengthSq() == 0f) {

            // up and z are parallel

            if (abs(up.z) == 1f) {

                z.x += 0.0001f

            } else {

                z.z += 0.0001f

            }

            z.normalize()
            x.crossVectors(up, z)

        }

        x.normalize()
        y.crossVectors(z, x)

        te[0] = x.x; te[4] = y.x; te[8] = z.x
        te[1] = x.y; te[5] = y.y; te[9] = z.y
        te[2] = x.z; te[6] = y.z; te[10] = z.z

        return this


    }

    /**
     * Multiplies this matrix by m.
     */
    fun multiply(m: Matrix4): Matrix4 {
        return this.multiplyMatrices(this, m)
    }

    fun premultiply(m: Matrix4): Matrix4 {
        return this.multiplyMatrices(m, this)
    }

    /**
     * Sets this matrix to a x b.
     */
    fun multiplyMatrices(a: Matrix4, b: Matrix4): Matrix4 {
        val ae = a.elements
        val be = b.elements
        val te = this.elements

        val a11 = ae[0]
        val a12 = ae[4]
        val a13 = ae[8]
        val a14 = ae[12]
        val a21 = ae[1]
        val a22 = ae[5]
        val a23 = ae[9]
        val a24 = ae[13]
        val a31 = ae[2]
        val a32 = ae[6]
        val a33 = ae[10]
        val a34 = ae[14]
        val a41 = ae[3]
        val a42 = ae[7]
        val a43 = ae[11]
        val a44 = ae[15]

        val b11 = be[0]
        val b12 = be[4]
        val b13 = be[8]
        val b14 = be[12]
        val b21 = be[1]
        val b22 = be[5]
        val b23 = be[9]
        val b24 = be[13]
        val b31 = be[2]
        val b32 = be[6]
        val b33 = be[10]
        val b34 = be[14]
        val b41 = be[3]
        val b42 = be[7]
        val b43 = be[11]
        val b44 = be[15]

        te[0] = a11 * b11 + a12 * b21 + a13 * b31 + a14 * b41
        te[4] = a11 * b12 + a12 * b22 + a13 * b32 + a14 * b42
        te[8] = a11 * b13 + a12 * b23 + a13 * b33 + a14 * b43
        te[12] = a11 * b14 + a12 * b24 + a13 * b34 + a14 * b44

        te[1] = a21 * b11 + a22 * b21 + a23 * b31 + a24 * b41
        te[5] = a21 * b12 + a22 * b22 + a23 * b32 + a24 * b42
        te[9] = a21 * b13 + a22 * b23 + a23 * b33 + a24 * b43
        te[13] = a21 * b14 + a22 * b24 + a23 * b34 + a24 * b44

        te[2] = a31 * b11 + a32 * b21 + a33 * b31 + a34 * b41
        te[6] = a31 * b12 + a32 * b22 + a33 * b32 + a34 * b42
        te[10] = a31 * b13 + a32 * b23 + a33 * b33 + a34 * b43
        te[14] = a31 * b14 + a32 * b24 + a33 * b34 + a34 * b44

        te[3] = a41 * b11 + a42 * b21 + a43 * b31 + a44 * b41
        te[7] = a41 * b12 + a42 * b22 + a43 * b32 + a44 * b42
        te[11] = a41 * b13 + a42 * b23 + a43 * b33 + a44 * b43
        te[15] = a41 * b14 + a42 * b24 + a43 * b34 + a44 * b44

        return this
    }

    /**
     * Multiplies this matrix by s.
     */
    fun multiplyScalar(s: Float): Matrix4 {
        val te = this.elements

        te[0] *= s; te[4] *= s; te[8] *= s; te[12] *= s
        te[1] *= s; te[5] *= s; te[9] *= s; te[13] *= s
        te[2] *= s; te[6] *= s; te[10] *= s; te[14] *= s
        te[3] *= s; te[7] *= s; te[11] *= s; te[15] *= s

        return this
    }


    fun applyToBufferAttribute(attribute: FloatBufferAttribute): FloatBufferAttribute {
        val v1 = Vector3()
        for (i in 0 until attribute.count) {

            v1.x = attribute.getX(i)
            v1.y = attribute.getY(i)
            v1.z = attribute.getZ(i)

            v1.applyMatrix4(this)

            attribute.setXYZ(i, v1.x, v1.y, v1.z)

        }

        return attribute
    }

    /**
     * Computes determinant of this matrix.
     * Based on http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/fourD/index.htm
     */
    fun determinant(): Float {
        val te = this.elements

        val n11 = te[0]
        val n12 = te[4]
        val n13 = te[8]
        val n14 = te[12]
        val n21 = te[1]
        val n22 = te[5]
        val n23 = te[9]
        val n24 = te[13]
        val n31 = te[2]
        val n32 = te[6]
        val n33 = te[10]
        val n34 = te[14]
        val n41 = te[3]
        val n42 = te[7]
        val n43 = te[11]
        val n44 = te[15]

        return (
                n41 * (
                        +n14 * n23 * n32
                                - n13 * n24 * n32
                                - n14 * n22 * n33
                                + n12 * n24 * n33
                                + n13 * n22 * n34
                                - n12 * n23 * n34
                        ) +
                        n42 * (
                        +n11 * n23 * n34
                                - n11 * n24 * n33
                                + n14 * n21 * n33
                                - n13 * n21 * n34
                                + n13 * n24 * n31
                                - n14 * n23 * n31
                        ) +
                        n43 * (
                        +n11 * n24 * n32
                                - n11 * n22 * n34
                                - n14 * n21 * n32
                                + n12 * n21 * n34
                                + n14 * n22 * n31
                                - n12 * n24 * n31
                        ) +
                        n44 * (
                        -n13 * n22 * n31
                                - n11 * n23 * n32
                                + n11 * n22 * n33
                                + n13 * n21 * n32
                                - n12 * n21 * n33
                                + n12 * n23 * n31
                        )

                )
    }

    /**
     * Transposes this matrix.
     */
    fun transpose(): Matrix4 {
        val te = this.elements
        var tmp: Float

        tmp = te[1]; te[1] = te[4]; te[4] = tmp
        tmp = te[2]; te[2] = te[8]; te[8] = tmp
        tmp = te[6]; te[6] = te[9]; te[9] = tmp

        tmp = te[3]; te[3] = te[12]; te[12] = tmp
        tmp = te[7]; te[7] = te[13]; te[13] = tmp
        tmp = te[11]; te[11] = te[14]; te[14] = tmp

        return this
    }

    fun setPosition(x: Number, y: Number, z: Number): Matrix4 {
        val te = this.elements

        te[12] = x.toFloat()
        te[13] = y.toFloat()
        te[14] = z.toFloat()

        return this
    }

    /**
     * Sets the position component for this matrix from vector v.
     */
    fun setPosition(v: Vector3): Matrix4 {
        val te = this.elements

        te[12] = v.x
        te[13] = v.y
        te[14] = v.z

        return this
    }

    /**
     * Sets this matrix to the inverse of matrix m.
     * Based on http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/fourD/index.htm.
     */
    @JvmOverloads
    fun getInverse(m: Matrix4, throwOnDegeneratee: Boolean = false): Matrix4 {
        val te = this.elements
        val me = m.elements

        val n11 = me[0]
        val n21 = me[1]
        val n31 = me[2]
        val n41 = me[3]
        val n12 = me[4]
        val n22 = me[5]
        val n32 = me[6]
        val n42 = me[7]
        val n13 = me[8]
        val n23 = me[9]
        val n33 = me[10]
        val n43 = me[11]
        val n14 = me[12]
        val n24 = me[13]
        val n34 = me[14]
        val n44 = me[15]

        val t11 =
                n23 * n34 * n42 - n24 * n33 * n42 + n24 * n32 * n43 - n22 * n34 * n43 - n23 * n32 * n44 + n22 * n33 * n44
        val t12 =
                n14 * n33 * n42 - n13 * n34 * n42 - n14 * n32 * n43 + n12 * n34 * n43 + n13 * n32 * n44 - n12 * n33 * n44
        val t13 =
                n13 * n24 * n42 - n14 * n23 * n42 + n14 * n22 * n43 - n12 * n24 * n43 - n13 * n22 * n44 + n12 * n23 * n44
        val t14 =
                n14 * n23 * n32 - n13 * n24 * n32 - n14 * n22 * n33 + n12 * n24 * n33 + n13 * n22 * n34 - n12 * n23 * n34

        val det = n11 * t11 + n21 * t12 + n31 * t13 + n41 * t14

        if (det == 0f) {
            val msg = "can't invert matrix, determinant is 0"
            if (throwOnDegeneratee) {
                throw IllegalStateException(msg)
            } else {
                println(msg)
            }
            return this.identity()
        }
        val detInv = 1f / det

        te[0] = t11 * detInv
        te[1] =
                (n24 * n33 * n41 - n23 * n34 * n41 - n24 * n31 * n43 + n21 * n34 * n43 + n23 * n31 * n44 - n21 * n33 * n44) * detInv
        te[2] =
                (n22 * n34 * n41 - n24 * n32 * n41 + n24 * n31 * n42 - n21 * n34 * n42 - n22 * n31 * n44 + n21 * n32 * n44) * detInv
        te[3] =
                (n23 * n32 * n41 - n22 * n33 * n41 - n23 * n31 * n42 + n21 * n33 * n42 + n22 * n31 * n43 - n21 * n32 * n43) * detInv

        te[4] = t12 * detInv
        te[5] =
                (n13 * n34 * n41 - n14 * n33 * n41 + n14 * n31 * n43 - n11 * n34 * n43 - n13 * n31 * n44 + n11 * n33 * n44) * detInv
        te[6] =
                (n14 * n32 * n41 - n12 * n34 * n41 - n14 * n31 * n42 + n11 * n34 * n42 + n12 * n31 * n44 - n11 * n32 * n44) * detInv
        te[7] =
                (n12 * n33 * n41 - n13 * n32 * n41 + n13 * n31 * n42 - n11 * n33 * n42 - n12 * n31 * n43 + n11 * n32 * n43) * detInv

        te[8] = t13 * detInv
        te[9] =
                (n14 * n23 * n41 - n13 * n24 * n41 - n14 * n21 * n43 + n11 * n24 * n43 + n13 * n21 * n44 - n11 * n23 * n44) * detInv
        te[10] =
                (n12 * n24 * n41 - n14 * n22 * n41 + n14 * n21 * n42 - n11 * n24 * n42 - n12 * n21 * n44 + n11 * n22 * n44) * detInv
        te[11] =
                (n13 * n22 * n41 - n12 * n23 * n41 - n13 * n21 * n42 + n11 * n23 * n42 + n12 * n21 * n43 - n11 * n22 * n43) * detInv

        te[12] = t14 * detInv
        te[13] =
                (n13 * n24 * n31 - n14 * n23 * n31 + n14 * n21 * n33 - n11 * n24 * n33 - n13 * n21 * n34 + n11 * n23 * n34) * detInv
        te[14] =
                (n14 * n22 * n31 - n12 * n24 * n31 - n14 * n21 * n32 + n11 * n24 * n32 + n12 * n21 * n34 - n11 * n22 * n34) * detInv
        te[15] =
                (n12 * n23 * n31 - n13 * n22 * n31 + n13 * n21 * n32 - n11 * n23 * n32 - n12 * n21 * n33 + n11 * n22 * n33) * detInv

        return this
    }

    /**
     * Multiplies the columns of this matrix by vector v.
     */
    fun scale(v: Vector3): Matrix4 {
        val te = this.elements
        val x = v.x
        val y = v.y
        val z = v.z

        te[0] *= x; te[4] *= y; te[8] *= z
        te[1] *= x; te[5] *= y; te[9] *= z
        te[2] *= x; te[6] *= y; te[10] *= z
        te[3] *= x; te[7] *= y; te[11] *= z

        return this
    }

    fun getMaxScaleOnAxis(): Float {
        val te = this.elements

        val scaleXSq = te[0] * te[0] + te[1] * te[1] + te[2] * te[2]
        val scaleYSq = te[4] * te[4] + te[5] * te[5] + te[6] * te[6]
        val scaleZSq = te[8] * te[8] + te[9] * te[9] + te[10] * te[10]

        return sqrt(max(scaleXSq, max(scaleYSq, scaleZSq)))
    }

    /**
     * Sets this matrix as translation transform.
     */
    fun makeTranslation(x: Float, y: Float, z: Float): Matrix4 {
        this.set(

                1f, 0f, 0f, x,
                0f, 1f, 0f, y,
                0f, 0f, 1f, z,
                0f, 0f, 0f, 1f

        )

        return this
    }

    /**
     * Sets this matrix as rotation transform around x axis by theta radians.
     *
     * @param theta Rotation angle in radians.
     */
    fun makeRotationX(theta: Float): Matrix4 {
        val c = cos(theta)
        val s = sin(theta)

        this.set(

                1f, 0f, 0f, 0f,
                0f, c, -s, 0f,
                0f, s, c, 0f,
                0f, 0f, 0f, 1f

        )

        return this
    }

    /**
     * Sets this matrix as rotation transform around y axis by theta radians.
     *
     * @param theta Rotation angle in radians.
     */
    fun makeRotationY(theta: Float): Matrix4 {
        val c = cos(theta)
        val s = sin(theta)

        this.set(

                c, 0f, s, 0f,
                0f, 1f, 0f, 0f,
                -s, 0f, c, 0f,
                0f, 0f, 0f, 1f

        )

        return this
    }

    /**
     * Sets this matrix as rotation transform around z axis by theta radians.
     *
     * @param theta Rotation angle in radians.
     */
    fun makeRotationZ(theta: Float): Matrix4 {
        val c = cos(theta)
        val s = sin(theta)

        this.set(

                c, -s, 0f, 0f,
                s, c, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f

        )

        return this
    }

    /**
     * Sets this matrix as rotation transform around axis by angle radians.
     * Based on http://www.gamedev.net/reference/articles/article1199.asp.
     *
     * @param axis Rotation axis.
     * @param theta Rotation angle in radians.
     */
    fun makeRotationAxis(axis: Vector3, angle: Float): Matrix4 {
        // Based on http://www.gamedev.net/reference/articles/article1199.asp

        val c = cos(angle)
        val s = sin(angle)
        val t = 1f - c
        val x = axis.x
        val y = axis.y
        val z = axis.z
        val tx = t * x
        val ty = t * y

        this.set(

                tx * x + c, tx * y - s * z, tx * z + s * y, 0f,
                tx * y + s * z, ty * y + c, ty * z - s * x, 0f,
                tx * z - s * y, ty * z + s * x, t * z * z + c, 0f,
                0f, 0f, 0f, 1f

        )

        return this
    }

    /**
     * Sets this matrix as scale transform.
     */
    fun makeScale(x: Float, y: Float, z: Float): Matrix4 {
        this.set(

                x, 0f, 0f, 0f,
                0f, y, 0f, 0f,
                0f, 0f, z, 0f,
                0f, 0f, 0f, 1f

        )

        return this
    }

    /**
     * Sets this matrix to the transformation composed of translation, rotation and scale.
     */
    fun compose(position: Vector3, quaternion: Quaternion, scale: Vector3): Matrix4 {
        val te = this.elements

        val x = quaternion.x
        val y = quaternion.y
        val z = quaternion.z
        val w = quaternion.w

        val x2 = x + x
        val y2 = y + y
        val z2 = z + z
        val xx = x * x2
        val xy = x * y2
        val xz = x * z2
        val yy = y * y2
        val yz = y * z2
        val zz = z * z2
        val wx = w * x2
        val wy = w * y2
        val wz = w * z2

        val sx = scale.x
        val sy = scale.y
        val sz = scale.z

        te[0] = (1 - (yy + zz)) * sx
        te[1] = (xy + wz) * sx
        te[2] = (xz - wy) * sx
        te[3] = 0f

        te[4] = (xy - wz) * sy
        te[5] = (1 - (xx + zz)) * sy
        te[6] = (yz + wx) * sy
        te[7] = 0f

        te[8] = (xz + wy) * sz
        te[9] = (yz - wx) * sz
        te[10] = (1 - (xx + yy)) * sz
        te[11] = 0f

        te[12] = position.x
        te[13] = position.y
        te[14] = position.z
        te[15] = 1f

        return this
    }

    /**
     * Decomposes this matrix into the translation, rotation and scale components.
     * If parameters are not passed, new instances will be created.
     */
    fun decompose(
            position: Vector3 = Vector3(),
            quaternion: Quaternion = Quaternion(),
            scale: Vector3 = Vector3()
    ): Triple<Vector3, Quaternion, Vector3> {
        val vector = Vector3()
        val matrix = Matrix4()
        val te = this.elements

        var sx = vector.set(te[0], te[1], te[2]).length()
        val sy = vector.set(te[4], te[5], te[6]).length()
        val sz = vector.set(te[8], te[9], te[10]).length()

        // if determine is negative, we need to invert one scale
        val det = this.determinant()
        if (det < 0) {
            sx = -sx
        }

        position.x = te[12]
        position.y = te[13]
        position.z = te[14]

        // scale the rotation part
        matrix.copy(this)

        val invSX = 1f / sx
        val invSY = 1f / sy
        val invSZ = 1f / sz

        matrix.elements[0] *= invSX
        matrix.elements[1] *= invSX
        matrix.elements[2] *= invSX

        matrix.elements[4] *= invSY
        matrix.elements[5] *= invSY
        matrix.elements[6] *= invSY

        matrix.elements[8] *= invSZ
        matrix.elements[9] *= invSZ
        matrix.elements[10] *= invSZ

        quaternion.setFromRotationMatrix(matrix)

        scale.x = sx
        scale.y = sy
        scale.z = sz

        return Triple(position, quaternion, scale)
    }

    /**
     * Creates a perspective projection matrix.
     */
    fun makePerspective(
            left: Float,
            right: Float,
            top: Float,
            bottom: Float,
            near: Float,
            far: Float
    ): Matrix4 {
        val te = this.elements
        val x = 2 * near / (right - left)
        val y = 2 * near / (top - bottom)

        val a = (right + left) / (right - left)
        val b = (top + bottom) / (top - bottom)
        val c = -(far + near) / (far - near)
        val d = -2 * far * near / (far - near)

        te[0] = x; te[4] = 0f; te[8] = a; te[12] = 0f
        te[1] = 0f; te[5] = y; te[9] = b; te[13] = 0f
        te[2] = 0f; te[6] = 0f; te[10] = c; te[14] = d
        te[3] = 0f; te[7] = 0f; te[11] = -1f; te[15] = 0f

        return this
    }

    /**
     * Creates an orthographic projection matrix.
     */
    fun makeOrthographic(
            left: Float,
            right: Float,
            top: Float,
            bottom: Float,
            near: Float,
            far: Float
    ): Matrix4 {
        val te = this.elements
        val w = 1f / (right - left)
        val h = 1f / (top - bottom)
        val p = 1f / (far - near)

        val x = (right + left) * w
        val y = (top + bottom) * h
        val z = (far + near) * p

        te[0] = 2 * w
        te[4] = 0f; te[8] = 0f; te[12] = -x
        te[1] = 0f; te[5] = 2 * h; te[9] = 0f; te[13] = -y
        te[2] = 0f; te[6] = 0f; te[10] = -2 * p; te[14] = -z
        te[3] = 0f; te[7] = 0f; te[11] = 0f; te[15] = 1f

        return this
    }

    @JvmOverloads
    fun fromArray(array: FloatArray, offset: Int = 0): Matrix4 {
        array.copyInto(elements, offset)
        return this
    }

    override fun toArray(array: FloatArray?, offset: Int): FloatArray {
        return elements.copyInto(array ?: FloatArray(16), offset)
    }

    fun toBuffer(buffer: FloatBuffer?, offset: Int): FloatBuffer {

        val buf = buffer ?: BufferUtils.createFloatBuffer(size)
        elements.forEachIndexed { i, v ->
            buf.put(i + offset, v)
        }

        return buf
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix4

        if (!elements.contentEquals(other.elements)) return false

        return true
    }

    override fun hashCode(): Int {
        return elements.contentHashCode()
    }


    override fun toString(): String {
        return "Matrix4(elements=${Arrays.toString(elements)})"
    }


}
