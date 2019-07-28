package info.laht.threekt.helpers

import info.laht.threekt.geometries.SphereBufferGeometry
import info.laht.threekt.lights.PointLight
import info.laht.threekt.materials.MaterialWithColor
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Color
import info.laht.threekt.objects.Mesh

class PointLightHelper(
    val light: PointLight,
    sphereSize: Number,
    val color: Color? = null
) : Mesh(
    geometry = SphereBufferGeometry(sphereSize.toFloat(), 4, 2),
    material = MeshBasicMaterial().apply {
        wireframe = true
        fog = false
    }) {

    init {

        this.matrix = this.light.matrixWorld;
        this.matrixAutoUpdate = false;

        update()
    }

    fun update() {

        (material as MaterialWithColor).also { material ->
            if (this.color != null) {

                material.color.set(this.color);

            } else {

                material.color.copy(this.light.color);

            }
        }


    }

    fun dispose() {
        this.geometry.dispose();
        this.material.dispose();
    }

}