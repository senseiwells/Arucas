package me.senseiwells.arucas.functions.user

import me.senseiwells.arucas.builtin.ListDef
import me.senseiwells.arucas.classes.ArucasClassDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.functions.ArucasFunction
import me.senseiwells.arucas.nodes.statements.Statement
import me.senseiwells.arucas.utils.*
import me.senseiwells.arucas.utils.impl.ArucasList

open class UserConstructorFunction(
    val definition: ArucasClassDefinition,
    private val constructorInit: ConstructorInit,
    parameters: List<ParameterTyped>,
    body: Statement,
    localTable: StackTable,
    trace: LocatableTrace
): UserFunction("", parameters, body, localTable, trace) {
    /**
     * The constructor that is invoked when the [ArucasFunction] is called.
     * This pushes the interpreter's current stack table to the captured
     * table of the constructor - to ensure the correct context.
     *
     * It populates the arguments into the stack table for the body
     * of the constructor to work.
     *
     * This also calls any this/super constructors that were declared.
     *
     * @param interpreter the interpreter that called the function.
     * @param arguments the arguments passed into the function.
     * @return the [ClassInstance] that the function is returning.
     */
    final override fun invoke(interpreter: Interpreter, arguments: List<ClassInstance>): ClassInstance {
        val localTable = StackTable(interpreter.modules, this.localTable)
        val instance = arguments[0]
        this.checkAndPopulate(interpreter, localTable, arguments)
        val definition = when (this.constructorInit.type) {
            ConstructorInit.InitType.SUPER -> this.definition.superclass()
            ConstructorInit.InitType.THIS -> this.definition
            else -> null
        }
        definition?.let {
            val initArgs = ArrayList<ClassInstance>()
            for (expression in this.constructorInit.arguments) {
                initArgs.add(interpreter.evaluate(localTable, expression))
            }
            it.init(interpreter, instance, initArgs, CallTrace(this.trace, "init ${definition.name}::${initArgs.size}"))
        }
        interpreter.execute(localTable, this.body)
        return instance
    }

    private class Varargs(
        definition: ArucasClassDefinition,
        constructorInit: ConstructorInit,
        parameters: List<ParameterTyped>,
        body: Statement,
        localTable: StackTable,
        trace: LocatableTrace
    ): UserConstructorFunction(definition, constructorInit, parameters, body, localTable, trace) {
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
            definition: ArucasClassDefinition,
            constructorInit: ConstructorInit,
            parameters: List<ParameterTyped>,
            body: Statement,
            table: StackTable,
            trace: LocatableTrace
        ): UserConstructorFunction {
            if (arbitrary) {
                return Varargs(definition, constructorInit, parameters, body, table, trace)
            }
            return UserConstructorFunction(definition, constructorInit, parameters, body, table, trace)
        }
    }
}