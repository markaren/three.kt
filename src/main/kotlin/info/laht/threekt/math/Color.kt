package info.laht.threekt.math

import kotlin.math.max


class Color(
    var r: Float,
    var g: Float,
    var b: Float
) {

    constructor() : this(0f, 0f, 0f)

    constructor(hex: Int) : this() {
        set(hex)
    }

    fun set(r: Float, g: Float, b: Float): Color {
        this.r = r
        this.g = g
        this.b = b
        return this
    }

    fun set(c: Color): Color {
        return this.copy(c)
    }

    fun set(hex: Int): Color {
        val r = (hex shr 16 and 255).toFloat() / 255
        val g = (hex shr 8 and 255).toFloat() / 255
        val b = (hex and 255).toFloat() / 255
        return set(r, g, b)
    }

    fun addScalar ( s: Float ): Color {

        this.r += s
        this.g += s
        this.b += s

        return this

    }

    fun sub ( color: Color ): Color {

        this.r = max(0f, this.r - color.r)
        this.g = max(0f, this.g - color.g)
        this.b = max(0f, this.b - color.b)

        return this

    }

    fun multiply ( color: Color ): Color {

        this.r *= color.r
        this.g *= color.g
        this.b *= color.b

        return this

    }

    fun multiplyScalar ( s: Float ): Color {

        this.r *= s
        this.g *= s
        this.b *= s

        return this

    }

    fun copy(c: Color): Color {
        return set(c.r, c.g, c.b)
    }

    fun clone(): Color {
        return Color(r, g, b)
    }

    override fun toString(): String {
        return "Color(r=$r, g=$g, b=$b)"
    }

    companion object {

        fun fromHex(hex: Int): Color {
            val r = (hex shr 16 and 255).toFloat() / 255
            val g = (hex shr 8 and 255).toFloat() / 255
            val b = (hex and 255).toFloat() / 255
            return Color(r, g, b)
        }

        val aliceblue = fromHex(0xF0F8FF)
        val antiquewhite = fromHex(0xFAEBD7)
        val aqua = fromHex(0x00FFFF)
        val aquamarine = fromHex(0x7FFFD4)
        val azure = fromHex(0xF0FFFF)
        val beige = fromHex(0xF5F5DC)
        val bisque = fromHex(0xFFE4C4)
        val black = fromHex(0x000000)
        val blanchedalmond = fromHex(0xFFEBCD)
        val blue = fromHex(0x0000FF)
        val blueviolet = fromHex(0x8A2BE2)
        val brown = fromHex(0xA52A2A)
        val burlywood = fromHex(0xDEB887)
        val cadetblue = fromHex(0x5F9EA0)
        val chartreuse = fromHex(0x7FFF00)
        val chocolate = fromHex(0xD2691E)
        val coral = fromHex(0xFF7F50)
        val cornflowerblue = fromHex(0x6495ED)
        val cornsilk = fromHex(0xFFF8DC)
        val crimson = fromHex(0xDC143C)
        val cyan = fromHex(0x00FFFF)
        val darkblue = fromHex(0x00008B)
        val darkcyan = fromHex(0x008B8B)
        val darkgoldenrod = fromHex(0xB8860B)
        val darkgray = fromHex(0xA9A9A9)
        val darkgreen = fromHex(0x006400)
        val darkgrey = fromHex(0xA9A9A9)
        val darkkhaki = fromHex(0xBDB76B)
        val darkmagenta = fromHex(0x8B008B)
        val darkolivegreen = fromHex(0x556B2F)
        val darkorange = fromHex(0xFF8C00)
        val darkorchid = fromHex(0x9932CC)
        val darkred = fromHex(0x8B0000)
        val darksalmon = fromHex(0xE9967A)
        val darkseagreen = fromHex(0x8FBC8F)
        val darkslateblue = fromHex(0x483D8B)
        val darkslategray = fromHex(0x2F4F4F)
        val darkslategrey = fromHex(0x2F4F4F)
        val darkturquoise = fromHex(0x00CED1)
        val darkviolet = fromHex(0x9400D3)
        val deeppink = fromHex(0xFF1493)
        val deepskyblue = fromHex(0x00BFFF)
        val dimgray = fromHex(0x696969)
        val dimgrey = fromHex(0x696969)
        val dodgerblue = fromHex(0x1E90FF)
        val firebrick = fromHex(0xB22222)
        val floralwhite = fromHex(0xFFFAF0)
        val forestgreen = fromHex(0x228B22)
        val fuchsia = fromHex(0xFF00FF)
        val gainsboro = fromHex(0xDCDCDC)
        val ghostwhite = fromHex(0xF8F8FF)
        val gold = fromHex(0xFFD700)
        val goldenrod = fromHex(0xDAA520)
        val gray = fromHex(0x808080)
        val green = fromHex(0x008000)
        val greenyellow = fromHex(0xADFF2F)
        val grey = fromHex(0x808080)
        val honeydew = fromHex(0xF0FFF0)
        val hotpink = fromHex(0xFF69B4)
        val indianred = fromHex(0xCD5C5C)
        val indigo = fromHex(0x4B0082)
        val ivory = fromHex(0xFFFFF0)
        val khaki = fromHex(0xF0E68C)
        val lavender = fromHex(0xE6E6FA)
        val lavenderblush = fromHex(0xFFF0F5)
        val lawngreen = fromHex(0x7CFC00)
        val lemonchiffon = fromHex(0xFFFACD)
        val lightblue = fromHex(0xADD8E6)
        val lightcoral = fromHex(0xF08080)
        val lightcyan = fromHex(0xE0FFFF)
        val lightgoldenrodyellow = fromHex(0xFAFAD2)
        val lightgray = fromHex(0xD3D3D3)
        val lightgreen = fromHex(0x90EE90)
        val lightgrey = fromHex(0xD3D3D3)
        val lightpink = fromHex(0xFFB6C1)
        val lightsalmon = fromHex(0xFFA07A)
        val lightseagreen = fromHex(0x20B2AA)
        val lightskyblue = fromHex(0x87CEFA)
        val lightslategray = fromHex(0x778899)
        val lightslategrey = fromHex(0x778899)
        val lightsteelblue = fromHex(0xB0C4DE)
        val lightyellow = fromHex(0xFFFFE0)
        val lime = fromHex(0x00FF00)
        val limegreen = fromHex(0x32CD32)
        val linen = fromHex(0xFAF0E6)
        val magenta = fromHex(0xFF00FF)
        val maroon = fromHex(0x800000)
        val mediumaquamarine = fromHex(0x66CDAA)
        val mediumblue = fromHex(0x0000CD)
        val mediumorchid = fromHex(0xBA55D3)
        val mediumpurple = fromHex(0x9370DB)
        val mediumseagreen = fromHex(0x3CB371)
        val mediumslateblue = fromHex(0x7B68EE)
        val mediumspringgreen = fromHex(0x00FA9A)
        val mediumturquoise = fromHex(0x48D1CC)
        val mediumvioletred = fromHex(0xC71585)
        val midnightblue = fromHex(0x191970)
        val mintcream = fromHex(0xF5FFFA)
        val mistyrose = fromHex(0xFFE4E1)
        val moccasin = fromHex(0xFFE4B5)
        val navajowhite = fromHex(0xFFDEAD)
        val navy = fromHex(0x000080)
        val oldlace = fromHex(0xFDF5E6)
        val olive = fromHex(0x808000)
        val olivedrab = fromHex(0x6B8E23)
        val orange = fromHex(0xFFA500)
        val orangered = fromHex(0xFF4500)
        val orchid = fromHex(0xDA70D6)
        val palegoldenrod = fromHex(0xEEE8AA)
        val palegreen = fromHex(0x98FB98)
        val paleturquoise = fromHex(0xAFEEEE)
        val palevioletred = fromHex(0xDB7093)
        val papayawhip = fromHex(0xFFEFD5)
        val peachpuff = fromHex(0xFFDAB9)
        val peru = fromHex(0xCD853F)
        val pink = fromHex(0xFFC0CB)
        val plum = fromHex(0xDDA0DD)
        val powderblue = fromHex(0xB0E0E6)
        val purple = fromHex(0x800080)
        val rebeccapurple = fromHex(0x663399)
        val red = fromHex(0xFF0000)
        val rosybrown = fromHex(0xBC8F8F)
        val royalblue = fromHex(0x4169E1)
        val saddlebrown = fromHex(0x8B4513)
        val salmon = fromHex(0xFA8072)
        val sandybrown = fromHex(0xF4A460)
        val seagreen = fromHex(0x2E8B57)
        val seashell = fromHex(0xFFF5EE)
        val sienna = fromHex(0xA0522D)
        val silver = fromHex(0xC0C0C0)
        val skyblue = fromHex(0x87CEEB)
        val slateblue = fromHex(0x6A5ACD)
        val slategray = fromHex(0x708090)
        val slategrey = fromHex(0x708090)
        val snow = fromHex(0xFFFAFA)
        val springgreen = fromHex(0x00FF7F)
        val steelblue = fromHex(0x4682B4)
        val tan = fromHex(0xD2B48C)
        val teal = fromHex(0x008080)
        val thistle = fromHex(0xD8BFD8)
        val tomato = fromHex(0xFF6347)
        val turquoise = fromHex(0x40E0D0)
        val violet = fromHex(0xEE82EE)
        val wheat = fromHex(0xF5DEB3)
        val white = fromHex(0xFFFFFF)
        val whitesmoke = fromHex(0xF5F5F5)
        val yellow = fromHex(0xFFFF00)
        val yellowgreen = fromHex(0x9ACD32)

    }

}