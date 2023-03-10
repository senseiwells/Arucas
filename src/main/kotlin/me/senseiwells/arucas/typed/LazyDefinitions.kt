package me.senseiwells.arucas.typed

import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.compiler.Trace
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.interpreter.StackTable

interface LazyDefinitions {
    fun get(): List<ClassDefinition>

    private class Impl(
        private val types: Array<String>?,
        private val local: StackTable,
        private val trace: Trace
    ): LazyDefinitions {
        private var definitions = ArrayList<ClassDefinition>()
        private var initialised = false

        override fun get(): List<ClassDefinition> {
            if (!this.initialised) {
                this.initialised = true
                if (this.types != null) {
                    for (type in this.types) {
                        this.definitions.add(this.local.getClass(type) ?: runtimeError("No such class $type", this.trace))
                    }
                }
            }
            return definitions
        }
    }

    companion object {
        private val empty = object: LazyDefinitions {
            override fun get(): List<ClassDefinition> = emptyList()
        }

        fun of(): LazyDefinitions {
            return this.empty
        }

        fun of(types: Array<String>?, local: StackTable, trace: Trace): LazyDefinitions {
            return Impl(types, local, trace)
        }
    }
}