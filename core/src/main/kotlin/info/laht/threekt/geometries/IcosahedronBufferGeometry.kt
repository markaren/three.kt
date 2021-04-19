package info.laht.threekt.geometries

import kotlin.math.sqrt

class IcosahedronBufferGeometry(
        radius: Float? = null,
        detail: Int? = null
) : PolyhedronBufferGeometry(vertices, indices, radius, detail) {

    private companion object {

        val t = (1 + sqrt(5f)) / 2

        val vertices = floatArrayOf(
                -1f, t, 0f, 1f, t, 0f, -1f, -t, 0f, 1f, -t, 0f,
                0f, -1f, t, 0f, 1f, t, 0f, -1f, -t, 0f, 1f, -t,
                t, 0f, -1f, t, 0f, 1f, -t, 0f, -1f, -t, 0f, 1f
        )

        val indices = intArrayOf(
                0, 11, 5, 0, 5, 1, 0, 1, 7, 0, 7, 10, 0, 10, 11,
                1, 5, 9, 5, 11, 4, 11, 10, 2, 10, 7, 6, 7, 1, 8,
                3, 9, 4, 3, 4, 2, 3, 2, 6, 3, 6, 8, 3, 8, 9,
                4, 9, 5, 2, 4, 11, 6, 2, 10, 8, 6, 7, 9, 8, 1
        )


    }

}
