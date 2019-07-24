package info.laht.threekt.renderers.opengl

import info.laht.threekt.cameras.Camera
import info.laht.threekt.core.*
import info.laht.threekt.core.GeometryGroup
import info.laht.threekt.materials.Material
import info.laht.threekt.objects.Group
import info.laht.threekt.scenes.Scene
import kotlin.math.roundToInt

class GLRenderList internal constructor() {

    private val renderItems = mutableListOf<RenderItem>()
    private var renderItemsIndex = 0

    val opaque = mutableListOf<RenderItem>()
    val transparent = mutableListOf<RenderItem>()

    fun init() {
        renderItemsIndex = 0
        opaque.clear()
        transparent.clear()
    }

    private fun getNextRenderItem(
            `object`: Object3D,
            geometry: BufferGeometry,
            material: Material,
            groupOrder: Int,
            z: Float,
            group: GeometryGroup?
    ): RenderItem {
        var renderItem = renderItems.getOrNull(renderItemsIndex)
        if (renderItem == null) {
            renderItem = RenderItem(
                    `object`.id,
                    `object`,
                    geometry,
                    material,
                    material.program ?: GLProgramDefault,
                    groupOrder,
                    `object`.renderOrder,
                    z,
                    group
            ).also {
                renderItems.add(it)
            }
        } else {

            renderItem.id = `object`.id;
            renderItem.`object` = `object`;
            renderItem.geometry = geometry;
            renderItem.material = material;
            renderItem.program = material.program ?: GLProgramDefault;
            renderItem.groupOrder = groupOrder;
            renderItem.renderOrder = `object`.renderOrder;
            renderItem.z = z;
            renderItem.group = group;


        }


        renderItemsIndex++;

        return renderItem

    }

    fun push(
            `object`: Object3D,
            geometry: BufferGeometry,
            material: Material,
            groupOrder: Int,
            z: Float,
            group: GeometryGroup?
    ) {

        val renderItem = getNextRenderItem(`object`, geometry, material, groupOrder, z, group)

        if (material.transparent) {
            transparent.add(renderItem)
        } else {
            opaque.add(renderItem)
        }

    }

    fun unshift(
            `object`: Object3D,
            geometry: BufferGeometry,
            material: Material,
            groupOrder: Int,
            z: Float,
            group: GeometryGroup?
    ) {

        val renderItem = getNextRenderItem(`object`, geometry, material, groupOrder, z, group)

        if (material.transparent) {
            transparent.add(renderItem)
        } else {
            opaque.add(renderItem)
        }

    }

    fun sort() {
        if (opaque.size > 1) {
            opaque.sortedWith(Comparator { a, b ->
                when {
                    a.groupOrder != b.groupOrder -> a.groupOrder - b.groupOrder
                    a.renderOrder != b.renderOrder -> a.renderOrder - b.renderOrder
                    a.program.id != b.program.id -> a.program.id - b.program.id
                    a.material.id != b.material.id -> a.material.id - b.material.id
                    a.z != b.z -> (a.z - b.z).roundToInt()
                    else -> a.id - b.id
                }
            })
        }
        if (transparent.size > 1) {
            transparent.sortedWith(Comparator { a, b ->
                when {
                    a.groupOrder != b.groupOrder -> a.groupOrder - b.groupOrder
                    a.renderOrder != b.renderOrder -> a.renderOrder - b.renderOrder
                    a.z != b.z -> (b.z - a.z).roundToInt()
                    else -> a.id - b.id
                }
            })
        }
    }

    class RenderItem(
            var id: Int,
            var `object`: Object3D,
            var geometry: BufferGeometry,
            var material: Material,
            var program: _GLProgram,
            var groupOrder: Int,
            var renderOrder: Int,
            var z: Float,
            var group: GeometryGroup?
    )

}

class GLRenderLists {

    private val onSceneDispose = OnSceneDispose()

    private val lists = mutableMapOf<Int, MutableMap<Int, GLRenderList>>()

    fun get(scene: Scene, camera: Camera): GLRenderList {

        val cameras = lists[scene.id]

        val list: GLRenderList
        if (cameras == null) {
            list = GLRenderList()
            lists[scene.id] = mutableMapOf(camera.id to list)
            scene.addEventListener("dispose", onSceneDispose)
        } else {
            list = cameras[camera.id] ?: GLRenderList().also {
                cameras[camera.id] = it
            }
        }

        return list

    }

    fun dispose() {
        lists.clear()
    }

    private inner class OnSceneDispose : EventLister {

        override fun onEvent(event: Event) {
            val scene = event.target as Scene
            scene.removeEventListener("dispose", this)
        }

    }

}

