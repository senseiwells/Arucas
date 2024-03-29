package me.senseiwells.arucas.functions.user

import me.senseiwells.arucas.builtin.ListDef
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.interpreter.Interpreter
import me.senseiwells.arucas.interpreter.StackTable
import me.senseiwells.arucas.nodes.statements.Statement
import me.senseiwells.arucas.typed.ArucasParameter
import me.senseiwells.arucas.typed.LazyDefinitions
import me.senseiwells.arucas.utils.impl.ArucasList

open class UserDefinedClassFunction(
    name: String,
    parameters: List<ArucasParameter>,
    body: Statement,
    localTable: StackTable,
    trace: LocatableTrace,
    private: Boolean,
    returnTypes: LazyDefinitions
): UserDefinedFunction(name, parameters, body, localTable, trace, private, returnTypes) {
    private class Varargs(
        name: String,
        parameters: List<ArucasParameter>,
        body: Statement,
        localTable: StackTable,
        trace: LocatableTrace,
        private: Boolean,
        returnTypes: LazyDefinitions
    ): UserDefinedClassFunction(name, parameters,  body, localTable, trace, private, returnTypes) {
        override val count: Int
            get() = -1

        override fun checkAndPopulate(interpreter: Interpreter, table: StackTable, arguments: List<ClassInstance>) {
            if (arguments.isEmpty()) {
                throw IllegalArgumentException("'this' was not passed into the function")
            }
            val instance = arguments[0]
            val list = ArucasList(arguments.subList(1, arguments.size))
            table.defineVar("this", instance)
            table.defineVar(this.parameters[1].name, interpreter.create(ListDef::class, list))
        }
    }

    companion object {
        fun of(
            arbitrary: Boolean,
            name: String,
            parameters: List<ArucasParameter>,
            body: Statement,
            table: StackTable,
            trace: LocatableTrace,
            returnTypes: LazyDefinitions,
            private: Boolean
        ): UserDefinedClassFunction {
            if (arbitrary) {
                return Varargs(name, parameters, body, table, trace, private, returnTypes)
            }
            return UserDefinedClassFunction(name, parameters, body, table, trace, private, returnTypes)
        }
    }
}