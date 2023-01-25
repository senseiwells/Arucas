package me.senseiwells.arucas.api

import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.ArucasError
import me.senseiwells.arucas.exceptions.FatalError
import me.senseiwells.arucas.exceptions.Propagator

interface ArucasErrorHandler {
    companion object {
        @JvmStatic
        val default = object: ArucasErrorHandler { }
    }

    fun handleArucasError(arucasError: ArucasError, interpreter: Interpreter) {
        interpreter.api.getOutput().printError(arucasError.format(interpreter))
    }

    fun handleInvalidPropagator(propagator: Propagator, interpreter: Interpreter) {
        interpreter.api.getOutput().println(propagator.message)
    }

    fun handleFatalError(fatalError: FatalError, interpreter: Interpreter) {
        interpreter.api.getOutput().printError(fatalError.format(interpreter))
    }

    fun handleFatalError(throwable: Throwable, interpreter: Interpreter) {
        throwable.printStackTrace()
    }
}