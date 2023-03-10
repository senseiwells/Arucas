package me.senseiwells.arucas.utils

import me.senseiwells.arucas.compiler.InternalTrace
import me.senseiwells.arucas.compiler.Trace
import me.senseiwells.arucas.exceptions.RuntimeError

/**
 * Utility object for helping handle exceptions.
 */
object ExceptionUtils {
    /**
     * Catches all [RuntimeError]s in a given lambda and pushes
     * a given trace to the error's stack.
     *
     * @param T the return type of the lambda.
     * @param trace the trace to push to any errors.
     * @param function the lambda to run.
     * @return whatever the lambda returns.
     */
    @JvmStatic
    inline fun <T> traceable(trace: Trace, function: () -> T): T {
        return try {
            function()
        } catch (e: RuntimeError) {
            if (e.topTrace == null || e.topTrace is InternalTrace) {
                e.pushToTop(trace)
            }
            throw e
        }
    }

    /**
     * Catches all [Exception]s in a given lambda and returns null instead.
     *
     * @param T the return type of the lambda.
     * @param function the lambda to run.
     * @return whatever the lambda returns, possibly null if an exception was caught.
     */
    @JvmStatic
    inline fun <T> catchAsNull(function: () -> T): T? {
        return try {
            function()
        } catch (e: Exception) {
            null
        }
    }
}