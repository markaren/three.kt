package info.laht.threekt.helpers

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.core.Object3DImpl
import info.laht.threekt.geometries.CylinderBufferGeometry
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.materials.MaterialWithColor
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Vector3
import info.laht.threekt.objects.Line
import info.laht.threekt.objects.Mesh
import kotlin.jvm.JvmOverloads
import kotlin.math.acos
import kotlin.math.max

class ArrowHelper(
        dir: Vector3 = Vector3(0f, 0f, 1f),
        origin: Vector3 = Vector3(),
        length: Float = 1f,
        color: Int = 0xffff00,
        headLength: Float = 0.2f * length,
        headWidth: Float = 0.4f * headLength
) : Object3DImpl() {

    val line: Line
    val cone: Mesh

    init {

        val lineGeometry = BufferGeometry().apply {
            addAttribute("position", FloatBufferAttribute(floatArrayOf(0f, 0f, 0f, 0f, 1f, 0f), 3))
        }

        val coneGeometry = CylinderBufferGeometry(0f, 0.5f, 1f, 5, 1).apply {
            translate(0f, -0.5f, 0f)
        }

        this.position.copy(origin)

        this.line = Line(lineGeometry, LineBasicMaterial().apply { this.color.set(color) })
        this.line.matrixAutoUpdate = false
        this.add(this.line)

        this.cone = Mesh(coneGeometry, MeshBasicMaterial().apply { this.color.set(color) })
        this.cone.matrixAutoUpdate = false
        this.add(this.cone)

        this.setDirection(dir)
        this.setLength(length, headLength, headWidth)

    }

    fun setDirection(dir: Vector3) = apply {

        val axis = Vector3()

        if (dir.y > 0.99999f) {

            this.quaternion.set(0f, 0f, 0f, 1f)

        } else if (dir.y < -0.99999f) {

            this.quaternion.set(1f, 0f, 0f, 0f)

        } else {

            axis.set(dir.z, 0f, -dir.x).normalize()

            val radians = acos(dir.y)

            this.quaternion.setFromAxisAngle(axis, radians)

        }

    }

    @JvmOverloads
    @Suppress("NAME_SHADOWING")
    fun setLength(length: Float, headLength: Float? = null, headWidth: Float? = null) = apply {

        val headLength = headLength ?: 0.2f * length
        val headWidth = headWidth ?: 0.2f * headLength

        this.line.scale.set(1f, max(0f, length - headLength), 1f)
        this.line.updateMatrix()

        this.cone.scale.set(headWidth, headLength, headWidth)
        this.cone.position.y = length
        this.cone.updateMatrix()

    }

    fun setColor(color: Int) = apply {

        this.line.material.color.set(color)
        val coneMaterial = cone.material
        if (coneMaterial is MaterialWithColor) {
            coneMaterial.color.set(color)
        }

    }

    fun copy(source: ArrowHelper): ArrowHelper {
        super.copy(source, false)

        this.line.copy(source.line)
        this.cone.copy(source.cone)

        return this
    }

    override fun clone(): ArrowHelper {
        return ArrowHelper().copy(this)
    }

}
