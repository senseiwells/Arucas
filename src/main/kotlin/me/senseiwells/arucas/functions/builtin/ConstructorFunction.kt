package me.senseiwells.arucas.functions.builtin

import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.functions.ArucasFunction
import me.senseiwells.arucas.functions.builtin.ConstructorFunction.Companion.arb
import me.senseiwells.arucas.functions.builtin.ConstructorFunction.Companion.of
import me.senseiwells.arucas.interpreter.Interpreter

/**
 * This class represents a built-in constructor used in [PrimitiveDefinition]s.
 * Much like [MemberFunction] it passes in the instance as the first parameter,
 * this is the instance that is being currently instructed and the constructor
 * should be setting the primitive for it:
 *
 * ```kotlin
 * ConstructorFunction.of({ arguments ->
 *     val instance = arguments.next()
 *     instance.setPrimitive(this, /* primitive */)
 * })
 * ```
 *
 * You are provided with [Arguments] - this gives you
 * the constructor that called, the interpreter, and the
 * arguments for the function.
 *
 * [ConstructorFunction]s may be deprecated, if they are
 * and the interpreter is logging deprecated then
 * a message will be printed before the function is called.
 *
 * You can create instances of the [ConstructorFunction] using the helper
 * methods [of] and [arb] to make regular constructors and vararg
 * constructors respectively.
 *
 * @param parameters the number of parameters the constructor has.
 * @param function the function to be invoked when the constructor is invoked.
 * @param deprecation the deprecation message which will be displayed the first time the function is called.
 * @see Arguments
 * @see MemberFunction
 * @see BuiltInFunction
 */
class ConstructorFunction private constructor(
    parameters: Int,
    function: (Arguments) -> Unit,
    deprecation: String?
): MemberFunction("", parameters, function, deprecation) {
    /**
     * The method that is invoked when the [ArucasFunction] is called.
     * Because this is a constructor it does not return anything.
     *
     * @param interpreter the interpreter that called the function.
     * @param arguments the arguments passed into the function.
     * @return the [ClassInstance] that the function is returning.
     */
    override fun invoke(interpreter: Interpreter, arguments: List<ClassInstance>): ClassInstance {
        this.checkDeprecation(interpreter)
        this.function(Arguments.Member(arguments, interpreter, this))
        return interpreter.getNull()
    }

    companion object {
        /**
         * Creates an instance of a [ConstructorFunction].
         *
         * @param parameters the number of parameters the function has (excluding `this`).
         * @param function the function lambda for when the constructor is invoked.
         * @param deprecation the deprecation message which will be displayed the first time the constructor is called.
         */
        @JvmStatic
        @JvmOverloads
        fun of(parameters: Int, function: (arguments: Arguments) -> Unit, deprecation: String? = null): ConstructorFunction {
            return ConstructorFunction(parameters + 1, function, deprecation)
        }

        /**
         * Creates an instance of a [ConstructorFunction] with 0 parameters.
         *
         * @param function the function lambda for when the constructor is invoked.
         * @param deprecation the deprecation message which will be displayed the first time the constructor is called.
         */
        @JvmStatic
        @JvmOverloads
        fun of(function: (arguments: Arguments) -> Unit, deprecation: String? = null): ConstructorFunction {
            return ConstructorFunction(1, function, deprecation)
        }

        /**
         * Creates an instance of a [ConstructorFunction] with a variable number of parameters.
         *
         * @param function the function lambda for when the constructor is invoked.
         * @param deprecation the deprecation message which will be displayed the first time the constructor is called.
         */
        @JvmStatic
        @JvmOverloads
        fun arb(function: (arguments: Arguments) -> Unit, deprecation: String? = null): ConstructorFunction {
            return ConstructorFunction(-1, function, deprecation)
        }
    }
}