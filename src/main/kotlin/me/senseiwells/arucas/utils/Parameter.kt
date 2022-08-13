package me.senseiwells.arucas.utils

import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.exceptions.runtimeError

class Parameter(val name: String, var typeNames: Array<String>? = null) {
    companion object {
        fun definitionsAsString(types: Collection<ClassDefinition>) = types.joinToString(" | ") { it.name }

        fun definitionsAsString(types: Array<ClassDefinition>) = types.joinToString(" | ") { it.name }

        fun namesToDefinitions(table: StackTable, names: Array<String>?, trace: Trace): Array<ClassDefinition>? {
            return names?.let { Array(it.size) { i -> table.getClass(it[i]) ?: runtimeError("No such class ${it[i]}", trace) } }
        }
    }

    fun definitions(table: StackTable, trace: Trace): Array<ClassDefinition>? {
        return namesToDefinitions(table, this.typeNames, trace)
    }

    fun toTyped(table: StackTable, trace: Trace): ParameterTyped {
        return ParameterTyped(this.name, this.definitions(table, trace))
    }

    override fun equals(other: Any?): Boolean {
        return other is Parameter && this.name == other.name
    }

    override fun hashCode(): Int {
        return this.name.hashCode()
    }
}

class ParameterTyped(val name: String, val types: Array<ClassDefinition>?) {
    fun definitionsAsString() = this.types!!.let { Parameter.definitionsAsString(it) }
}