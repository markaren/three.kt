package info.laht.threekt.objects

import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.materials.LineBasicMaterial

class LineLoop @JvmOverloads constructor(
        geometry: BufferGeometry? = null,
        material: LineBasicMaterial? = null
) : Line(geometry, material)
