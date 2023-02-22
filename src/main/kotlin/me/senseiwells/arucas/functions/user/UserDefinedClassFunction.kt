package me.senseiwells.arucas.functions.user

import me.senseiwells.arucas.builtin.ListDef
import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.nodes.Statement
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.ParameterTyped
import me.senseiwells.arucas.utils.StackTable
import me.senseiwells.arucas.utils.impl.ArucasList

open class UserDefinedClassFunction(
    name: String,
    parameters: List<ParameterTyped>,
    body: Statement,
    localTable: StackTable,
    trace: LocatableTrace,
    returnTypes: Array<ClassDefinition>?
): UserDefinedFunction(name, parameters, body, localTable, trace, returnTypes) {
    private class Varargs(
        name: String,
        parameters: List<ParameterTyped>,
        body: Statement,
        localTable: StackTable,
        trace: LocatableTrace,
        returnTypes: Array<ClassDefinition>?,
    ): UserDefinedClassFunction(name, parameters,  body, localTable, trace, returnTypes) {
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
            parameters: List<ParameterTyped>,
            body: Statement,
            table: StackTable,
            trace: LocatableTrace,
            returnTypes: Array<ClassDefinition>?
        ): UserDefinedClassFunction {
            if (arbitrary) {
                return Varargs(name, parameters, body, table, trace, returnTypes)
            }
            return UserDefinedClassFunction(name, parameters, body, table, trace, returnTypes)
        }
    }
}