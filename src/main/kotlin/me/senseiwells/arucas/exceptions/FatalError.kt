package me.senseiwells.arucas.exceptions

import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.Trace
import java.util.*

class FatalError(
    message: String,
    override val cause: Throwable,
    private val stackTrace: Stack<Trace>
): ArucasError(message) {
    override fun format(interpreter: Interpreter): String {
        val builder = StringBuilder()
        for (trace in this.stackTrace) {
            builder.append("> ").append(trace).append("\n")
        }
        builder.append(if (builder.isEmpty()) "StackTrace was empty, this is a bug!" else "StackTrace (most recent call last)")
        builder.append("\nCaused by: ").append(this.cause::class.simpleName).append(" - ").append(this.cause.message)
        if (interpreter.properties.isDebug) {
            builder.append("\nInternal StackTrace (Something went very wrong):\n").append(this.cause.stackTraceToString())
        }
        return "${"FatalError"}: ${this.message}\n$builder"
    }
}