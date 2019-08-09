package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import kotlin.jvm.JvmStatic
import kotlin.math.max

data class Color(
        var r: Float,
        var g: Float,
        var b: Float
) : Cloneable {

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

    fun addScalar(s: Float): Color {

        this.r += s
        this.g += s
        this.b += s

        return this

    }

    fun sub(color: Color): Color {

        this.r = max(0f, this.r - color.r)
        this.g = max(0f, this.g - color.g)
        this.b = max(0f, this.b - color.b)

        return this

    }

    fun multiply(color: Color): Color {

        this.r *= color.r
        this.g *= color.g
        this.b *= color.b

        return this

    }

    fun multiplyScalar(s: Float): Color {

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

    fun fromArray(array: FloatArray, offset: Int = 0): Color {

        this.r = array[offset]
        this.g = array[offset + 1]
        this.b = array[offset + 2]

        return this

    }

    fun toArray(array: FloatArray = FloatArray(3), offset: Int = 0): FloatArray {

        array[offset + 0] = this.r
        array[offset + 1] = this.g
        array[offset + 2] = this.b

        return array

    }

    companion object {

        @JvmStatic
        val aliceblue = 0xF0F8FF
        @JvmStatic
        val antiquewhite = 0xFAEBD7
        @JvmStatic
        val aqua = 0x00FFFF
        @JvmStatic
        val aquamarine = 0x7FFFD4
        @JvmStatic
        val azure = 0xF0FFFF
        @JvmStatic
        val beige = 0xF5F5DC
        @JvmStatic
        val bisque = 0xFFE4C4
        @JvmStatic
        val black = 0x000000
        @JvmStatic
        val blanchedalmond = 0xFFEBCD
        @JvmStatic
        val blue = 0x0000FF
        @JvmStatic
        val blueviolet = 0x8A2BE2
        @JvmStatic
        val brown = 0xA52A2A
        @JvmStatic
        val burlywood = 0xDEB887
        @JvmStatic
        val cadetblue = 0x5F9EA0
        @JvmStatic
        val chartreuse = 0x7FFF00
        @JvmStatic
        val chocolate = 0xD2691E
        @JvmStatic
        val coral = 0xFF7F50
        @JvmStatic
        val cornflowerblue = 0x6495ED
        @JvmStatic
        val cornsilk = 0xFFF8DC
        @JvmStatic
        val crimson = 0xDC143C
        @JvmStatic
        val cyan = 0x00FFFF
        @JvmStatic
        val darkblue = 0x00008B
        @JvmStatic
        val darkcyan = 0x008B8B
        @JvmStatic
        val darkgoldenrod = 0xB8860B
        @JvmStatic
        val darkgray = 0xA9A9A9
        @JvmStatic
        val darkgreen = 0x006400
        @JvmStatic
        val darkgrey = 0xA9A9A9
        @JvmStatic
        val darkkhaki = 0xBDB76B
        @JvmStatic
        val darkmagenta = 0x8B008B
        @JvmStatic
        val darkolivegreen = 0x556B2F
        @JvmStatic
        val darkorange = 0xFF8C00
        @JvmStatic
        val darkorchid = 0x9932CC
        @JvmStatic
        val darkred = 0x8B0000
        @JvmStatic
        val darksalmon = 0xE9967A
        @JvmStatic
        val darkseagreen = 0x8FBC8F
        @JvmStatic
        val darkslateblue = 0x483D8B
        @JvmStatic
        val darkslategray = 0x2F4F4F
        @JvmStatic
        val darkslategrey = 0x2F4F4F
        @JvmStatic
        val darkturquoise = 0x00CED1
        @JvmStatic
        val darkviolet = 0x9400D3
        @JvmStatic
        val deeppink = 0xFF1493
        @JvmStatic
        val deepskyblue = 0x00BFFF
        @JvmStatic
        val dimgray = 0x696969
        @JvmStatic
        val dimgrey = 0x696969
        @JvmStatic
        val dodgerblue = 0x1E90FF
        @JvmStatic
        val firebrick = 0xB22222
        @JvmStatic
        val floralwhite = 0xFFFAF0
        @JvmStatic
        val forestgreen = 0x228B22
        @JvmStatic
        val fuchsia = 0xFF00FF
        @JvmStatic
        val gainsboro = 0xDCDCDC
        @JvmStatic
        val ghostwhite = 0xF8F8FF
        @JvmStatic
        val gold = 0xFFD700
        @JvmStatic
        val goldenrod = 0xDAA520
        @JvmStatic
        val gray = 0x808080
        @JvmStatic
        val green = 0x008000
        @JvmStatic
        val greenyellow = 0xADFF2F
        @JvmStatic
        val grey = 0x808080
        @JvmStatic
        val honeydew = 0xF0FFF0
        @JvmStatic
        val hotpink = 0xFF69B4
        @JvmStatic
        val indianred = 0xCD5C5C
        @JvmStatic
        val indigo = 0x4B0082
        @JvmStatic
        val ivory = 0xFFFFF0
        @JvmStatic
        val khaki = 0xF0E68C
        @JvmStatic
        val lavender = 0xE6E6FA
        @JvmStatic
        val lavenderblush = 0xFFF0F5
        @JvmStatic
        val lawngreen = 0x7CFC00
        @JvmStatic
        val lemonchiffon = 0xFFFACD
        @JvmStatic
        val lightblue = 0xADD8E6
        @JvmStatic
        val lightcoral = 0xF08080
        @JvmStatic
        val lightcyan = 0xE0FFFF
        @JvmStatic
        val lightgoldenrodyellow = 0xFAFAD2
        @JvmStatic
        val lightgray = 0xD3D3D3
        @JvmStatic
        val lightgreen = 0x90EE90
        @JvmStatic
        val lightgrey = 0xD3D3D3
        @JvmStatic
        val lightpink = 0xFFB6C1
        @JvmStatic
        val lightsalmon = 0xFFA07A
        @JvmStatic
        val lightseagreen = 0x20B2AA
        @JvmStatic
        val lightskyblue = 0x87CEFA
        @JvmStatic
        val lightslategray = 0x778899
        @JvmStatic
        val lightslategrey = 0x778899
        @JvmStatic
        val lightsteelblue = 0xB0C4DE
        @JvmStatic
        val lightyellow = 0xFFFFE0
        @JvmStatic
        val lime = 0x00FF00
        @JvmStatic
        val limegreen = 0x32CD32
        @JvmStatic
        val linen = 0xFAF0E6
        @JvmStatic
        val magenta = 0xFF00FF
        @JvmStatic
        val maroon = 0x800000
        @JvmStatic
        val mediumaquamarine = 0x66CDAA
        @JvmStatic
        val mediumblue = 0x0000CD
        @JvmStatic
        val mediumorchid = 0xBA55D3
        @JvmStatic
        val mediumpurple = 0x9370DB
        @JvmStatic
        val mediumseagreen = 0x3CB371
        @JvmStatic
        val mediumslateblue = 0x7B68EE
        @JvmStatic
        val mediumspringgreen = 0x00FA9A
        @JvmStatic
        val mediumturquoise = 0x48D1CC
        @JvmStatic
        val mediumvioletred = 0xC71585
        @JvmStatic
        val midnightblue = 0x191970
        @JvmStatic
        val mintcream = 0xF5FFFA
        @JvmStatic
        val mistyrose = 0xFFE4E1
        @JvmStatic
        val moccasin = 0xFFE4B5
        @JvmStatic
        val navajowhite = 0xFFDEAD
        @JvmStatic
        val navy = 0x000080
        @JvmStatic
        val oldlace = 0xFDF5E6
        @JvmStatic
        val olive = 0x808000
        @JvmStatic
        val olivedrab = 0x6B8E23
        @JvmStatic
        val orange = 0xFFA500
        @JvmStatic
        val orangered = 0xFF4500
        @JvmStatic
        val orchid = 0xDA70D6
        @JvmStatic
        val palegoldenrod = 0xEEE8AA
        @JvmStatic
        val palegreen = 0x98FB98
        @JvmStatic
        val paleturquoise = 0xAFEEEE
        @JvmStatic
        val palevioletred = 0xDB7093
        @JvmStatic
        val papayawhip = 0xFFEFD5
        @JvmStatic
        val peachpuff = 0xFFDAB9
        @JvmStatic
        val peru = 0xCD853F
        @JvmStatic
        val pink = 0xFFC0CB
        @JvmStatic
        val plum = 0xDDA0DD
        @JvmStatic
        val powderblue = 0xB0E0E6
        @JvmStatic
        val purple = 0x800080
        @JvmStatic
        val rebeccapurple = 0x663399
        @JvmStatic
        val red = 0xFF0000
        @JvmStatic
        val rosybrown = 0xBC8F8F
        @JvmStatic
        val royalblue = 0x4169E1
        @JvmStatic
        val saddlebrown = 0x8B4513
        @JvmStatic
        val salmon = 0xFA8072
        @JvmStatic
        val sandybrown = 0xF4A460
        @JvmStatic
        val seagreen = 0x2E8B57
        @JvmStatic
        val seashell = 0xFFF5EE
        @JvmStatic
        val sienna = 0xA0522D
        @JvmStatic
        val silver = 0xC0C0C0
        @JvmStatic
        val skyblue = 0x87CEEB
        @JvmStatic
        val slateblue = 0x6A5ACD
        @JvmStatic
        val slategray = 0x708090
        @JvmStatic
        val slategrey = 0x708090
        @JvmStatic
        val snow = 0xFFFAFA
        @JvmStatic
        val springgreen = 0x00FF7F
        @JvmStatic
        val steelblue = 0x4682B4
        @JvmStatic
        val tan = 0xD2B48C
        @JvmStatic
        val teal = 0x008080
        @JvmStatic
        val thistle = 0xD8BFD8
        @JvmStatic
        val tomato = 0xFF6347
        @JvmStatic
        val turquoise = 0x40E0D0
        @JvmStatic
        val violet = 0xEE82EE
        @JvmStatic
        val wheat = 0xF5DEB3
        @JvmStatic
        val white = 0xFFFFFF
        @JvmStatic
        val whitesmoke = 0xF5F5F5
        @JvmStatic
        val yellow = 0xFFFF00
        @JvmStatic
        val yellowgreen = 0x9ACD32

    }

}
