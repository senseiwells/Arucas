package me.senseiwells.arucas.functions.builtin

import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.functions.ArucasFunction
import me.senseiwells.arucas.functions.builtin.MemberFunction.Companion.arb
import me.senseiwells.arucas.functions.builtin.MemberFunction.Companion.of
import me.senseiwells.arucas.interpreter.Interpreter

/**
 * This class represents a function that is a member of a class. It is used
 * to define the method in [PrimitiveDefinition].
 *
 * You are provided with [Arguments] - this gives you
 * the function that called, the interpreter, and the
 * arguments for the function. The first argument will
 * be the class instance that called the method and
 * subsequent arguments would be the rest of the arguments
 * passed in.
 *
 * [BuiltInFunction]s may be deprecated, if they are
 * and the interpreter is logging deprecated then
 * a message will be printed before the function is called.
 *
 * To be able to construct [BuiltInFunction]s you can use the
 * helper functions [of] and [arb] to create regular functions
 * and vararg functions respectively:
 *
 * ```kotlin
 * MemberFunction.of("example", { arguments ->
 *     // Assuming we are creating this in a PrimitiveDefinition
 *     val instance = arguments.nextPrimitive(this)
 *     // Logic
 * })
 * ```
 *
 * @param name the name of the function.
 * @param parameters the number of parameters the function has.
 * @param function the function to be invoked when it is called from the interpreter.
 * @param deprecation the deprecation message which will be displayed the first time the function is called.
 * @see Arguments
 * @see BuiltInFunction
 */
open class MemberFunction protected constructor(
    name: String,
    parameters: Int,
    function: (Arguments) -> Any?,
    deprecation: String?
): BuiltInFunction(name, parameters, function, deprecation) {
    /**
     * The method that is invoked when the [ArucasFunction] is called.
     *
     * @param interpreter the interpreter that called the function.
     * @param arguments the arguments passed into the function.
     * @return the [ClassInstance] that the function is returning.
     */
    override fun invoke(interpreter: Interpreter, arguments: List<ClassInstance>): ClassInstance {
        this.checkDeprecation(interpreter)
        return interpreter.convertValue(this.function(Arguments.Member(arguments, interpreter, this)))
    }

    companion object {
        /**
         * Creates an instance of [MemberFunction].
         *
         * @param name the name of the function.
         * @param parameters the number of parameters the function has (excluding `this`).
         * @param function the function lambda for when the function is invoked.
         * @param deprecation the deprecation message which will be displayed the first time the function is called.
         */
        @JvmStatic
        @JvmOverloads
        fun of(name: String, parameters: Int, function: (arguments: Arguments) -> Any?, deprecation: String? = null): MemberFunction {
            return MemberFunction(name, parameters + 1, function, deprecation)
        }

        /**
         * Creates an instance of [MemberFunction] with 0 parameters.
         *
         * @param name the name of the function.
         * @param function the function lambda for when the function is invoked.
         * @param deprecation the deprecation message which will be displayed the first time the function is called.
         */
        @JvmStatic
        @JvmOverloads
        fun of(name: String, function: (arguments: Arguments) -> Any?, deprecation: String? = null): MemberFunction {
            return MemberFunction(name, 1, function, deprecation)
        }

        /**
         * Creates an instance of [MemberFunction] with a variable number of parameters.
         *
         * @param name the name of the function.
         * @param function the function lambda for when the function is invoked.
         * @param deprecation the deprecation message which will be displayed the first time the function is called.
         */
        @JvmStatic
        @JvmOverloads
        fun arb(name: String, function: (arguments: Arguments) -> Any?, deprecation: String? = null): MemberFunction {
            return MemberFunction(name, -1, function, deprecation)
        }
    }
}