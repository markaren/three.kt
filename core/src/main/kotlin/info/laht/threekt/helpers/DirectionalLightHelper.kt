package info.laht.threekt.helpers

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.FloatBufferAttribute
import info.laht.threekt.core.Object3DImpl
import info.laht.threekt.lights.DirectionalLight
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.math.Vector3
import info.laht.threekt.objects.Line

class DirectionalLightHelper(
    val light: DirectionalLight,
    val size: Number = 1,
    val color: Color? = null
) : Object3DImpl() {

    private val lightPlane: Line
    private val targetLine: Line

    init {

        this.light.updateMatrixWorld()

        this.matrix = light.matrixWorld
        this.matrixAutoUpdate = false

        val material = LineBasicMaterial().apply {
            fog = false
        }

        lightPlane = Line(BufferGeometry().apply {
            val sizef = size.toFloat()
            addAttribute(
                "position", FloatBufferAttribute(
                    floatArrayOf(
                        -sizef, sizef, 0f,
                        sizef, sizef, 0f,
                        sizef, -sizef, 0f,
                        -sizef, -sizef, 0f,
                        -sizef, sizef, 0f
                    ), 3
                )
            )
        }, material)

        targetLine = Line(BufferGeometry().apply {
            addAttribute(
                "position", FloatBufferAttribute(
                    floatArrayOf(
                        0f, 0f, 0f,
                        0f, 0f, 1f
                    ), 3
                )
            )
        }, material)

        add(lightPlane)
        add(targetLine)

        update()

    }

    fun update() {

        val v1 = Vector3()
        val v2 = Vector3()
        val v3 = Vector3()

        v1.setFromMatrixPosition(this.light.matrixWorld)
        v2.setFromMatrixPosition(this.light.target.matrixWorld)
        v3.subVectors(v2, v1)

        this.lightPlane.lookAt(v2)

        if (this.color !== null) {

            this.lightPlane.material.color.set(this.color)
            this.targetLine.material.color.set(this.color)

        } else {

            this.lightPlane.material.color.copy(this.light.color)
            this.targetLine.material.color.copy(this.light.color)

        }

        this.targetLine.lookAt(v2)
        this.targetLine.scale.z = v3.length()
    }

    fun dispose() {
        this.lightPlane.geometry.dispose()
        this.lightPlane.material.dispose()
        this.targetLine.geometry.dispose()
        this.targetLine.material.dispose()
    }

}
