package info.laht.threekt

import kotlinx.io.charsets.Charset
import java.io.File

internal actual fun String.readText(charset: Charset): String {
    return File(this).readText()
}

internal actual fun String.readBytes(): ByteArray {
    return File(this).readBytes()
}