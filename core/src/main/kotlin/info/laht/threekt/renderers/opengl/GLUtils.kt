package info.laht.threekt.renderers.opengl

import info.laht.threekt.*
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL30

internal object GLUtils {

    fun convert(p: Int?): Int {
        if (p == TextureWrapping.Repeat.value) return GL11.GL_REPEAT
        if (p == TextureWrapping.ClampToEdge.value) return GL12.GL_CLAMP_TO_EDGE
        if (p == TextureWrapping.MirroredRepeat.value) return GL14.GL_MIRRORED_REPEAT

        if (p == TextureFilter.Nearest.value) return GL11.GL_NEAREST
        if (p == TextureFilter.NearestMipMapNearest.value) return GL11.GL_NEAREST_MIPMAP_NEAREST
        if (p == TextureFilter.NearestMipMapLinear.value) return GL11.GL_NEAREST_MIPMAP_LINEAR

        if (p == TextureFilter.Linear.value) return GL11.GL_LINEAR
        if (p == TextureFilter.LinearMipMapNearest.value) return GL11.GL_LINEAR_MIPMAP_NEAREST
        if (p == TextureFilter.LinearMipMapLinear.value) return GL11.GL_LINEAR_MIPMAP_LINEAR

        if (p == TextureType.UnsignedByte.value) return GL11.GL_UNSIGNED_BYTE
        if (p == TextureType.UnsignedShort4444.value) return GL12.GL_UNSIGNED_SHORT_4_4_4_4
        if (p == TextureType.UnsignedShort5551.value) return GL12.GL_UNSIGNED_SHORT_5_5_5_1
        if (p == TextureType.UnsignedShort565.value) return GL12.GL_UNSIGNED_SHORT_5_6_5

        if (p == TextureType.Byte.value) return GL11.GL_BYTE
        if (p == TextureType.Short.value) return GL11.GL_SHORT
        if (p == TextureType.UnsignedShort.value) return GL11.GL_UNSIGNED_SHORT
        if (p == TextureType.Int.value) return GL11.GL_INT
        if (p == TextureType.UnsignedInt.value) return GL11.GL_UNSIGNED_INT
        if (p == TextureType.Float.value) return GL11.GL_FLOAT

        if (p == TextureType.HalfFloat.value) {
            return GL30.GL_HALF_FLOAT
        }

        if (p == TextureFormat.Alpha.value) return GL11.GL_ALPHA
        if (p == TextureFormat.RGB.value) return GL11.GL_RGB
        if (p == TextureFormat.RGBA.value) return GL11.GL_RGBA
        if (p == TextureFormat.Luminance.value) return GL11.GL_LUMINANCE
        if (p == TextureFormat.LuminanceAlpha.value) return GL11.GL_LUMINANCE_ALPHA
        if (p == TextureFormat.Depth.value) return GL11.GL_DEPTH_COMPONENT
        if (p == TextureFormat.DepthStencil.value) return GL30.GL_DEPTH_STENCIL
        if (p == TextureFormat.Red.value) return GL11.GL_RED

        if (p == BlendingEquation.Add.value) return GL14.GL_FUNC_ADD
        if (p == BlendingEquation.Subtract.value) return GL14.GL_FUNC_SUBTRACT
        if (p == BlendingEquation.ReverseSubtract.value) return GL14.GL_FUNC_REVERSE_SUBTRACT

        if (p == BlendingFactor.Zero.value) return GL11.GL_ZERO
        if (p == BlendingFactor.One.value) return GL11.GL_ONE
        if (p == BlendingFactor.SrcColor.value) return GL11.GL_SRC_COLOR
        if (p == BlendingFactor.OneMinusSrcColor.value) return GL11.GL_ONE_MINUS_SRC_COLOR
        if (p == BlendingFactor.SrcAlpha.value) return GL11.GL_SRC_ALPHA
        if (p == BlendingFactor.OneMinusSrcAlpha.value) return GL11.GL_ONE_MINUS_SRC_ALPHA
        if (p == BlendingFactor.DstAlpha.value) return GL11.GL_DST_ALPHA
        if (p == BlendingFactor.OneMinusDstAlpha.value) return GL11.GL_ONE_MINUS_DST_ALPHA

        if (p == BlendingFactor.DstColor.value) return GL11.GL_DST_COLOR
        if (p == BlendingFactor.OneMinusDstColor.value) return GL11.GL_ONE_MINUS_DST_COLOR
        if (p == BlendingFactor.SrcAlphaSaturate.value) return GL11.GL_SRC_ALPHA_SATURATE

        if (p == BlendingEquation.Min.value || p == BlendingEquation.Max.value) {
            if (p == BlendingEquation.Min.value) return GL14.GL_MIN
            if (p == BlendingEquation.Max.value) return GL14.GL_MAX
        }

        if (p == TextureType.UnsignedInt248.value) {
            return GL30.GL_UNSIGNED_INT_24_8
        }

        return 0

    }

}