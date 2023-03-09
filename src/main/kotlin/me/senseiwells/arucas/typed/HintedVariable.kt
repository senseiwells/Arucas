package me.senseiwells.arucas.typed

import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.nodes.expressions.Expression
import me.senseiwells.arucas.utils.StackTable
import me.senseiwells.arucas.utils.Trace
import me.senseiwells.arucas.utils.Util

class HintedVariable(
    val name: String,
    private val prefix: String,
    private val readonly: Boolean,
    private val private: Boolean,
    val expression: Expression,
    private val hints: Array<String>? = null
) {
    fun create(interpreter: Interpreter, local: StackTable, trace: Trace): ArucasVariable {
        return ArucasVariable(
            this.name,
            interpreter.evaluate(local, this.expression),
            this.prefix,
            this.readonly,
            if (this.private) local else null,
            LazyDefinitions.of(this.hints, local, trace)
        )
    }

    override fun equals(other: Any?): Boolean {
        return other is HintedVariable && this.name == other.name
    }

    override fun hashCode(): Int {
        return this.name.hashCode()
    }

    override fun toString(): String {
        return this.hints?.joinToString(" | ") ?: Util.Types.OBJECT
    }
}