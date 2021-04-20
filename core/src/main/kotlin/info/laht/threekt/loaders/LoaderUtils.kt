package info.laht.threekt.loaders

object LoaderUtils {

    fun extractUrlBase(url: String): String {

        @Suppress("NAME_SHADOWING")
        val url = url.replace("\\", "/")

        val index = url.lastIndexOf("/")

        if (index == -1) return "./"

        return url.substring(0, index + 1)

    }

}
