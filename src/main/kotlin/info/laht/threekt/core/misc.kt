package info.laht.threekt.core

internal class DrawRange(
    var start: Int,
    var count: Int
)

internal class UpdateRange(
    var offset: Int,
    var count: Int
)

internal class GeometryGroup(
    var start: Int,
    var count: Int,
    var materialIndex: Int = 0
)
