package me.senseiwells.arucas.api

import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.LocatableTrace
import kotlin.math.log10

/**
 * Interface to handle output for the interpreter.
 */
@Suppress("UNUSED")
interface ArucasOutput {
    /**
     * This should take a string and format it
     * as an error, typically a red colour.
     *
     * @param string the string to format.
     * @return the formatted string.
     */
    fun formatError(string: String): String

    /**
     * This should take a string and format it
     * as an error and bold.
     *
     * @param string the string to format.
     * @return the formatted string.
     */
    fun formatErrorBold(string: String): String

    /**
     * This method should output a value
     * to the regular output for the program.
     *
     * @param value the value to print.
     */
    fun print(value: Any?)

    /**
     * Outputs a new line.
     */
    fun println() = this.print("\n")

    /**
     * Outputs a value then a new line.
     *
     * @param value the value to print.
     */
    fun println(value: Any?) = this.print(value.toString() + "\n")

    /**
     * Outputs an error.
     *
     * @param value the value to print as an error.
     */
    fun printError(value: Any?) = this.println(this.formatErrorBold(value.toString()))

    /**
     * This method should output a value
     * to the debug output for the program.
     *
     * @param value the value to log.
     */
    fun log(value: Any?)

    /**
     * Logs a new line.
     */
    fun logln() = this.log("\n")

    /**
     * Logs a value then a new line.
     *
     * @param value the value to log.
     */
    fun logln(value: Any?) = this.log(value.toString() + "\n")

    /**
     * Logs an error.
     *
     * @param value the value to log as an error.
     */
    fun logError(value: Any?) = this.logln(this.formatErrorBold(value.toString()))

    /**
     * This should format a stack trace.
     *
     * @param interpreter the interpreter that formatted this stack trace.
     * @param message a given message to format the trace with.
     * @param trace the trace tor format.
     * @return the formatted stack trace.
     */
    fun formatStackTrace(interpreter: Interpreter, message: String?, trace: LocatableTrace): String {
        return defaultFormatStackTrace(interpreter, message, trace)
    }

    companion object {
        @JvmStatic
        fun defaultFormatStackTrace(interpreter: Interpreter, message: String?, trace: LocatableTrace): String {
            val maxLength = interpreter.properties.errorMaxLength
            val lines = trace.fileContent.lines()
            val errorLine = trace.line + 1
            val padSize = 1.coerceAtLeast((log10(errorLine.toDouble()) + 1.0).toInt());
            val padFormat = "%" + padSize + "d"
            val numPadding = " ".repeat(padSize)

            var errorStart = trace.column
            var errorEnd = trace.column + 1
            var errorString = lines[errorLine - 1]

            if (errorStart > maxLength / 2) {
                val diff = errorStart - (maxLength / 2)
                errorStart -= diff - 4
                errorEnd -= diff - 4
                errorString = "... " + errorString.substring(diff)
            }

            if (errorString.length > maxLength - 4) {
                if (errorEnd > maxLength - 4) {
                    errorEnd = maxLength - 4
                }

                errorString = errorString.substring(0, maxLength - 4) + " ..."
            }

            val sb = StringBuilder()

            sb.append("\n")
                .append(String.format(padFormat, errorLine)).append(" | ").append(errorString).append("\n")
                .append(numPadding).append(" | ").append(" ".repeat(errorStart)).append("^".repeat(errorEnd - errorStart))

            if (message != null) {
                sb.append("\n").append(numPadding).append(" | ").append(" ".repeat(errorStart)).append(message)
            }

            return sb.toString()
        }
    }
}