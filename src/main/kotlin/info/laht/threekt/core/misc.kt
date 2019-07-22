package info.laht.threekt.core

class DrawRange(
    var start: Int,
    var count: Int
)

class UpdateRange(
    var offset: Int,
    var count: Int
)

class GeometryGroup(
    var start: Int,
    var count: Int,
    var materialIndex: Int = 0
)
