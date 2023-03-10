package me.senseiwells.arucas.exceptions

import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.interpreter.Interpreter

fun compileError(details: String, trace: LocatableTrace): Nothing = throw CompileError(details, trace)

class CompileError(
    details: String,
    val trace: LocatableTrace
): ArucasError(details) {
    override fun format(interpreter: Interpreter): String {
        return "> ${this.trace.toString(interpreter, "\nCompilation Error:", this.message)}"
    }
}