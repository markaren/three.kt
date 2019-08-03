package info.laht.threekt.math

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.GeometryObject
import info.laht.threekt.core.Object3D
import info.laht.threekt.objects.Mesh
import info.laht.threekt.objects.Sprite
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer


fun Vector2.toBuffer(buffer: FloatBuffer?, offset: Int): FloatBuffer {
    val buf = buffer ?: BufferUtils.createFloatBuffer(3)
    return buf.put(x).put(y)
}

fun Vector3.project(camera: Camera): Vector3 {
    return this.applyMatrix4(camera.matrixWorldInverse).applyMatrix4(camera.projectionMatrix)
}

fun Vector3.unproject(camera: Camera): Vector3 {
    return this.applyMatrix4(camera.projectionMatrixInverse).applyMatrix4(camera.matrixWorld)
}

fun Vector3.toBuffer(buffer: FloatBuffer?, offset: Int): FloatBuffer {
    val buf = buffer ?: BufferUtils.createFloatBuffer(3)
    return buf.put(x).put(y).put(z)
}

fun Vector4.toBuffer(buffer: FloatBuffer?, offset: Int): FloatBuffer {
    val buf = buffer ?: BufferUtils.createFloatBuffer(2)
    return buf.put(x).put(y).put(z).put(w)
}

fun Matrix3.toBuffer(buffer: FloatBuffer?, offset: Int): FloatBuffer {

    val buf = buffer ?: BufferUtils.createFloatBuffer(size)
    elements.forEachIndexed { i, v ->
        buf.put(i + offset, v)
    }

    return buf
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
                    attribute.toVector3(i, v1).applyMatrix4(node.matrixWorld)
                    expandByPoint(v1)
                }

            }

        }

    }

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
