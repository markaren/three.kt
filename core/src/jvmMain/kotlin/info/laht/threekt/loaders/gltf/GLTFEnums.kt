package info.laht.threekt.loaders.gltf

internal enum class GLTFType {

    SCALAR,
    VEC2,
    VEC3,
    VEC4,
    MAT2,
    MAT3,
    MAT4;

    fun size() = when (this) {
        SCALAR -> 1
        VEC2 -> 2
        VEC3 -> 3
        VEC4 -> 4
        MAT2 -> 4
        MAT3 -> 9
        MAT4 -> 16
    }

}

internal enum class GLTFAttribute {

    NORMAL,
    POSITION,
    TANGENT,
    TEXCOORD_0,
    TEXCOORD_1,
    COLOR_0,
    JOINTS_0,
    WEIGHTS_0

}

internal enum class GLTFAlphaMode {

    OPAQUE,
    MASK,
    BLEND

}
