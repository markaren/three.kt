package info.laht.threekt.cameras

data class View(
    var enabled: Boolean,
    var fullWidth: Int,
    var fullHeight: Int,
    var offsetX: Int,
    var offsetY: Int,
    var width: Int,
    var height: Int
) {

    override fun toString(): String {
        return "View(enabled=$enabled, fullWidth=$fullWidth, fullHeight=$fullHeight, offsetX=$offsetX, offsetY=$offsetY, width=$width, height=$height)"
    }

}
