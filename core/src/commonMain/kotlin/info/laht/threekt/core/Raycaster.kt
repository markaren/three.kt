package info.laht.threekt.core

import info.laht.threekt.cameras.Camera
import info.laht.threekt.cameras.OrthographicCamera
import info.laht.threekt.cameras.PerspectiveCamera
import info.laht.threekt.math.Ray
import info.laht.threekt.math.Vector2
import info.laht.threekt.math.Vector3
import kotlin.jvm.JvmOverloads

class Raycaster @JvmOverloads constructor(
        origin: Vector3 = Vector3(),
        direction: Vector3 = Vector3(),
        val near: Float = 0f,
        val far: Float = Float.POSITIVE_INFINITY
) {

    val ray = Ray(origin, direction)
    private var camera: Camera? = null

    /**
     * Updates the ray with a new origin and direction.
     * @param origin The origin vector where the ray casts from.
     * @param direction The normalized direction vector that gives direction to the ray.
     */
    fun set(origin: Vector3, direction: Vector3) {
        this.ray.set(origin, direction)
    }

    /**
     * Updates the ray with a new origin and direction.
     * @param coords 2D coordinates of the mouse, in normalized device coordinates (NDC)---X and Y components should be between -1 and 1.
     * @param camera camera from which the ray should originate
     */
    fun setFromCamera(coords: Vector2, camera: Camera) {
        when (camera) {
            is PerspectiveCamera -> {

                this.ray.origin.setFromMatrixPosition(camera.matrixWorld)
                this.ray.direction.set(coords.x, coords.y, 0.5f).unproject(camera).sub(this.ray.origin).normalize()
                this.camera = camera

            }
            is OrthographicCamera -> {

                this.ray.origin.set(coords.x, coords.y, (camera.near + camera.far) / (camera.near - camera.far))
                        .unproject(camera) // set origin in plane of camera
                this.ray.direction.set(0f, 0f, -1f).transformDirection(camera.matrixWorld)
                this.camera = camera

            }
            else -> throw IllegalArgumentException("Unsupported camera type: $camera")
        }
    }

    private fun intersectObject(`object`: Object3D, raycaster: Raycaster, intersects: MutableList<Intersection>, recursive: Boolean) {

        if (!`object`.visible) return

        `object`.raycast(raycaster, intersects)

        if (recursive) {

            `object`.children.forEach {

                intersectObject(it, raycaster, intersects, true)

            }

        }
    }

    /**
     * Checks all intersection between the ray and the object with or without the descendants. Intersections are returned sorted by distance, closest first.
     * @param object The object to check for intersection with the ray.
     * @param recursive If true, it also checks all descendants. Otherwise it only checks intersecton with the object. Default is false.
     * @param optionalTarget (optional) target to set the result. Otherwise a new Array is instantiated. If set, you must clear this array prior to each call (i.e., array.length = 0)
     */
    @JvmOverloads
    fun intersectObject(
            `object`: Object3D,
            recursive: Boolean = false,
            intersects: MutableList<Intersection> = mutableListOf()
    ): List<Intersection> {

        intersectObject(`object`, this, intersects, recursive)

        intersects.sort()

        return intersects
    }


    /**
     * Checks all intersection between the ray and the objects with or without the descendants. Intersections are returned sorted by distance, closest first. Intersections are of the same form as those returned by .intersectObject.
     * @param objects The objects to check for intersection with the ray.
     * @param recursive If true, it also checks all descendants of the objects. Otherwise it only checks intersecton with the objects. Default is false.
     * @param optionalTarget (optional) target to set the result. Otherwise a new Array is instantiated. If set, you must clear this array prior to each call (i.e., array.length = 0)
     */
    @JvmOverloads
    fun intersectObjects(
            objects: List<Object3D>,
            recursive: Boolean = false,
            intersects: MutableList<Intersection> = mutableListOf()
    ): List<Intersection> {

        objects.forEach {
            intersectObject(it, this, intersects, recursive)
        }

        intersects.sort()

        return intersects

    }

}

data class Intersection internal constructor(
        var distance: Float,
        var distanceToRay: Float? = null,
        var point: Vector3,
        var index: Int? = null,
        var face: Face3? = null,
        var faceIndex: Int? = null,
        var `object`: Object3D,
        var uv: Vector2? = null,
        var uv2: Vector2? = null
) : Comparable<Intersection> {

    override fun compareTo(other: Intersection): Int {

        val diff = distance - other.distance
        return when {
            diff == 0f -> 0
            diff > 0 -> 1
            else -> -1
        }
    }

}
