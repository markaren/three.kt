package info.laht.threekt

import kotlin.reflect.KClass

expect class Logger {

    fun warn(message: String)

    fun info(message: String)

    fun debug(message: String)

    fun error(message: String)

}

expect fun getLogger(clazz: KClass<*>): Logger
