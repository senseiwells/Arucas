package me.senseiwells.arucas.utils

import me.senseiwells.arucas.core.ThreadHandler
import me.senseiwells.arucas.utils.ArucasExecutor.Companion.wrap
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

/**
 * Interface that allows for execution of a [Callable]
 * and returns a [Future]. This is used by [ThreadHandler]
 * to be able to run scripts on a defined thread.
 *
 * You are able to wrap an [ExecutorService] with
 * [wrap].
 */
interface ArucasExecutor {
    /**
     * Submits a [callable] to be executed. This may be
     * at a later time. A [Future] is returned for the
     * [callable] function.
     *
     * @param callable the function to execute.
     * @return the future of the function.
     */
    fun <T> submit(callable: Callable<T>): Future<T>

    companion object {
        @JvmStatic
        fun wrap(executor: ExecutorService): ArucasExecutor {
            return object: ArucasExecutor {
                override fun <T> submit(callable: Callable<T>): Future<T> {
                    return executor.submit(callable)
                }
            }
        }
    }
}