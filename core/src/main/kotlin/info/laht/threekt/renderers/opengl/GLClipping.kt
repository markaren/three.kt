package info.laht.threekt.renderers.opengl

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.Uniform
import info.laht.threekt.math.Matrix3
import info.laht.threekt.math.Plane

internal class GLClipping {

    private var globalState: FloatArray? = null
    private var numGlobalPlanes = 0
    private var localClippingEnabled = false
    private var renderingShadows = false
    private var plane = Plane()
    private var viewNormalMatrix = Matrix3()

    var uniform = Uniform(null).apply {
        needsUpdate = false
    }

    var numPlanes = 0
    var numIntersection = 0

    fun init(planes: List<Plane>, enableLocalClipping: Boolean, camera: Camera): Boolean {

        val enabled =
            planes.isNotEmpty() ||
                    enableLocalClipping ||
                    numGlobalPlanes != 0 ||
                    localClippingEnabled

        localClippingEnabled = enableLocalClipping
        globalState = projectPlanes(planes, camera, 0)

        numGlobalPlanes = planes.size

        return enabled

    }

    fun beginShadows() {
        renderingShadows = true
        numPlanes = 0
    }

    fun endShadows() {
        renderingShadows = false
        resetGlobalState()
    }

    fun setState(
        planes: List<Plane>?,
        clipIntersection: Boolean,
        clipShadows: Boolean,
        camera: Camera,
        cache: Properties,
        fromCache: Boolean
    ) {
        if (!localClippingEnabled || planes == null || planes.isEmpty() || renderingShadows && !clipShadows) {

            // there's no local clipping

            if (renderingShadows) {

                // there's no global clipping

                numPlanes = 0//projectPlanes( null );

            } else {

                resetGlobalState()

            }

        } else {

            val nGlobal = if (renderingShadows) 0 else numGlobalPlanes
            val lGlobal = nGlobal * 4

            var dstArray = cache["clippingState"] as FloatArray?

            uniform.value = dstArray // ensure unique state

            dstArray = projectPlanes(planes, camera, lGlobal, fromCache)

            for (i in 0 until lGlobal) {

                dstArray[i] = globalState!!.get(i)

            }

            cache["clippingState"] = dstArray
            this.numIntersection = if (clipIntersection) this.numPlanes else 0
            this.numPlanes += nGlobal

        }

    }

    private fun resetGlobalState() {
        if (uniform.value != globalState) {

            uniform.value = globalState
            uniform.needsUpdate = numGlobalPlanes > 0

        }

        numPlanes = numGlobalPlanes
        numIntersection = 0
    }

    private fun projectPlanes(
        planes: List<Plane>,
        camera: Camera,
        dstOffset: Int,
        skipTransform: Boolean = false
    ): FloatArray {

        val nPlanes = planes.size
        var dstArray = uniform.value as FloatArray?

        if (!skipTransform || dstArray == null) {
            val flatSize = dstOffset + nPlanes * 4
            val viewMatrix = camera.matrixWorldInverse

            if (dstArray == null || dstArray.size < flatSize) {
                dstArray = FloatArray(flatSize)
            }

            var i4 = dstOffset
            planes.forEach {
                plane.copy(it).applyMatrix4(viewMatrix, viewNormalMatrix)

                plane.normal.toArray(dstArray, i4)
                dstArray[i4 + 3] = plane.constant

                i4 += 4
            }

        }

        uniform.value = dstArray
        uniform.needsUpdate = true

        numPlanes = nPlanes

        return dstArray
    }

}
