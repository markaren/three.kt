@file:JvmName("THREE")

package info.laht.threekt

enum class CullFaceMode(
    val value: Int
) {
    None(0),
    Back(1),
    Front(2),
    FrontBack(3)
}

enum class FrontFaceDirection(
    val value: Int
) {
    CW(0),
    CCW(1)
}

enum class ShadowType(
    val value: Int
) {
    Basic(0),
    PCF(1),
    PCFSoft(2)
}

enum class Side(
    val value: Int
) {
    Front(0),
    Back(1),
    Double(2)
}

enum class Shading(
    val value: Int
) {
    Flat(1),
    Smooth(2)
}

enum class Colors(
    val value: Int
) {
    None(0),
    Face(1),
    Vertex(2)
}

enum class Blending(
    val value: Int
) {
    None(0),
    Normal(1),
    Additive(2),
    Subtractive(3),
    Multiply(4),
    Custom(5)
}

enum class BlendingEquation(
    val value: Int
) {
    Add(100),
    Subtract(101),
    ReverseSubtract(102),
    Min(103),
    Max(104),
}

enum class BlendingFactor(
    val value: Int
) {
    Zero(200),
    One(201),
    SrcColor(202),
    OneMinusSrcColor(203),
    SrcAlpha(204),
    OneMinusSrcAlpha(205),
    DstAlpha(206),
    OneMinusDstAlpha(207),
    DstColor(208),
    OneMinusDstColor(209),
    SrcAlphaSaturate(210)
}

enum class DepthMode(
    val value: Int
) {
    NeverDepth(0),
    AlwaysDepth(1),
    LessDepth(2),
    LessEqualDepth(3),
    EqualDepth(4),
    GreaterEqualDepth(5),
    GreaterDepth(6),
    NotEqualDepth(7)
}

enum class TextureCombineOperation(
    val value: Int
) {
    Multiply(0),
    Mix(1),
    Add(2)
}

enum class ToneMapping(
    val value: Int
) {
    None(0),
    Linear(1),
    Reinhard(2),
    Uncharted2(3),
    Cineon(4),
    ACESFilmic(5)
}

enum class TextureMapping(
    val value: Int
) {
    UV(300),
    CubeReflection(301),
    CubeRefraction(302),
    EquirectangularReflection(303),
    EquirectangularRefraction(304),
    SphericalReflection(305),
    CubeUVReflection(306),
    CubeUVRefraction(307)
}

enum class TextureWrapping(
    val value: Int
) {
    Repeat(1000),
    ClampToEdge(1001),
    MirroredRepeat(1002)
}

enum class TextureFilter(
    val value: Int
) {
    Nearest(1003),
    NearestMipMapNearest(1004),
    NearestMipMapLinear(1005),
    Linear(1006),
    LinearMipMapNearest(1007),
    LinearMipMapLinear(1008)
}

enum class TextureType(
    val value: kotlin.Int
) {
    UnsignedByte(1009),
    Byte(1010),
    Short(1011),
    UnsignedShort(1012),
    Int(1013),
    UnsignedInt(1014),
    Float(1015),
    HalfFloat(1016),
    UnsignedShort4444(1017),
    UnsignedShort5551(1018),
    UnsignedShort565(1019),
    UnsignedInt248(1020)
}

enum class TextureFormat(
    val value: Int
) {
    Alpha(1021),
    RGB(1022),
    RGBA(1023),
    Luminance(1024),
    LuminanceAlpha(1025),
    RGBE(RGBA.value),
    Depth(1026),
    DepthStencil(1027),
    Red(1028),
}

//enum class DDS_ST3CCompressedTextureFormat(
//    val value: Int
//) {
//    RGB_S3TC_DXT1_Format(33776),
//    RGBA_S3TC_DXT1_Format(33777),
//    RGBA_S3TC_DXT3_Format(33778),
//    RGBA_S3TC_DXT5_Format(33779)
//}
//
//enum class PVRTCCompressedTextureFormat(
//    val value: Int
//) {
//    RGB_PVRTC_4BPPV1_Format(35840),
//    RGB_PVRTC_2BPPV1_Format(35841),
//    RGBA_PVRTC_4BPPV1_Format(35842),
//    RGBA_PVRTC_2BPPV1_Format(35843)
//}
//
//enum class ETCCompressedTextureFormat(
//    val value: Int
//) {
//    RGB_ETC1_Format(36196)
//}

//RGBA_ASTC_4x4_Format(37808),
//RGBA_ASTC_5x4_Format(37809),
//RGBA_ASTC_5x5_Format(37810),
//RGBA_ASTC_6x5_Format(37811),
//RGBA_ASTC_6x6_Format(37812),
//RGBA_ASTC_8x5_Format(37813),
//RGBA_ASTC_8x6_Format(37814),
//RGBA_ASTC_8x8_Format(37815),
//RGBA_ASTC_10x5_Format(37816),
//RGBA_ASTC_10x6_Format(37817),
//RGBA_ASTC_10x8_Format(37818),
//RGBA_ASTC_10x10_Format(37819),
//RGBA_ASTC_12x10_Format(37820),
//RGBA_ASTC_12x12_Format(37821)

enum class LoopMode(
    val value: Int
) {
    Once(2200),
    Repeat(2201),
    PingPong(2202)
}


enum class InterpolationMode(
    val value: Int
) {
    Discrete(2300),
    Linear(2301),
    Smooth(2302)
}

enum class EndingModel(
    val value: Int
) {
    ZeroCurvature(2400),
    ZeroSlope(2401),
    WrapAround(2402)
}

enum class DrawMode(
    val value: Int
) {
    Triangles(0),
    TriangleStrip(1),
    TriangleFan(2)
}

enum class TextureEncoding(
    val value: Int
) {
    Linear(3000),
    sRGB(3001),
    Gamma(3007),
    RGBE(3002),
    LogLuv(3003),
    RGBM7(3004),
    RGBM16(3005),
    RGBD(3006),
    BasicDepthPacking(3200),
    RGBADepthPacking(3201),
}

enum class NormalMapType(
    val value: Int
) {
    TangentSpace(0),
    ObjectSpace(1)
}
