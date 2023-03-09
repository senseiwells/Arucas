package me.senseiwells.arucas.typed

import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.StackTable
import me.senseiwells.arucas.utils.Trace

class ArucasVariable constructor(
    val name: String,
    private var instance: ClassInstance,
    private val prefix: String,
    private val readonly: Boolean,
    private val local: StackTable? = null,
    private val definitions: LazyDefinitions = LazyDefinitions.of()
) {
    private var locked = false

    fun set(instance: ClassInstance, interpreter: Interpreter, trace: Trace) {
        if (this.readonly && this.locked) {
            runtimeError("Cannot reassign '$this'", trace)
        }
        if (this.local != null && !interpreter.isWithinStack(this.local)) {
            runtimeError("Cannot assign private field '$this'", trace)
        }

        if (!this.canAssign(instance)) {
            runtimeError("Hinted type for '$this' got '${instance.definition.name}' but expected '${this.typesAsString()}'", trace)
        }
        this.instance = instance
    }

    fun get(interpreter: Interpreter, trace: Trace): ClassInstance {
        if (this.local != null && !interpreter.isWithinStack(this.local)) {
            runtimeError("Cannot access private field '$this'", trace)
        }
        return this.instance
    }

    fun typesAsString(): String {
        return this.definitions.get().joinToString(" | ")
    }

    internal fun finalise(trace: Trace) {
        this.locked = true
        if (!this.canAssign(this.instance)) {
            runtimeError("Hinted type for '$this' was constructed with '${this.instance.definition.name}' but expected '${this.typesAsString()}'", trace)
        }
    }

    private fun canAssign(instance: ClassInstance): Boolean {
        val definitions = this.definitions.get()
        if (definitions.isEmpty()) {
            return true
        }
        for (definition in definitions) {
            if (instance.isOf(definition)) {
                return true
            }
        }
        return false
    }

    /**
     * Returns the `prefix + "." + name` of the variable.
     *
     * @return
     */
    override fun toString(): String {
        return "${this.prefix}.${this.name}"
    }
}