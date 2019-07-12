package info.laht.threekt.renderers.renderers.gl

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.Object3D
import info.laht.threekt.materials.Material
import info.laht.threekt.objects.Group

class GLRenderList {

    private val renderItems = mutableListOf<RenderItem>()
    private var renderItemsIndex = 0

    private val opaque = mutableListOf<RenderItem>()
    private val transparent = mutableListOf<RenderItem>()

    var defaultProgram = -1

    fun init() {
        renderItemsIndex = 0
        opaque.clear()
        transparent.clear()
    }

    fun getNextRenderItem( `object`: Object3D, geometry: BufferGeometry, material: Material, groupOrder: Int, z: Int, group: Group): RenderItem {
        return if (renderItemsIndex < renderItems.size) {
            renderItems[renderItemsIndex]
        } else {
            RenderItem(
                `object`.id,
                `object`,
                geometry,
                material,
                material.program ?: defaultProgram,
                groupOrder,
                `object`.renderOrder,
                z,
                group
            ).also {
                renderItems[ renderItemsIndex ] = it
            }
        }.also {
            renderItemsIndex ++;
        }

    }

    fun push(`object`: Object3D, geometry: BufferGeometry, material: Material, groupOrder: Int, z: Int, group: Group) {

        val renderItem = getNextRenderItem(`object`, geometry, material, groupOrder, z, group)

        if (material.transparent) {
            transparent.add(renderItem)
        } else {
            opaque.add(renderItem)
        }

    }

    fun unshift(`object`: Object3D, geometry: BufferGeometry, material: Material, groupOrder: Int, z: Int, group: Group) {

        val renderItem = getNextRenderItem(`object`, geometry, material, groupOrder, z, group)

        if (material.transparent) {
            transparent.add(renderItem)
        } else {
            opaque.add(renderItem)
        }

    }

    fun sort() {
        if ( opaque.size > 1 ) opaque.sortedWith(PainterSortStable)
        if ( transparent.size > 1 ) transparent.sortedWith( ReversePainterSortStable );
    }

}

class RenderItem (
    val id: Int,
    val `object`: Object3D,
    val geometry: BufferGeometry,
    val material: Material,
    val program: Int,
    val groupOrder: Int,
    val renderOrder: Int,
    var z: Int,
    var group: Group
)

private object PainterSortStable: Comparator<RenderItem> {

    override fun compare(a: RenderItem, b: RenderItem): Int {
        return when {
            a.groupOrder != b.groupOrder -> a.groupOrder - b.groupOrder
            a.renderOrder != b.renderOrder -> a.renderOrder - b.renderOrder
//        a.program != b.program -> a.program.id - b.program.id
            a.material.id != b.material.id -> a.material.id - b.material.id
            a.z != b.z -> a.z - b.z
            else -> a.id - b.id
        }
    }
}

private object ReversePainterSortStable: Comparator<RenderItem> {

    override fun compare(a: RenderItem, b: RenderItem): Int {
        return when {
            a.groupOrder != b.groupOrder -> a.groupOrder - b.groupOrder
            a.renderOrder != b.renderOrder -> a.renderOrder - b.renderOrder
            a.z != b.z -> b.z - a.z
            else -> a.id - b.id
        }
    }
}
