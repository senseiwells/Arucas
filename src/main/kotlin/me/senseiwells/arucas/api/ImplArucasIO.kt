package me.senseiwells.arucas.api

import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.LocatableTrace
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.math.log10

const val BOLD = "\u001b[1;31m"
const val DEBUG = "\u001b[1;36m"
const val ERROR = "\u001b[0;31m"
const val RESET = "\u001b[0m"

open class ImplArucasIO(
    private val format: Boolean = true
): ArucasInput, ArucasOutput {
    private val scanner = Scanner(System.`in`)

    override fun takeInput(): CompletableFuture<String> {
        return CompletableFuture.completedFuture(this.scanner.next())
    }

    override fun formatError(string: String) = if (this.format) ERROR + string + RESET else string

    override fun formatErrorBold(string: String) = if (this.format) BOLD + string + RESET else string

    override fun print(value: Any?) = kotlin.io.print(value)

    override fun log(value: Any?) = this.print(if (this.format) "$DEBUG[DEBUG]$RESET $value" else "[DEBUG] $value")

    override fun formatStackTrace(interpreter: Interpreter, message: String?, trace: LocatableTrace): String {
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