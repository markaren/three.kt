package info.laht.threekt.objects

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.Intersection
import info.laht.threekt.core.Object3D
import info.laht.threekt.core.Object3DImpl
import info.laht.threekt.core.Raycaster
import info.laht.threekt.math.Vector3
import kotlin.math.abs

class LOD : Object3DImpl() {

    private val levels = mutableListOf<Level>()

    var autoUpdate = true

    fun copy(source: LOD): LOD {

        super.copy(source, true)

        levels.clear()
        source.levels.forEach {
            levels.add(it.clone())
        }

        return this

    }

    fun addLevel(`object`: Object3D, distance: Float = 0f): LOD {

        @Suppress("NAME_SHADOWING")
        val distance = abs(distance)

        var l = 0
        for (i in 1 until levels.size) {
            if (distance < levels[i].distance) {
                break
            }
            l++
        }

        levels.add(l, Level(`object`, distance))

        return this

    }

    fun getObjectForDistance(distance: Float): Object3D {

        var l = 0
        for (i in 1 until levels.size) {
            if (distance < levels[i].distance) {
                break
            }
            l++
        }

        return levels[l - 1].`object`

    }

    override fun raycast(raycaster: Raycaster, intersects: List<Intersection>) {

        val matrixPosition = Vector3()

        matrixPosition.setFromMatrixPosition(this.matrixWorld)

        val distance = raycaster.ray.origin.distanceTo(matrixPosition)

        this.getObjectForDistance(distance).raycast(raycaster, intersects)

    }

    fun update(camera: Camera) {

        val v1 = Vector3()
        val v2 = Vector3()

        val levels = this.levels

        if (levels.size > 1) {

            v1.setFromMatrixPosition(camera.matrixWorld)
            v2.setFromMatrixPosition(this.matrixWorld)

            val distance = v1.distanceTo(v2)

            levels[0].`object`.visible = true

            var j = 1
            for (i in 1 until levels.size) {

                if (distance >= levels[i].distance) {

                    levels[i - 1].`object`.visible = false
                    levels[i].`object`.visible = true

                } else {

                    break

                }

                j++

            }

            for (i in j until levels.size) {

                levels[i].`object`.visible = false

            }

        }

    }

    class Level(
            val `object`: Object3D,
            val distance: Float
    ) {

        fun clone(): Level {
            return Level(`object`.clone(), distance)
        }

    }

}
