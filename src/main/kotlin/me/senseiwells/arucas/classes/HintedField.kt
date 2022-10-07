package me.senseiwells.arucas.classes

import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.Parameter
import me.senseiwells.arucas.utils.Trace

class HintedField(
    private val name: String,
    private val definitions: Array<ClassDefinition>?,
    private val assignable: Boolean,
    instance: ClassInstance
) {
    companion object {
        fun of(name: String, instance: ClassInstance, assignable: Boolean = false): HintedField {
            return HintedField(name, null, assignable, instance)
        }
    }

    var instance: ClassInstance = instance
        private set

    fun set(instance: ClassInstance, trace: Trace) {
        if (!this.assignable) {
            runtimeError("Cannot reassign '${this.name}'", trace)
        }

        if (this.definitions != null && !this.checkHinted(this.definitions, instance)) {
            runtimeError("Hinted type for '${this.name}' got '${instance.definition.name}' but expected '${this.definitionsAsString(this.definitions)}'", trace)
        }
        this.instance = instance
    }

    fun checkInstanceType(trace: Trace) {
        if (this.definitions != null && !this.checkHinted(this.definitions, this.instance)) {
            runtimeError("Hinted type for '${this.name}' was constructed with '${this.instance.definition.name}' but expected '${this.definitionsAsString(this.definitions)}'", trace)
        }
    }

    private fun checkHinted(definitions: Array<ClassDefinition>, instance: ClassInstance): Boolean {
        for (definition in definitions) {
            if (instance.isOf(definition)) {
                return true
            }
        }
        return false
    }

    private fun definitionsAsString(definitions: Array<ClassDefinition>) = Parameter.definitionsAsString(definitions)
}