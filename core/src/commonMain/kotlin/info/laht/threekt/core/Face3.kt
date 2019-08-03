package info.laht.threekt.core

import info.laht.threekt.math.Color
import info.laht.threekt.math.Vector3

class Face3 private constructor(
    var a: Int,
    var b: Int,
    var c: Int,
    val normal: Vector3,
    val color: Color,
    val vertexNormals: MutableList<Vector3>,
    val vertexColors: MutableList<Color>,
    private var materialIndex: Int
): Cloneable {

    constructor(
        a: Int,
        b: Int,
        c: Int,
        normal: Vector3 = Vector3(),
        color: Color = Color(),
        materialIndex: Int = 0
    ) : this(a, b, c, normal, color, mutableListOf(), mutableListOf(), materialIndex)

    constructor(
        a: Int,
        b: Int,
        c: Int,
        vertexNormals: MutableList<Vector3>,
        vertexColors: MutableList<Color>,
        materialIndex: Int = 0
    ) : this(a, b, c, Vector3(), Color(), vertexNormals, vertexColors, materialIndex)

    override fun clone(): Face3 {
        return Face3(a, b, c).copy(this)
    }

    fun copy(source: Face3): Face3 {

        this.a = source.a
        this.b = source.b
        this.c = source.c

        this.normal.copy(source.normal)
        this.color.copy(source.color)

        vertexNormals.clear()
        source.vertexNormals.forEach {
            vertexNormals.add(it.clone())
        }

        vertexColors.clear()
        source.vertexColors.forEach {
            vertexColors.add(it.clone())
        }

        this.materialIndex = source.materialIndex

        return this

    }

}
