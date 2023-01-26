package me.senseiwells.arucas.api.impl

import me.senseiwells.arucas.api.ArucasInput
import me.senseiwells.arucas.api.ArucasOutput
import java.util.*
import java.util.concurrent.CompletableFuture

private const val BOLD = "\u001b[1;31m"
private const val DEBUG = "\u001b[1;36m"
private const val ERROR = "\u001b[0;31m"
private const val RESET = "\u001b[0m"

/**
 * The default implementation for Arucas input and output.
 *
 * This simply just takes input from the terminal as well
 * as prints to the terminal with formatting, if enabled.
 *
 * @param format whether to format errors and logs.
 * @see ArucasInput
 * @see ArucasOutput
 */
open class DefaultArucasIO(
    /**
     * Whether to format errors and logs.
     */
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
}