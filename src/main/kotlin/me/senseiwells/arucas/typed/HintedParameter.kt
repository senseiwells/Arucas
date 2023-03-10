package me.senseiwells.arucas.typed

import me.senseiwells.arucas.compiler.Trace
import me.senseiwells.arucas.interpreter.StackTable
import me.senseiwells.arucas.utils.misc.Types

/**
 * This class represents a parameter with a type hint which
 * gets converted into a [ArucasParameter] at runtime.
 *
 * @param name the name of the parameter.
 * @param hints the array of the names of the classes for the hints.
 */
class HintedParameter(
    /**
     * The name of the parameter.
     */
    val name: String,
    /**
     * The array of the names of the classes for the hints.
     */
    val hints: Array<String>? = null
) {
    /**
     * This creates an [ArucasParameter].
     *
     * @param local the local table to find the hinted classes on.
     * @param trace the trace location.
     * @return the arucas parameter.
     */
    fun create(local: StackTable, trace: Trace): ArucasParameter {
        return ArucasParameter(this.name, LazyDefinitions.of(this.hints, local, trace))
    }

    override fun equals(other: Any?): Boolean {
        return other is HintedParameter && this.name == other.name
    }

    override fun hashCode(): Int {
        return this.name.hashCode()
    }

    /**
     * This converts the hints to string.
     *
     * @return the hints as a string.
     */
    override fun toString(): String {
        return this.hints?.joinToString(" | ") ?: Types.OBJECT
    }
}