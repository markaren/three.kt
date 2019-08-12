package info.laht.threekt.objects

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.Intersection
import info.laht.threekt.core.Object3D
import info.laht.threekt.core.Object3DImpl
import info.laht.threekt.core.Raycaster
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

        return levels[l].`object`

    }

    override fun raycast(raycaster: Raycaster, intersects: List<Intersection>) {
        TODO()
    }

    fun update(camera: Camera) {
        TODO()
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
