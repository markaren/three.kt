package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import info.laht.threekt.core.GeometryObject
import info.laht.threekt.core.Object3D
import info.laht.threekt.objects.Sprite
import kotlin.jvm.JvmOverloads

class Frustum @JvmOverloads constructor(

    p0: Plane = Plane(),
    p1: Plane = Plane(),
    p2: Plane = Plane(),
    p3: Plane = Plane(),
    p4: Plane = Plane(),
    p5: Plane = Plane()

) : Cloneable {

    private val planes = arrayOf(p0, p1, p2, p3, p4, p5)

    fun set(
        p0: Plane,
        p1: Plane,
        p2: Plane,
        p3: Plane,
        p4: Plane,
        p5: Plane
    ) {

        planes[0] = p0
        planes[1] = p1
        planes[2] = p2
        planes[3] = p3
        planes[4] = p4
        planes[5] = p5

    }

    fun copy(source: Frustum): Frustum {
        planes.forEachIndexed { i, plane ->
            plane.copy(source.planes[i])
        }
        return this
    }

    override fun clone(): Frustum {
        return Frustum().copy(this)
    }

    fun setFromMatrix(m: Matrix4): Frustum {

        val me = m.elements
        val me0 = me[0]
        val me1 = me[1]
        val me2 = me[2]
        val me3 = me[3]
        val me4 = me[4]
        val me5 = me[5]
        val me6 = me[6]
        val me7 = me[7]
        val me8 = me[8]
        val me9 = me[9]
        val me10 = me[10]
        val me11 = me[11]
        val me12 = me[12]
        val me13 = me[13]
        val me14 = me[14]
        val me15 = me[15]

        planes[0].setComponents(me3 - me0, me7 - me4, me11 - me8, me15 - me12).normalize()
        planes[1].setComponents(me3 + me0, me7 + me4, me11 + me8, me15 + me12).normalize()
        planes[2].setComponents(me3 + me1, me7 + me5, me11 + me9, me15 + me13).normalize()
        planes[3].setComponents(me3 - me1, me7 - me5, me11 - me9, me15 - me13).normalize()
        planes[4].setComponents(me3 - me2, me7 - me6, me11 - me10, me15 - me14).normalize()
        planes[5].setComponents(me3 + me2, me7 + me6, me11 + me10, me15 + me14).normalize()

        return this
    }

    fun intersectsSphere(sphere: Sphere): Boolean {

        val planes = this.planes
        val center = sphere.center
        val negRadius = -sphere.radius

        for (i in 0 until 6) {

            val distance = planes[i].distanceToPoint(center)
            if (distance < negRadius) {
                return false
            }

        }

        return true

    }

    fun intersectsBox(box: Box3): Boolean {

        val p = Vector3()

        for (i in 0 until 6) {

            val plane = planes[i]

            // corner at max distance

            p.x = if (plane.normal.x > 0) box.max.x else box.min.x
            p.y = if (plane.normal.y > 0) box.max.y else box.min.y
            p.z = if (plane.normal.z > 0) box.max.z else box.min.z

            if (plane.distanceToPoint(p) < 0) {
                return false
            }

        }
        return true

    }


    fun containsPoint(point: Vector3): Boolean {

        planes.forEach {
            if (it.distanceToPoint(point) < 0) {
                return false
            }
        }

        return true

    }


    fun intersectsObject(`object`: Object3D): Boolean {

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

    fun intersectsSprite(sprite: Sprite): Boolean {

        val sphere = Sphere()

        sphere.center.set(0f, 0f, 0f)
        sphere.radius = 0.7071067811865476f
        sphere.applyMatrix4(sprite.matrixWorld)

        return this.intersectsSphere(sphere)

    }

}
