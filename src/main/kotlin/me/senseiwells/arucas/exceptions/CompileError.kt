package me.senseiwells.arucas.exceptions

import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.Trace

fun compileError(details: String, trace: Trace): Nothing = throw CompileError(details, trace)

class CompileError(
    details: String,
    val trace: Trace
): ArucasError(details) {
    override fun format(interpreter: Interpreter): String {
        return "> ${this.trace}\nCompilation Error: ${this.message}"
    }
}