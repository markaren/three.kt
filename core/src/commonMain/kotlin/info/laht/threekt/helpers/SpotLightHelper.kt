package info.laht.threekt.helpers

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.core.Object3DImpl
import info.laht.threekt.lights.SpotLight
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.Vector3
import info.laht.threekt.objects.LineSegments
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class SpotLightHelper(
    val light: SpotLight,
    val color: Color? = null
) : Object3DImpl() {

    private val tmpVector = Vector3()

    private val cone: LineSegments

    init {

        light.updateMatrixWorld()

        matrix = light.matrixWorld
        matrixAutoUpdate = false

        val geometry = BufferGeometry()

        val positions = mutableListOf(
            0f, 0f, 0f, 0f, 0f, 1f,
            0f, 0f, 0f, 1f, 0f, 1f,
            0f, 0f, 0f, -1f, 0f, 1f,
            0f, 0f, 0f, 0f, 1f, 1f,
            0f, 0f, 0f, 0f, -1f, 1f
        )

        val l = 32
        var j = 1
        for (i in 0 until l) {

            val p1 = ((i.toFloat() / l) * PI * 2).toFloat()
            val p2 = ((j.toFloat() / l) * PI * 2).toFloat()

            positions.addAll(
                listOf(
                    cos(p1), sin(p1), 1f,
                    cos(p2), sin(p2), 1f
                )
            )

            j++
        }

        geometry.addAttribute("position", FloatBufferAttribute(positions.toFloatArray(), 3))

        val material = LineBasicMaterial().apply {
            fog = false
        }

        cone = LineSegments(geometry, material)
        add(cone)

        update()

    }

    private fun update() {

        this.light.updateMatrixWorld()

        val coneLength = if (this.light.distance > 0f) this.light.distance else 1000f
        val coneWidth = coneLength * tan(this.light.angle)

        this.cone.scale.set(coneWidth, coneWidth, coneLength)

        tmpVector.setFromMatrixPosition(this.light.target.matrixWorld)

        this.cone.lookAt(tmpVector)

        if (this.color != null) {

            this.cone.material.color.set(this.color)

        } else {

            this.cone.material.color.copy(this.light.color)

        }
    }

    fun dispose() {
        cone.geometry.dispose()
        cone.material.dispose()
    }

}
