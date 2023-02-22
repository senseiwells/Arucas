package me.senseiwells.arucas.functions.builtin

import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.extensions.JavaDef
import me.senseiwells.arucas.functions.ArucasFunction
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.ReflectionUtils

/**
 * This class is used to represent a function that has
 * its behaviour defined natively. This is done through
 * the [function] parameter. This will be called when the
 * Function is called in Arucas.
 *
 * You are provided with [Arguments] - this gives you
 * the function that called, the interpreter, and the
 * arguments for the function.
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
 * BuiltInFunction.of("example", 1, { arguments ->
 *     // Logic
 * });
 * ```
 * @param name the name of the function.
 * @param parameters the number of parameters the function has.
 * @param function the function to be invoked when it is called from the interpreter.
 * @param deprecation the deprecation message which will be displayed the first time the function is called.
 * @see Arguments
 */
open class BuiltInFunction protected constructor(
    name: String,
    parameters: Int,
    /**
     * The function to be invoked when it is called from the interpreter.
     */
    val function: (Arguments) -> Any?,
    /**
     * The deprecation message which will be displayed the first time the function is called.
     */
    private var deprecation: String? = null
): ArucasFunction(name, parameters) {
    /**
     * This gets run whenever the function is invoked.
     * It checks whether the function has a deprecation message
     * and logs it to the output if it is not null.
     *
     * @param interpreter the interpreter calling the function.
     */
    protected fun checkDeprecation(interpreter: Interpreter) {
        this.deprecation?.let {
            if (interpreter.properties.logDeprecated) {
                interpreter.api.getOutput().printError(it)
                this.deprecation = null
            }
        }
    }

    /**
     * The method that is invoked when the [ArucasFunction] is called.
     *
     * @param interpreter the interpreter that called the function.
     * @param arguments the arguments passed into the function.
     * @return the [ClassInstance] that the function is returning.
     */
    override fun invoke(interpreter: Interpreter, arguments: List<ClassInstance>): ClassInstance {
        this.checkDeprecation(interpreter)
        return interpreter.convertValue(this.function(Arguments(arguments, interpreter, this)))
    }

    companion object {
        /**
         * Creates an instance of a [BuiltInFunction].
         *
         * @param name the name of the function.
         * @param parameters the number of parameters the function has.
         * @param function the function lambda for when the function is invoked.
         * @param deprecation the deprecation message which will be displayed the first time the function is called.
         */
        @JvmStatic
        @JvmOverloads
        fun of(name: String, parameters: Int, function: (arguments: Arguments) -> Any?, deprecation: String? = null): BuiltInFunction {
            return BuiltInFunction(name, parameters, function, deprecation)
        }

        /**
         * Creates an instance of a [BuiltInFunction] with 0 parameters.
         *
         * @param name the name of the function.
         * @param function the function lambda for when the function is invoked.
         * @param deprecation the deprecation message which will be displayed the first time the function is called.
         */
        @JvmStatic
        @JvmOverloads
        fun of(name: String, function: (arguments: Arguments) -> Any?, deprecation: String? = null): BuiltInFunction {
            return of(name, 0, function, deprecation)
        }

        /**
         * Creates an instance of a [BuiltInFunction] with a variable number of parameters.
         *
         * @param name the name of the function.
         * @param function the function lambda for when the function is invoked.
         * @param deprecation the deprecation message which will be displayed the first time the function is called.
         */
        @JvmStatic
        @JvmOverloads
        fun arb(name: String, function: (arguments: Arguments) -> Any?, deprecation: String? = null): BuiltInFunction {
            return of(name, -1, function, deprecation)
        }

        /**
         * Creates an instance of a [BuiltInFunction] that will call a Java
         * method using reflection.
         *
         * @param clazz the clazz the method belongs in.
         * @param any the instance that is being called upon, this may be null if calling a static method.
         * @param name the name of the method to call.
         */
        @JvmStatic
        fun java(clazz: Class<*>, any: Any?, name: String): BuiltInFunction {
            return arb("\$java.delegate.${clazz.simpleName}.${name}", {
                val def = it.interpreter.getPrimitive(JavaDef::class)
                def.createNullable(ReflectionUtils.callMethod(clazz, any, name, it.arguments, it.api().getObfuscator()))
            })
        }
    }
}