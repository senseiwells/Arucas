package me.senseiwells.arucas.api

import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.*

/**
 * Interface to handle Errors in that happen during execution.
 *
 * By default, this just prints it to the API output.
 */
interface ArucasErrorHandler {
    companion object {
        /**
         * Default error handler - prints to API output.
         */
        @JvmStatic
        val default = object: ArucasErrorHandler { }
    }

    /**
     * Handles [RuntimeError] and [CompileError], these are non-fatal
     * and are usually a result of the user.
     *
     * These can be formatted to show a stack trace.
     *
     * @param arucasError the error that was thrown.
     * @param interpreter the interpreter that the error was thrown on.
     */
    fun handleArucasError(arucasError: ArucasError, interpreter: Interpreter) {
        interpreter.api.getOutput().printError(arucasError.format(interpreter))
    }

    /**
     * Handles [Propagator], these are propagations produced by `break`, `continue`, `return`, etc.
     *
     * These propagations weren't caught for whatever reason - likely the user used it in the wrong context.
     *
     * @param propagator the propagator that was thrown.
     * @param interpreter the interpreter that the error was thrown on.
     */
    fun handleInvalidPropagator(propagator: Propagator, interpreter: Interpreter) {
        interpreter.api.getOutput().println(propagator.message)
    }

    /**
     * Handles [FatalError], these errors happen during runtime usually because
     * a built-in method threw an [Exception] that was likely unintended.
     *
     * @param fatalError the fatal error that was thrown.
     * @param interpreter the interpreter that the error was thrown on.
     */
    fun handleFatalError(fatalError: FatalError, interpreter: Interpreter) {
        interpreter.api.getOutput().printError(fatalError.format(interpreter))
    }

    /**
     * Handles [Throwable], any other throwables that aren't handled in
     * previous methods - something has gone very wrong, this shouldn't be called.
     *
     * @param throwable the throwable that was thrown.
     * @param interpreter the interpreter that the error was thrown on.
     */
    fun handleFatalError(throwable: Throwable, interpreter: Interpreter) {
        throwable.printStackTrace()
    }
}