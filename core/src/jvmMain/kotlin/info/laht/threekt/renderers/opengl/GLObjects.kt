package info.laht.threekt.renderers.opengl

import info.laht.threekt.core.*
import info.laht.threekt.objects.InstancedMesh
import org.lwjgl.opengl.GL15

internal class GLObjects(
    private val geometries: GLGeometries,
    private val attributes: GLAttributes,
    private val info: GLInfo
) {

    private val updateList = mutableMapOf<Int, Int>()
    private val onInstancedMeshDispose = OnInstancedMeshDispose()

    fun update(`object`: Object3D): BufferGeometry {

        if (`object` !is GeometryObject) throw IllegalArgumentException("Object does not have a geometry!")

        val frame = info.render.frame

        val geometry = `object`.geometry
        val buffergeometry = geometries.get(`object`, geometry)

        if (updateList[buffergeometry.id] != frame) {

            geometries.update(buffergeometry)

            updateList[buffergeometry.id] = frame

        }

        if (`object` is InstancedMesh) {
            if (!`object`.hasEventListener("dispose", onInstancedMeshDispose)) {
                `object`.addEventListener("dispose", onInstancedMeshDispose)
            }
            attributes.update(`object`.instanceMatrix, GL15.GL_ARRAY_BUFFER)
            if (`object`.instanceColor != null) {
                attributes.update(`object`.instanceColor!!, GL15.GL_ARRAY_BUFFER)
            }
        }

        return buffergeometry

    }

    fun dispose() {
        updateList.clear()
    }

    private inner class OnInstancedMeshDispose : EventLister {
        override fun onEvent(event: Event) {
            val instancedMesh = event.target as InstancedMesh
            instancedMesh.removeEventListener("dispose", onInstancedMeshDispose)
            attributes.remove(instancedMesh.instanceMatrix)
            if (instancedMesh.instanceColor != null) {
                attributes.remove(instancedMesh.instanceColor!!)
            }
        }
    }

}
