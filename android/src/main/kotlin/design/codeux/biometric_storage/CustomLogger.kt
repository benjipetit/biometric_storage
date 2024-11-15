package design.codeux.biometric_storage

import io.flutter.plugin.common.MethodChannel
import io.github.oshai.kotlinlogging.KotlinLogging

class CustomLogger(methodChannel: MethodChannel) {
    private val key = "log"
    private val channel = methodChannel
    private val logger = KotlinLogging.logger {}

    fun trace(log: String) {
        channel.invokeMethod(key, log)
        logger.trace(log)
    }

    fun error(errorLog: String) {
        channel.invokeMethod(key, errorLog)
        logger.error(errorLog)
    }

    fun warn(warnLog: String) {
        channel.invokeMethod(key, warnLog)
        logger.warn(warnLog)
    }

    fun debug(debugLog: String) {
        channel.invokeMethod(key, debugLog)
        logger.debug(debugLog)
    }
}