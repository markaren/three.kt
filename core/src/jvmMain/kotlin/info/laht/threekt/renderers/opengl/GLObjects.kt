package info.laht.threekt.renderers.opengl

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.GeometryObject
import info.laht.threekt.core.Object3D

internal class GLObjects(
    private val geometries: GLGeometries,
    private val info: GLInfo
) {

    private val updateList = mutableMapOf<Int, Int>()

    fun update(`object`: Object3D): BufferGeometry {

        if (`object` !is GeometryObject) throw IllegalArgumentException("Object does not have a geometry!")

        val frame = info.render.frame

        val geometry = `object`.geometry
        val buffergeometry = geometries.get(`object`, geometry)

        if (updateList[buffergeometry.id] != frame) {

            geometries.update(buffergeometry);

            updateList[buffergeometry.id] = frame;

        }

        return buffergeometry;

    }

    fun dispose() {
        updateList.clear()
    }

}
