package me.senseiwells.arucas.api

import me.senseiwells.arucas.api.ArucasExecutor.Companion.wrap
import me.senseiwells.arucas.core.ThreadHandler
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
    companion object {
        /**
         * This wraps an [ExecutorService] to an [ArucasExecutor].
         *
         * @param executor the executor to wrap.
         * @return the wrapping Arucas Executor.
         */
        @JvmStatic
        fun wrap(executor: ExecutorService): ArucasExecutor {
            return object: ArucasExecutor {
                override fun <T> submit(callable: Callable<T>): Future<T> {
                    return executor.submit(callable)
                }
            }
        }
    }

    /**
     * Submits a [callable] to be executed. This may be
     * at a later time. A [Future] is returned for the
     * [callable] function.
     *
     * @param callable the function to execute.
     * @return the future of the function.
     */
    fun <T> submit(callable: Callable<T>): Future<T>
}