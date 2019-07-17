package info.laht.threekt.renderers.opengl

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.Object3D

class GLObjects (
    private val geometries: GLGeometries,
    private val info: GLInfo
) {

    private val updateList = mutableMapOf<Int, Int>()

    fun update( `object`: Object3D): BufferGeometry {

        val frame = info.render.frame

//        val geometry = `object`.geom


        TODO()

    }

    fun dispose() {
        updateList.clear()
    }

}