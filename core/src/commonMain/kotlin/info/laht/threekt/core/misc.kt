package info.laht.threekt.core

data class DrawRange(
    var start: Int,
    var count: Int
)

data class UpdateRange(
    var offset: Int,
    var count: Int
)

data class GeometryGroup(
    var start: Int,
    var count: Int,
    var materialIndex: Int = 0
)
