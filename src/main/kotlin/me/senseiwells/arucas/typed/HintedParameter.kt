package me.senseiwells.arucas.typed

import me.senseiwells.arucas.compiler.Trace
import me.senseiwells.arucas.interpreter.StackTable
import me.senseiwells.arucas.utils.misc.Types

class HintedParameter(val name: String, val hints: Array<String>? = null) {
    fun create(local: StackTable, trace: Trace): ArucasParameter {
        return ArucasParameter(this.name, LazyDefinitions.of(this.hints, local, trace))
    }

    override fun equals(other: Any?): Boolean {
        return other is HintedParameter && this.name == other.name
    }

    override fun hashCode(): Int {
        return this.name.hashCode()
    }

    override fun toString(): String {
        return this.hints?.joinToString(" | ") ?: Types.OBJECT
    }
}