package me.senseiwells.arucas.exceptions

import me.senseiwells.arucas.builtin.ErrorDef
import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.Trace
import java.util.*

fun runtimeError(details: String, cause: Throwable): Nothing {
    throw RuntimeError(details, cause, Trace.INTERNAL)
}

@JvmOverloads
fun runtimeError(details: String, trace: Trace = Trace.INTERNAL): Nothing {
    throw RuntimeError(details, null, trace)
}

open class RuntimeError @JvmOverloads constructor(
    message: String,
    cause: Throwable? = null,
    var topTrace: Trace? = null,
    private var stackTrace: Stack<Trace>? = null
): ArucasError(message, cause) {
    internal fun fillStackTrace(stackTrace: Stack<Trace>) {
        if (this.stackTrace == null) {
            val stack = Stack<Trace>()
            for (trace in stackTrace) {
                stack.push(trace)
            }
            this.stackTrace = stack
        }
    }

    fun pushToTop(trace: Trace) {
        this.topTrace?.let {
            val stack = this.stackTrace ?: Stack()
            stack.add(it)
            this.stackTrace = stack
        }
        this.topTrace = trace
    }

    open fun getInstance(interpreter: Interpreter): ClassInstance {
        return interpreter.getPrimitive(ErrorDef::class).create(this.message, interpreter.getNull())
    }

    open fun getErrorName() = "Error"

    override fun format(interpreter: Interpreter): String {
        val builder = StringBuilder()
        this.stackTrace?.let {
            for (trace in it) {
                builder.append("> ").append(trace.toString(interpreter, null)).append("\n")
            }
        }
        this.topTrace?.let { t ->
            builder.append("> ").append(t.toString(interpreter, this.message)).append("\n")
        }
        this.cause?.let {
            builder.append("\nCaused by: ").append(it::class.simpleName).append(" - ").append(it.message)
            if (it is ArucasError) {
                builder.append("\n").append(it.format(interpreter))
            }
            if (interpreter.properties.isDebug) {
                builder.append("\nInternal StackTrace (Something went very wrong):\n").append(it.stackTraceToString())
            }
        }
        return "${this.getErrorName()}: ${this.message}\n$builder"
    }

    class Arucas(
        private val errorInstance: ClassInstance,
        val value: ClassInstance,
        details: String
    ): RuntimeError(details, null) {
        override fun getInstance(interpreter: Interpreter): ClassInstance {
            return this.errorInstance
        }

        override fun getErrorName(): String {
            return this.errorInstance.definition.name
        }
    }
}