package me.senseiwells.arucas.typed

import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.StackTable
import me.senseiwells.arucas.utils.Trace

/**
 * Class representing a variable or field in Arucas.
 *
 * It provides the ability to set the variable as read-only
 * or private to limit visibility in classes.
 *
 * It also enforces type hints at runtime and will throw
 * an error if assigning an object that is not of the correct
 * type.
 *
 * @param name the name of the variable.
 * @param instance the instance to set the variable with.
 * @param prefix the prefix name of the variable.
 * @param readonly whether the variable is read-only.
 * @param local the local table where the variable was defined.
 * This should be specified if you want your variable to be private, otherwise null.
 * @param definitions the lazy type hints.
 * @see HintedVariable
 */
class ArucasVariable(
    /**
     * The name of the variable.
     */
    val name: String,
    /**
     * The instance to set the variable with.
     */
    private var instance: ClassInstance,
    /**
     * The prefix name of the variable.
     */
    private val prefix: String,
    /**
     * Whether the variable is read-only.
     */
    private val readonly: Boolean,
    /**
     * The local table where the variable was defined.
     * This should be specified if you want your variable to be private, otherwise null.
     */
    private val local: StackTable? = null,
    /**
     * The lazy type hints.
     */
    private val definitions: LazyDefinitions = LazyDefinitions.of()
) {
    /**
     * This stored whether the variable is locked and can no longer be modified.
     */
    private var locked = false

    /**
     * This sets the [ClassInstance] that the variable holds.
     *
     * It will only be set if the variable is not read-only
     * (and being assigned after being locked), if the
     * interpreter has private access to the variable, and
     * if the instance matches the type hints.
     *
     * @param instance the [ClassInstance] to set the varible to.
     * @param interpreter the interpreter setting the variable.
     * @param trace the location that this is being called from.
     */
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

    /**
     * This gets the [ClassInstance] that the variable holds.
     *
     * This will only be successful if the interpreter has access
     * to the variable (if the variable is marked as private).
     *
     * @param interpreter the interpreter getting the variable.
     * @param trace the location that his is being called from.
     * @return the [ClassInstance] that the variable holds.
     */
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
     * @return the name of the variable.
     */
    override fun toString(): String {
        return "${this.prefix}.${this.name}"
    }
}