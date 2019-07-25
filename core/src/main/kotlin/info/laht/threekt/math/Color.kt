package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import kotlin.math.max


class Color(
    var r: Float,
    var g: Float,
    var b: Float
): Cloneable {

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

    override fun clone(): Color {
        return Color(r, g, b)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Color

        if (r != other.r) return false
        if (g != other.g) return false
        if (b != other.b) return false

        return true
    }

    override fun hashCode(): Int {
        var result = r.hashCode()
        result = 31 * result + g.hashCode()
        result = 31 * result + b.hashCode()
        return result
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

        val aliceblue = 0xF0F8FF
        val antiquewhite = 0xFAEBD7
        val aqua = 0x00FFFF
        val aquamarine = 0x7FFFD4
        val azure = 0xF0FFFF
        val beige = 0xF5F5DC
        val bisque = 0xFFE4C4
        val black = 0x000000
        val blanchedalmond = 0xFFEBCD
        val blue = 0x0000FF
        val blueviolet = 0x8A2BE2
        val brown = 0xA52A2A
        val burlywood = 0xDEB887
        val cadetblue = 0x5F9EA0
        val chartreuse = 0x7FFF00
        val chocolate = 0xD2691E
        val coral = 0xFF7F50
        val cornflowerblue = 0x6495ED
        val cornsilk = 0xFFF8DC
        val crimson = 0xDC143C
        val cyan = 0x00FFFF
        val darkblue = 0x00008B
        val darkcyan = 0x008B8B
        val darkgoldenrod = 0xB8860B
        val darkgray = 0xA9A9A9
        val darkgreen = 0x006400
        val darkgrey = 0xA9A9A9
        val darkkhaki = 0xBDB76B
        val darkmagenta = 0x8B008B
        val darkolivegreen = 0x556B2F
        val darkorange = 0xFF8C00
        val darkorchid = 0x9932CC
        val darkred = 0x8B0000
        val darksalmon = 0xE9967A
        val darkseagreen = 0x8FBC8F
        val darkslateblue = 0x483D8B
        val darkslategray = 0x2F4F4F
        val darkslategrey = 0x2F4F4F
        val darkturquoise = 0x00CED1
        val darkviolet = 0x9400D3
        val deeppink = 0xFF1493
        val deepskyblue = 0x00BFFF
        val dimgray = 0x696969
        val dimgrey = 0x696969
        val dodgerblue = 0x1E90FF
        val firebrick = 0xB22222
        val floralwhite = 0xFFFAF0
        val forestgreen = 0x228B22
        val fuchsia = 0xFF00FF
        val gainsboro = 0xDCDCDC
        val ghostwhite = 0xF8F8FF
        val gold = 0xFFD700
        val goldenrod = 0xDAA520
        val gray = 0x808080
        val green = 0x008000
        val greenyellow = 0xADFF2F
        val grey = 0x808080
        val honeydew = 0xF0FFF0
        val hotpink = 0xFF69B4
        val indianred = 0xCD5C5C
        val indigo = 0x4B0082
        val ivory = 0xFFFFF0
        val khaki = 0xF0E68C
        val lavender = 0xE6E6FA
        val lavenderblush = 0xFFF0F5
        val lawngreen = 0x7CFC00
        val lemonchiffon = 0xFFFACD
        val lightblue = 0xADD8E6
        val lightcoral = 0xF08080
        val lightcyan = 0xE0FFFF
        val lightgoldenrodyellow = 0xFAFAD2
        val lightgray = 0xD3D3D3
        val lightgreen = 0x90EE90
        val lightgrey = 0xD3D3D3
        val lightpink = 0xFFB6C1
        val lightsalmon = 0xFFA07A
        val lightseagreen = 0x20B2AA
        val lightskyblue = 0x87CEFA
        val lightslategray = 0x778899
        val lightslategrey = 0x778899
        val lightsteelblue = 0xB0C4DE
        val lightyellow = 0xFFFFE0
        val lime = 0x00FF00
        val limegreen = 0x32CD32
        val linen = 0xFAF0E6
        val magenta = 0xFF00FF
        val maroon = 0x800000
        val mediumaquamarine = 0x66CDAA
        val mediumblue = 0x0000CD
        val mediumorchid = 0xBA55D3
        val mediumpurple = 0x9370DB
        val mediumseagreen = 0x3CB371
        val mediumslateblue = 0x7B68EE
        val mediumspringgreen = 0x00FA9A
        val mediumturquoise = 0x48D1CC
        val mediumvioletred = 0xC71585
        val midnightblue = 0x191970
        val mintcream = 0xF5FFFA
        val mistyrose = 0xFFE4E1
        val moccasin = 0xFFE4B5
        val navajowhite = 0xFFDEAD
        val navy = 0x000080
        val oldlace = 0xFDF5E6
        val olive = 0x808000
        val olivedrab = 0x6B8E23
        val orange = 0xFFA500
        val orangered = 0xFF4500
        val orchid = 0xDA70D6
        val palegoldenrod = 0xEEE8AA
        val palegreen = 0x98FB98
        val paleturquoise = 0xAFEEEE
        val palevioletred = 0xDB7093
        val papayawhip = 0xFFEFD5
        val peachpuff = 0xFFDAB9
        val peru = 0xCD853F
        val pink = 0xFFC0CB
        val plum = 0xDDA0DD
        val powderblue = 0xB0E0E6
        val purple = 0x800080
        val rebeccapurple = 0x663399
        val red = 0xFF0000
        val rosybrown = 0xBC8F8F
        val royalblue = 0x4169E1
        val saddlebrown = 0x8B4513
        val salmon = 0xFA8072
        val sandybrown = 0xF4A460
        val seagreen = 0x2E8B57
        val seashell = 0xFFF5EE
        val sienna = 0xA0522D
        val silver = 0xC0C0C0
        val skyblue = 0x87CEEB
        val slateblue = 0x6A5ACD
        val slategray = 0x708090
        val slategrey = 0x708090
        val snow = 0xFFFAFA
        val springgreen = 0x00FF7F
        val steelblue = 0x4682B4
        val tan = 0xD2B48C
        val teal = 0x008080
        val thistle = 0xD8BFD8
        val tomato = 0xFF6347
        val turquoise = 0x40E0D0
        val violet = 0xEE82EE
        val wheat = 0xF5DEB3
        val white = 0xFFFFFF
        val whitesmoke = 0xF5F5F5
        val yellow = 0xFFFF00
        val yellowgreen = 0x9ACD32

    }

}
