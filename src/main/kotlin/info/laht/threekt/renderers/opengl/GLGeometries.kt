package info.laht.threekt.renderers.opengl

import info.laht.threekt.core.BufferAttribute
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.Event
import info.laht.threekt.core.EventLister

class GLGeometries(
    private val attributes: GLAttributes,
    private val info: GLInfo
) {

    private val onGeometryDispose = OnGeometryDispose()
    private val geometries = mutableMapOf<Int, BufferGeometry>()
    private val wireframeAttributes = mutableMapOf<Int, BufferAttribute>()

    inner class OnGeometryDispose: EventLister {

        override fun onEvent(evt: Event) {

            val geometry = evt.target as BufferGeometry
            val buffergeometry = geometries[geometry.id]!!

            buffergeometry.index?.also {
                attributes.remove(it)
            }

            buffergeometry.attributes.values.forEach {
                attributes.remove(it)
            }

            geometry.removeEventListener("dispose", onGeometryDispose)
            geometries.remove(geometry.id)

            wireframeAttributes[buffergeometry.id]?.also {
                attributes.remove(it)
                wireframeAttributes.remove(buffergeometry.id)
            }

            info.memory.geometries--

        }
    }

    fun get(): BufferGeometry {
        TODO()
    }

    fun update( geometry: BufferGeometry ) {
        TODO()
    }

    fun getWireframeAttribute( geometry: BufferGeometry ): BufferAttribute {
        TODO()
    }

}