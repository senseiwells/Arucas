package me.senseiwells.arucas.typed

import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.compiler.Trace
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.functions.user.UserDefinedFunction
import me.senseiwells.arucas.interpreter.StackTable

/**
 * Interface providing the ability to lazily store class definitions until needed.
 *
 * This is used for type hints so that they are able to forward reference classes.
 *
 * @see ArucasParameter
 * @see ArucasVariable
 * @see UserDefinedFunction
 */
interface LazyDefinitions {
    /**
     * Gets the list of class definitions.
     *
     * @return the list of class definitions.
     */
    fun get(): List<ClassDefinition>

    /**
     * An implementation of [LazyDefinitions] that stores the
     * names of the classes as well as the local table to retrieve
     * the classes from.
     *
     * To create this implementation you can use the [of] function.
     */
    private class Impl(
        private val types: Array<String>?,
        private val local: StackTable,
        private val trace: Trace
    ): LazyDefinitions {
        private val definitions = ArrayList<ClassDefinition>()
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

        /**
         * Gets the empty implementation of [LazyDefinitions].
         *
         * @return empty lazy definitions.
         */
        fun of(): LazyDefinitions {
            return this.empty
        }

        /**
         * Stores the names of the given classes to lazily evaluate
         * them as and when needed.
         *
         * @param types the array of names.
         * @param local the local table to access the classes in.
         * @param trace the trace location.
         * @return the lazy definitions.
         */
        fun of(types: Array<String>?, local: StackTable, trace: Trace): LazyDefinitions {
            return Impl(types, local, trace)
        }
    }
}