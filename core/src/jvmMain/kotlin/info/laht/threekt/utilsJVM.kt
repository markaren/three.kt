package info.laht.threekt

import kotlinx.io.charsets.Charset
import java.io.File

actual fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
}

actual fun String.readText(charset: Charset): String {
    return File(this).readText(charset)
}

actual fun String.readBytes(): ByteArray {
    return File(this).readBytes()
}
