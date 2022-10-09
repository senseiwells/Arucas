package me.senseiwells.arucas.api

import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * Interface to handle output for the interpreter.
 */
interface ArucasOutput {
    /**
     * This should take a string and format it
     * as an error, typically a red colour.
     *
     * This method should do nothing if formatting
     * is disabled.
     */
    fun formatError(string: String): String

    /**
     * This should take a string and format it
     * as an error and bold.
     *
     * This method should do nothing if formatting
     * is disabled.
     */
    fun formatErrorBold(string: String): String

    /**
     * This should format a stack trace.
     */
    fun formatStackTrace(interpreter: Interpreter, message: String?, trace: LocatableTrace): String

    /**
     * This method should output a value
     * to the regular output for the program.
     */
    fun print(value: Any?)

    /**
     * Outputs a new line.
     */
    fun println() = this.print("\n")

    /**
     * Outputs a value then a new line.
     */
    fun println(value: Any?) = this.print(value.toString() + "\n")

    /**
     * Outputs an error.
     */
    fun printError(value: Any?) = this.println(this.formatErrorBold(value.toString()))

    /**
     * This method should output a value
     * to the logging output for the program.
     */
    fun log(value: Any?)

    /**
     * Logs a new line.
     */
    fun logln() = this.log("\n")

    /**
     * Logs a value then a new line.
     */
    fun logln(value: Any?) = this.log(value.toString() + "\n")

    /**
     * Logs an error.
     */
    fun logError(value: Any?) = this.logln(this.formatErrorBold(value.toString()))
}