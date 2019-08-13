package info.laht.threekt

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

actual class Logger(
    name: String
) {

    private val logger: org.slf4j.Logger = LoggerFactory.getLogger(name)

    constructor(clazz: Class<*>) : this(clazz.simpleName)

    actual fun error(message: String) {
        logger.error(message)
    }

    actual fun warn(message: String) {
        logger.warn(message)
    }

    actual fun info(message: String) {
        logger.info(message)
    }

    actual fun debug(message: String) {
        logger.debug(message)
    }

}

private val loggerMap = mutableMapOf<String, Logger>()

actual fun getLogger(clazz: KClass<*>): Logger {
    val name = clazz.simpleName!!
    return loggerMap.computeIfAbsent(name) { Logger(name) }
}
