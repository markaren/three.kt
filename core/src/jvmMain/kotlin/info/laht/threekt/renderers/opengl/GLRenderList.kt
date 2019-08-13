package info.laht.threekt.renderers.opengl

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.GeometryGroup
import info.laht.threekt.core.Object3D
import info.laht.threekt.materials.Material
import kotlin.math.roundToInt

internal class GLRenderList {

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
                    material.program as _GLProgram? ?: GLProgramDefault,
                groupOrder,
                `object`.renderOrder,
                z,
                group
            ).also {
                renderItems.add(it)
            }
        } else {

            renderItem.id = `object`.id
            renderItem.`object` = `object`
            renderItem.geometry = geometry
            renderItem.material = material
            renderItem.program = material.program as _GLProgram? ?: GLProgramDefault
            renderItem.groupOrder = groupOrder
            renderItem.renderOrder = `object`.renderOrder
            renderItem.z = z
            renderItem.group = group


        }


        renderItemsIndex++

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
            transparent.add(0, renderItem)
        } else {
            opaque.add(0, renderItem)
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

    internal class RenderItem(
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