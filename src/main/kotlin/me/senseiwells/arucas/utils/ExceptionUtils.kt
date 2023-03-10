package me.senseiwells.arucas.utils

import me.senseiwells.arucas.compiler.InternalTrace
import me.senseiwells.arucas.compiler.Trace
import me.senseiwells.arucas.exceptions.RuntimeError

object ExceptionUtils {
    @JvmStatic
    fun <T> traceable(trace: Trace, function: () -> T): T {
        return try {
            function()
        } catch (e: RuntimeError) {
            if (e.topTrace == null || e.topTrace is InternalTrace) {
                e.pushToTop(trace)
            }
            throw e
        }
    }

    @JvmStatic
    fun <T> catchAsNull(function: () -> T): T? {
        return try {
            function()
        } catch (e: kotlin.Exception) {
            null
        }
    }
}