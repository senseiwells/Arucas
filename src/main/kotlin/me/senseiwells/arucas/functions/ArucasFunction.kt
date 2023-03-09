package me.senseiwells.arucas.functions

import me.senseiwells.arucas.builtin.FunctionDef
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.FunctionMap

/**
 * This class represents all functions in Arucas.
 * This provides an [invoke] method for when the interpreter
 * calls the function.
 *
 * Each function is defined by the name and the number
 * of parameters that it has. This is how different functions
 * are identified from each other, see [FunctionMap].
 *
 * [ArucasFunction]s are usually wrapped in [FunctionDef]
 * instances in arucas which can then be called through
 * [Interpreter.call].
 *
 * @param name the name of the function.
 * @param count the number of parameters the function has, this may be `-1` for varargs.
 */
abstract class ArucasFunction(
    /**
     * The name of the function.
     */
    val name: String,
    /**
     * The number of parameters the function has, this may be `-1` for varargs.
     */
    open val count: Int
): (Interpreter, List<ClassInstance>) -> ClassInstance {
    /**
     * Whether the function is private.
     */
    open val private: Boolean = false

    /**
     * The method that is invoked when the [ArucasFunction] is called.
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
    open fun accessible(interpreter: Interpreter): Boolean {
        return !this.private
    }
}