package me.senseiwells.arucas.utils

import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.Propagator
import java.util.function.Supplier

/**
 * Interface that implements methods to safely executes lambdas.
 *
 * @see Interpreter
 */
interface ErrorSafe {
    /**
     * Handles a [Throwable] safely in order not to crash the application.
     *
     * @param throwable the throwable that was thrown.
     */
    fun handleError(throwable: Throwable)

    fun runSafe(block: Runnable) {
        try {
            block.run()
        } catch (e: Exception) {
            this.handleError(e)
        }
    }

    fun <T> runSafe(block: Supplier<T>): T? {
        return try {
            block.get()
        } catch (e: Exception) {
            this.handleError(e)
            null
        }
    }

    fun <T> runSafe(default: T, block: Supplier<T>): T {
        return try {
            block.get()
        } catch (e: Exception) {
            this.handleError(e)
            default
        }
    }

    fun canInterrupt(block: Runnable) {
        return try {
            block.run()
        } catch (e: InterruptedException) {
            throw Propagator.Stop.INSTANCE
        }
    }

    fun <T> canInterrupt(block: Supplier<T>): T {
        return try {
            block.get()
        } catch (e: InterruptedException) {
            throw Propagator.Stop.INSTANCE
        }
    }
}