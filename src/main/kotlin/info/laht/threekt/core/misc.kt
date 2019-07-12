package info.laht.threekt.core

internal data class DrawRange(
    var start: Int,
    var count: Int
)

internal data class UpdateRange(
    val offset: Int,
    val count: Int
)

internal data class GeometryGroup(
    val start: Int,
    val count: Int,
    val materialIndex: Int = 0
)
