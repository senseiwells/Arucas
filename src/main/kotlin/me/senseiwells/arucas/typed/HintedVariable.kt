package me.senseiwells.arucas.typed

import me.senseiwells.arucas.compiler.Trace
import me.senseiwells.arucas.interpreter.Interpreter
import me.senseiwells.arucas.interpreter.StackTable
import me.senseiwells.arucas.nodes.expressions.Expression
import me.senseiwells.arucas.utils.misc.Types

/**
 * This class represents a variable/field with a type hint
 * as well as other modifiers. This also holds the expression
 * for initial value of the [ArucasVariable]. This class gets
 * converted into a [ArucasVariable] at runtime.
 *
 * @param name the name of the variable/field.
 * @param prefix the prefix of the field.
 * @param readonly whether the variable is read-only.
 * @param private whether the variable is private.
 * @param expression the initial expression for the variable.
 * @param hints the array of names of the classes for the hints.
 */
class HintedVariable(
    /**
     * The name of the variable/field.
     */
    val name: String,
    /**
     * The prefix of the field.
     */
    private val prefix: String,
    /**
     * Whether the variable is read-only.
     */
    private val readonly: Boolean,
    /**
     * Whether the variable is private.
     */
    private val private: Boolean,
    /**
     * The initial expression for the variable.
     */
    val expression: Expression,
    /**
     * The array of names of the classes for the hints.
     */
    private val hints: Array<String>? = null
) {
    /**
     * This creates a [ArucasVariable].
     *
     * @param interpreter the interpreter that is creating the variable.
     * @param local the local table to find the hinted classes on.
     * @param trace the trace location.
     * @return the arucas variable.
     */
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
        return this.hints?.joinToString(" | ") ?: Types.OBJECT
    }
}