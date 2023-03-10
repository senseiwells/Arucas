package me.senseiwells.arucas.exceptions

import me.senseiwells.arucas.interpreter.Interpreter

abstract class ArucasError(
    override val message: String,
    cause: Throwable? = null
): RuntimeException(cause) {
    override fun fillInStackTrace() = this

    abstract fun format(interpreter: Interpreter): String
}