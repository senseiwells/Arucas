package me.senseiwells.arucas.functions.user

import me.senseiwells.arucas.builtin.ListDef
import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.Propagator
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.functions.ArucasFunction
import me.senseiwells.arucas.nodes.statements.Statement
import me.senseiwells.arucas.utils.*
import me.senseiwells.arucas.utils.impl.ArucasList

open class UserDefinedFunction(
    name: String,
    parameters: List<ParameterTyped>,
    body: Statement,
    localTable: StackTable,
    trace: LocatableTrace,
    private val returnTypes: Array<ClassDefinition>?
): UserFunction(name, parameters, body, localTable, trace) {
    /**
     * The method that is invoked when the [ArucasFunction] is called.
     * This pushes the interpreter's current stack table to the captured
     * table of the function - to ensure the correct context.
     *
     * It populates the arguments into the stack table for the body
     * of the function to work.
     *
     * @param interpreter the interpreter that called the function.
     * @param arguments the arguments passed into the function.
     * @return the [ClassInstance] that the function is returning.
     */
    final override fun invoke(interpreter: Interpreter, arguments: List<ClassInstance>): ClassInstance {
        val localTable = StackTable(interpreter.modules, this.localTable)
        this.checkAndPopulate(interpreter, localTable, arguments)
        var returnValue = interpreter.getNull()
        try {
            interpreter.execute(localTable, this.body)
        } catch (returnPropagator: Propagator.Return) {
            returnValue = returnPropagator.returnValue
        }
        this.returnTypes?.let {
            if (!returnValue.definition.inheritsFrom(it.toList())) {
                runtimeError("Function ${this.name} got ${returnValue.definition.name} for return but expected ${Parameter.definitionsAsString(it)}", this.trace)
            }
        }
        return returnValue
    }

    private class Varargs(
        name: String,
        parameters: List<ParameterTyped>,
        body: Statement,
        localTable: StackTable,
        trace: LocatableTrace,
        returnTypes: Array<ClassDefinition>?,
    ): UserDefinedFunction(name, parameters, body, localTable, trace, returnTypes) {
        override val count: Int
            get() = -1

        override fun checkAndPopulate(interpreter: Interpreter, table: StackTable, arguments: List<ClassInstance>) {
            val list = ArucasList(arguments)
            table.defineVar(this.parameters[0].name, interpreter.create(ListDef::class, list))
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
        ): UserDefinedFunction {
            if (arbitrary) {
                return Varargs(name, parameters, body, table, trace, returnTypes)
            }
            return UserDefinedFunction(name, parameters, body, table, trace, returnTypes)
        }
    }
}