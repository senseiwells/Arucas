package me.senseiwells.arucas.utils

import me.senseiwells.arucas.builtin.ListDef
import me.senseiwells.arucas.classes.ArucasClassDefinition
import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.Propagator
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.nodes.Statement
import me.senseiwells.arucas.utils.impl.ArucasList

abstract class ArucasFunction(
    val name: String,
    open val count: Int
): (Interpreter, List<ClassInstance>) -> ClassInstance

abstract class UserFunction(
    name: String,
    val parameters: List<ParameterTyped>,
    val body: Statement,
    val localTable: StackTable,
    val trace: LocatableTrace
): ArucasFunction(name, parameters.size) {
    protected open fun checkAndPopulate(interpreter: Interpreter, table: StackTable, arguments: List<ClassInstance>) {
        if (this.count != arguments.size) {
            runtimeError("Incorrect number of parameters for function '${this.name}', expected '${this.count}' got '${arguments.size}'", this.trace)
        }

        for (i in 0 until this.count) {
            val parameter = this.parameters[i]
            val argument = arguments[i]

            parameter.types?.let {
                if (!argument.definition.inheritsFrom(*it)) {
                    runtimeError(this.incorrectType(i, argument, parameter), this.trace)
                }
            }

            table.defineVar(parameter.name, argument)
        }
    }

    protected open fun incorrectType(index: Int, got: ClassInstance, expected: ParameterTyped): String {
        return "Function ${this.name} got '${got.definition.name}' for parameter ${index + 1} but expected ${expected.definitionsAsString()}"
    }
}

open class UserDefinedFunction(
    name: String,
    parameters: List<ParameterTyped>,
    body: Statement,
    localTable: StackTable,
    trace: LocatableTrace,
    private val returnTypes: Array<ClassDefinition>?
): UserFunction(name, parameters, body, localTable, trace) {
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
            return (if (arbitrary) ::Arbitrary else ::UserDefinedFunction)(name, parameters, body, table, trace, returnTypes)
        }
    }

    final override fun invoke(interpreter: Interpreter, arguments: List<ClassInstance>): ClassInstance {
        val localTable = StackTable(this.localTable)
        this.checkAndPopulate(interpreter, localTable, arguments)
        var returnValue = interpreter.getNull()
        try {
            interpreter.execute(localTable, this.body)
        } catch (returnPropagator: Propagator.Return) {
            returnValue = returnPropagator.returnValue
        }
        this.returnTypes?.let {
            if (!returnValue.definition.inheritsFrom(*it)) {
                runtimeError("Function ${this.name} got ${returnValue.definition.name} for return but expected ${Parameter.definitionsAsString(it)}", this.trace)
            }
        }
        return returnValue
    }

    class Arbitrary(
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
}

open class UserDefinedClassFunction(
    name: String,
    parameters: List<ParameterTyped>,
    body: Statement,
    localTable: StackTable,
    trace: LocatableTrace,
    returnTypes: Array<ClassDefinition>?
): UserDefinedFunction(name, parameters, body, localTable, trace, returnTypes) {
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
            return (if (arbitrary) UserDefinedClassFunction::Arbitrary else ::UserDefinedClassFunction)(name, parameters, body, table, trace, returnTypes)
        }
    }

    private class Arbitrary(
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
}

open class UserConstructorFunction(
    val definition: ArucasClassDefinition,
    private val constructorInit: ConstructorInit,
    parameters: List<ParameterTyped>,
    body: Statement,
    localTable: StackTable,
    trace: LocatableTrace
): UserFunction("", parameters, body, localTable, trace) {
    companion object {
        fun of(
            arbitrary: Boolean,
            definition: ArucasClassDefinition,
            constructorInit: ConstructorInit,
            parameters: List<ParameterTyped>,
            body: Statement,
            localTable: StackTable,
            trace: LocatableTrace
        ): UserConstructorFunction {
            return (if (arbitrary) UserConstructorFunction::Arbitrary else ::UserConstructorFunction)(definition, constructorInit, parameters, body, localTable, trace)
        }
    }

    final override fun invoke(interpreter: Interpreter, arguments: List<ClassInstance>): ClassInstance {
        val localTable = StackTable(this.localTable)
        val instance = arguments[0]
        this.checkAndPopulate(interpreter, localTable, arguments)
        val definition = when (this.constructorInit.type) {
            ConstructorInit.InitType.SUPER -> instance.definition.superclass()
            ConstructorInit.InitType.THIS -> instance.definition
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

    class Arbitrary(
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
}

sealed class BuiltInFunction(
    name: String,
    parameters: Int,
    private var deprecation: String? = null
): ArucasFunction(name, parameters) {
    companion object {
        @JvmStatic
        @JvmOverloads
        fun of(
            name: String,
            parameters: Int,
            function: (arguments: Arguments) -> Any?,
            deprecation: String? = null
        ): BuiltInFunction {
            return Auto(name, parameters, function, deprecation)
        }

        @JvmStatic
        @JvmOverloads
        fun of(
            name: String,
            function: (arguments: Arguments) -> Any?,
            deprecation: String? = null
        ): BuiltInFunction {
            return Auto(name, 0, function, deprecation)
        }

        @JvmStatic
        @JvmOverloads
        fun arb(
            name: String,
            function: (arguments: Arguments) -> Any?,
            deprecation: String? = null
        ): BuiltInFunction {
            return this.of(name, -1, function, deprecation)
        }
    }

    protected fun checkDeprecation(interpreter: Interpreter) {
        this.deprecation?.let {
            if (interpreter.properties.logDeprecated) {
                interpreter.api.getOutput().printError(it)
                this.deprecation = null
            }
        }
    }

    private class Auto(
        name: String,
        parameters: Int,
        val function: (Arguments) -> Any?,
        deprecation: String? = null
    ): BuiltInFunction(name, parameters, deprecation) {
        override fun invoke(interpreter: Interpreter, arguments: List<ClassInstance>): ClassInstance {
            this.checkDeprecation(interpreter)
            return interpreter.convertValue(this.function(Arguments(arguments, interpreter, this)))
        }
    }
}

sealed class MemberFunction(
    name: String,
    parameters: Int,
    deprecation: String?
): BuiltInFunction(name, parameters, deprecation) {
    companion object {
        @JvmStatic
        @JvmOverloads
        fun of(
            name: String,
            parameters: Int,
            function: (arguments: Arguments) -> Any?,
            deprecation: String? = null
        ): MemberFunction {
            return Auto(name, parameters + 1, function, deprecation)
        }

        @JvmStatic
        @JvmOverloads
        fun of(
            name: String,
            function: (arguments: Arguments) -> Any?,
            deprecation: String? = null
        ): MemberFunction {
            return Auto(name, 1, function, deprecation)
        }

        @JvmStatic
        @JvmOverloads
        fun arb(
            name: String,
            function: (arguments: Arguments) -> Any?,
            deprecation: String? = null
        ): MemberFunction {
            return Auto(name, -1, function, deprecation)
        }
    }

    private class Auto(
        name: String,
        parameters: Int,
        val function: (Arguments) -> Any?,
        deprecation: String? = null
    ): MemberFunction(name, parameters, deprecation) {
        override fun invoke(interpreter: Interpreter, arguments: List<ClassInstance>): ClassInstance {
            this.checkDeprecation(interpreter)
            return interpreter.convertValue(this.function(Arguments.Member(arguments, interpreter, this)))
        }
    }
}

sealed class ConstructorFunction(
    parameters: Int,
    deprecation: String?
): MemberFunction("", parameters, deprecation) {
    companion object {
        @JvmStatic
        @JvmOverloads
        fun of(
            parameters: Int,
            function: (arguments: Arguments) -> Unit,
            deprecation: String? = null
        ): ConstructorFunction {
            return NonReturnable(parameters + 1, function, deprecation)
        }

        @JvmStatic
        @JvmOverloads
        fun of(
            function: (arguments: Arguments) -> Unit,
            deprecation: String? = null
        ): ConstructorFunction {
            return NonReturnable(1, function, deprecation)
        }

        @JvmStatic
        @JvmOverloads
        fun arb(
            function: (arguments: Arguments) -> Unit,
            deprecation: String? = null
        ): ConstructorFunction {
            return NonReturnable(-1, function, deprecation)
        }
    }

    private class NonReturnable(
        parameters: Int,
        val function: (Arguments) -> Unit,
        deprecation: String? = null
    ): ConstructorFunction(parameters, deprecation) {
        override fun invoke(interpreter: Interpreter, arguments: List<ClassInstance>): ClassInstance {
            this.checkDeprecation(interpreter)
            this.function(Arguments.Member(arguments, interpreter, this))
            return interpreter.getNull()
        }
    }
}