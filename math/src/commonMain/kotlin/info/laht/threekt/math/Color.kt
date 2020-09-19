package info.laht.threekt.math

import info.laht.threekt.core.Cloneable
import kotlin.jvm.JvmField
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

        @JvmField
        val aliceblue = 0xF0F8FF

        @JvmField
        val antiquewhite = 0xFAEBD7

        @JvmField
        val aqua = 0x00FFFF

        @JvmField
        val aquamarine = 0x7FFFD4

        @JvmField
        val azure = 0xF0FFFF

        @JvmField
        val beige = 0xF5F5DC

        @JvmField
        val bisque = 0xFFE4C4

        @JvmField
        val black = 0x000000

        @JvmField
        val blanchedalmond = 0xFFEBCD

        @JvmField
        val blue = 0x0000FF

        @JvmField
        val blueviolet = 0x8A2BE2

        @JvmField
        val brown = 0xA52A2A

        @JvmField
        val burlywood = 0xDEB887

        @JvmField
        val cadetblue = 0x5F9EA0

        @JvmField
        val chartreuse = 0x7FFF00

        @JvmField
        val chocolate = 0xD2691E

        @JvmField
        val coral = 0xFF7F50

        @JvmField
        val cornflowerblue = 0x6495ED

        @JvmField
        val cornsilk = 0xFFF8DC

        @JvmField
        val crimson = 0xDC143C

        @JvmField
        val cyan = 0x00FFFF

        @JvmField
        val darkblue = 0x00008B

        @JvmField
        val darkcyan = 0x008B8B

        @JvmField
        val darkgoldenrod = 0xB8860B

        @JvmField
        val darkgray = 0xA9A9A9

        @JvmField
        val darkgreen = 0x006400

        @JvmField
        val darkgrey = 0xA9A9A9

        @JvmField
        val darkkhaki = 0xBDB76B

        @JvmField
        val darkmagenta = 0x8B008B

        @JvmField
        val darkolivegreen = 0x556B2F

        @JvmField
        val darkorange = 0xFF8C00

        @JvmField
        val darkorchid = 0x9932CC

        @JvmField
        val darkred = 0x8B0000

        @JvmField
        val darksalmon = 0xE9967A

        @JvmField
        val darkseagreen = 0x8FBC8F

        @JvmField
        val darkslateblue = 0x483D8B

        @JvmField
        val darkslategray = 0x2F4F4F

        @JvmField
        val darkslategrey = 0x2F4F4F

        @JvmField
        val darkturquoise = 0x00CED1

        @JvmField
        val darkviolet = 0x9400D3

        @JvmField
        val deeppink = 0xFF1493

        @JvmField
        val deepskyblue = 0x00BFFF

        @JvmField
        val dimgray = 0x696969

        @JvmField
        val dimgrey = 0x696969

        @JvmField
        val dodgerblue = 0x1E90FF

        @JvmField
        val firebrick = 0xB22222

        @JvmField
        val floralwhite = 0xFFFAF0

        @JvmField
        val forestgreen = 0x228B22

        @JvmField
        val fuchsia = 0xFF00FF

        @JvmField
        val gainsboro = 0xDCDCDC

        @JvmField
        val ghostwhite = 0xF8F8FF

        @JvmField
        val gold = 0xFFD700

        @JvmField
        val goldenrod = 0xDAA520

        @JvmField
        val gray = 0x808080

        @JvmField
        val green = 0x008000

        @JvmField
        val greenyellow = 0xADFF2F

        @JvmField
        val grey = 0x808080

        @JvmField
        val honeydew = 0xF0FFF0

        @JvmField
        val hotpink = 0xFF69B4

        @JvmField
        val indianred = 0xCD5C5C

        @JvmField
        val indigo = 0x4B0082

        @JvmField
        val ivory = 0xFFFFF0

        @JvmField
        val khaki = 0xF0E68C

        @JvmField
        val lavender = 0xE6E6FA

        @JvmField
        val lavenderblush = 0xFFF0F5

        @JvmField
        val lawngreen = 0x7CFC00

        @JvmField
        val lemonchiffon = 0xFFFACD

        @JvmField
        val lightblue = 0xADD8E6

        @JvmField
        val lightcoral = 0xF08080

        @JvmField
        val lightcyan = 0xE0FFFF

        @JvmField
        val lightgoldenrodyellow = 0xFAFAD2

        @JvmField
        val lightgray = 0xD3D3D3

        @JvmField
        val lightgreen = 0x90EE90

        @JvmField
        val lightgrey = 0xD3D3D3

        @JvmField
        val lightpink = 0xFFB6C1

        @JvmField
        val lightsalmon = 0xFFA07A

        @JvmField
        val lightseagreen = 0x20B2AA

        @JvmField
        val lightskyblue = 0x87CEFA

        @JvmField
        val lightslategray = 0x778899

        @JvmField
        val lightslategrey = 0x778899

        @JvmField
        val lightsteelblue = 0xB0C4DE

        @JvmField
        val lightyellow = 0xFFFFE0

        @JvmField
        val lime = 0x00FF00

        @JvmField
        val limegreen = 0x32CD32

        @JvmField
        val linen = 0xFAF0E6

        @JvmField
        val magenta = 0xFF00FF

        @JvmField
        val maroon = 0x800000

        @JvmField
        val mediumaquamarine = 0x66CDAA

        @JvmField
        val mediumblue = 0x0000CD

        @JvmField
        val mediumorchid = 0xBA55D3

        @JvmField
        val mediumpurple = 0x9370DB

        @JvmField
        val mediumseagreen = 0x3CB371

        @JvmField
        val mediumslateblue = 0x7B68EE

        @JvmField
        val mediumspringgreen = 0x00FA9A

        @JvmField
        val mediumturquoise = 0x48D1CC

        @JvmField
        val mediumvioletred = 0xC71585

        @JvmField
        val midnightblue = 0x191970

        @JvmField
        val mintcream = 0xF5FFFA

        @JvmField
        val mistyrose = 0xFFE4E1

        @JvmField
        val moccasin = 0xFFE4B5

        @JvmField
        val navajowhite = 0xFFDEAD

        @JvmField
        val navy = 0x000080

        @JvmField
        val oldlace = 0xFDF5E6

        @JvmField
        val olive = 0x808000

        @JvmField
        val olivedrab = 0x6B8E23

        @JvmField
        val orange = 0xFFA500

        @JvmField
        val orangered = 0xFF4500

        @JvmField
        val orchid = 0xDA70D6

        @JvmField
        val palegoldenrod = 0xEEE8AA

        @JvmField
        val palegreen = 0x98FB98

        @JvmField
        val paleturquoise = 0xAFEEEE

        @JvmField
        val palevioletred = 0xDB7093

        @JvmField
        val papayawhip = 0xFFEFD5

        @JvmField
        val peachpuff = 0xFFDAB9

        @JvmField
        val peru = 0xCD853F

        @JvmField
        val pink = 0xFFC0CB

        @JvmField
        val plum = 0xDDA0DD

        @JvmField
        val powderblue = 0xB0E0E6

        @JvmField
        val purple = 0x800080

        @JvmField
        val rebeccapurple = 0x663399

        @JvmField
        val red = 0xFF0000

        @JvmField
        val rosybrown = 0xBC8F8F

        @JvmField
        val royalblue = 0x4169E1

        @JvmField
        val saddlebrown = 0x8B4513

        @JvmField
        val salmon = 0xFA8072

        @JvmField
        val sandybrown = 0xF4A460

        @JvmField
        val seagreen = 0x2E8B57

        @JvmField
        val seashell = 0xFFF5EE

        @JvmField
        val sienna = 0xA0522D

        @JvmField
        val silver = 0xC0C0C0

        @JvmField
        val skyblue = 0x87CEEB

        @JvmField
        val slateblue = 0x6A5ACD

        @JvmField
        val slategray = 0x708090

        @JvmField
        val slategrey = 0x708090

        @JvmField
        val snow = 0xFFFAFA

        @JvmField
        val springgreen = 0x00FF7F

        @JvmField
        val steelblue = 0x4682B4

        @JvmField
        val tan = 0xD2B48C

        @JvmField
        val teal = 0x008080

        @JvmField
        val thistle = 0xD8BFD8

        @JvmField
        val tomato = 0xFF6347

        @JvmField
        val turquoise = 0x40E0D0

        @JvmField
        val violet = 0xEE82EE

        @JvmField
        val wheat = 0xF5DEB3

        @JvmField
        val white = 0xFFFFFF

        @JvmField
        val whitesmoke = 0xF5F5F5

        @JvmField
        val yellow = 0xFFFF00

        @JvmField
        val yellowgreen = 0x9ACD32

    }

}
