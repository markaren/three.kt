package info.laht.threekt.math

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.core.GeometryObject
import info.laht.threekt.core.Object3D
import info.laht.threekt.objects.Mesh
import info.laht.threekt.objects.Sprite
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer


/**
 * Sets this vector's x and y values from the attribute.
 * @param attribute the source attribute.
 * @param index index in the attribute.
 */
fun Vector2.fromBufferAttribute(attribute: FloatBufferAttribute, index: Int): Vector2 {
    this.x = attribute.getX(index)
    this.y = attribute.getY(index)

    return this
}

fun Vector2.toBuffer(buffer: FloatBuffer?, offset: Int): FloatBuffer {
    val buf = buffer ?: BufferUtils.createFloatBuffer(3)
    return buf.put(x).put(y)
}

fun Vector3.project(camera: Camera): Vector3 {
    return this.applyMatrix4( camera.matrixWorldInverse ).applyMatrix4( camera.projectionMatrix )
}

fun Vector3.unproject(camera: Camera): Vector3 {
    return this.applyMatrix4( camera.projectionMatrixInverse ).applyMatrix4( camera.matrixWorld )
}

fun Vector3.fromBufferAttribute(attribute: FloatBufferAttribute, index: Int): Vector3 {
    this.x = attribute.getX( index )
    this.y = attribute.getY( index )
    this.z = attribute.getZ( index )

    return this
}

fun Vector3.toBuffer(buffer: FloatBuffer?, offset: Int): FloatBuffer {
    val buf = buffer ?: BufferUtils.createFloatBuffer(3)
    return buf.put(x).put(y).put(z)
}

fun Vector4.fromBufferAttribute(attribute: FloatBufferAttribute, index: Int): Vector4 {

    this.x = attribute.getX(index)
    this.y = attribute.getY(index)
    this.z = attribute.getZ(index)
    this.w = attribute.getW(index)

    return this

}

fun Vector4.toBuffer(buffer: FloatBuffer?, offset: Int): FloatBuffer {
    val buf = buffer ?: BufferUtils.createFloatBuffer(2)
    return buf.put(x).put(y).put(z).put(w)
}

fun Matrix3.applyToBufferAttribute(attribute: FloatBufferAttribute): FloatBufferAttribute {
    val v1 = Vector3()

    for (i in 0 until attribute.count) {
        v1.x = attribute.getX(i)
        v1.y = attribute.getY(i)
        v1.z = attribute.getZ(i)

        v1.applyMatrix3(this)

        attribute.setXYZ(i, v1.x, v1.y, v1.z)
    }

    return attribute

}

fun Matrix3.toBuffer(buffer: FloatBuffer?, offset: Int): FloatBuffer {

    val buf = buffer ?: BufferUtils.createFloatBuffer(size)
    elements.forEachIndexed { i, v ->
        buf.put(i + offset, v)
    }

    return buf
}

fun Matrix4.applyToBufferAttribute(attribute: FloatBufferAttribute): FloatBufferAttribute {
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

fun Matrix4.toBuffer(buffer: FloatBuffer?, offset: Int): FloatBuffer {

    val buf = buffer ?: BufferUtils.createFloatBuffer(size)
    elements.forEachIndexed { i, v ->
        buf.put(i + offset, v)
    }

    return buf
}

fun Box3.setFromObject(`object`: Object3D): Box3 {
    this.makeEmpty()

    return this.expandByObject(`object`)
}

fun Box3.expandByObject(`object`: Object3D): Box3 {

    val v1 = Vector3()

    `object`.updateMatrixWorld(true)
    `object`.traverse { node ->

        if (node is Mesh) {

            val geometry = node.geometry

            geometry.attributes.position?.also { attribute ->

                for (i in 0 until attribute.count) {
                    v1.fromBufferAttribute(attribute, i).applyMatrix4(node.matrixWorld)
                    expandByPoint(v1)
                }

            }

        }

    }

    return this
}

fun Box3.setFromBufferAttribute(attribute: FloatBufferAttribute ): Box3 {
    var minX = Float.POSITIVE_INFINITY
    var minY = Float.POSITIVE_INFINITY
    var minZ = Float.POSITIVE_INFINITY

    var maxX = Float.NEGATIVE_INFINITY
    var maxY = Float.NEGATIVE_INFINITY
    var maxZ = Float.NEGATIVE_INFINITY

    var i = 0
    val l = attribute.count
    while (i < l) {

        val x = attribute.getX(i)
        val y = attribute.getY(i)
        val z = attribute.getZ(i)

        if (x < minX) minX = x
        if (y < minY) minY = y
        if (z < minZ) minZ = z

        if (x > maxX) maxX = x
        if (y > maxY) maxY = y
        if (z > maxZ) maxZ = z
        i++

    }

    this.min.set(minX, minY, minZ)
    this.max.set(maxX, maxY, maxZ)

    return this
}

fun Frustum.intersectsObject(`object`: Object3D): Boolean {

    val sphere = Sphere()

    `object` as GeometryObject

    val geometry = `object`.geometry

    if (geometry.boundingSphere == null) {
        geometry.computeBoundingSphere()
    }

    sphere.copy(geometry.boundingSphere!!)
        .applyMatrix4(`object`.matrixWorld)

    return this.intersectsSphere(sphere)

}

fun Frustum.intersectsSprite(sprite: Sprite): Boolean {

    val sphere = Sphere()

    sphere.center.set(0, 0, 0)
    sphere.radius = 0.7071067811865476f
    sphere.applyMatrix4(sprite.matrixWorld)

    return this.intersectsSphere(sphere)

}
