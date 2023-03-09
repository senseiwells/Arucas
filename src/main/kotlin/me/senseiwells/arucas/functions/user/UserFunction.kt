package me.senseiwells.arucas.functions.user

import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.functions.ArucasFunction
import me.senseiwells.arucas.nodes.statements.Statement
import me.senseiwells.arucas.typed.ArucasParameter
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.StackTable

/**
 * This class represents a function that is defined by the user
 * in Arucas - not natively implemented. This implementation works
 * much closer to the interpreter as it moves context and defines
 * variables in the scope.
 *
 * This is not intended for API use and should for the most part
 * be disregarded from outside the Arucas project.
 *
 * This implementation provides the name of the function as well
 * as the expected parameters (and their types) which are enforced
 * at runtime - otherwise an error is thrown. It keeps a reference
 * to the [StackTable] at which it was defined in - to be able to
 * jump back to the table when executed, however this needs to be
 * implemented in the subclass.
 *
 * @param name the name of the function.
 * @param parameters the named and typed parameters of the function.
 * @param body the body of the function which will be executed when invoked.
 * @param localTable the local [StackTable] from where the function was defined.
 * @param trace the trace location at which the function was defined at.
 * @param private whether the function is private.
 */
abstract class UserFunction(
    name: String,
    /**
     * The named and typed parameters of the function.
     */
    val parameters: List<ArucasParameter>,
    /**
     * The body of the function which will be executed when invoked.
     */
    val body: Statement,
    /**
     * The local [StackTable] from where the function was defined.
     */
    val localTable: StackTable,
    /**
     * The trace location at which the function was defined at.
     */
    val trace: LocatableTrace,
    /**
     * Whether the function is private.
     */
    val private: Boolean
): ArucasFunction(name, parameters.size) {
    /**
     * This should be invoked in [invoke] method to check the number of
     * arguments passed into the function is correct as well as populate
     * the arguments into the local stack table.
     *
     * The stack table should be a copy of the existing table, and this
     * is the table to execute the [body] on.
     *
     * This should be overridden by vararg function implementations to
     * remove the [count] check and to also
     *
     * @param interpreter the interpreter that called the function.
     * @param table the table to populate.
     * @param arguments the arguments being passed into the function.
     */
    protected open fun checkAndPopulate(interpreter: Interpreter, table: StackTable, arguments: List<ClassInstance>) {
        if (this.count != arguments.size) {
            runtimeError("Incorrect number of parameters for function '${this.name}', expected '${this.count}' got '${arguments.size}'", this.trace)
        }

        for (i in 0 until this.count) {
            val parameter = this.parameters[i]
            val argument = arguments[i]

            if (!argument.definition.inheritsFrom(parameter.getTypes())) {
                runtimeError(this.incorrectType(i, argument, parameter), this.trace)
            }

            table.defineVar(parameter.name, argument)
        }
    }

    /**
     * The method that is invoked when the [UserFunction] is called.
     *
     * @param interpreter the interpreter that called the function.
     * @param arguments the arguments passed into the function.
     * @return the [ClassInstance] that the function is returning.
     */
    abstract override fun invoke(interpreter: Interpreter, arguments: List<ClassInstance>): ClassInstance

    /**
     * Checks whether the function is accessible - this is used for private functions.
     *
     * @param interpreter the interpreter wanting to access the function.
     * @return whether the function is accessible.
     */
    override fun accessible(interpreter: Interpreter): Boolean {
        return !this.private || interpreter.isWithinStack(this.localTable)
    }

    /**
     * Function that creates a string for an incorrect type error.
     *
     * @param index the index of the incorrect parameter.
     * @param got the given [ClassInstance].
     * @param expected the expected parameter.
     * @return the error string.
     */
    private fun incorrectType(index: Int, got: ClassInstance, expected: ArucasParameter): String {
        return "Function ${this.name} got '${got.definition.name}' for parameter ${index + 1} but expected ${expected.getTypes()}"
    }
}