package me.senseiwells.arucas.api.docs.annotations

import me.senseiwells.arucas.classes.PrimitiveDefinition
import kotlin.reflect.KClass

/**
 * This annotation is used to document any
 * parameters used in functions/methods/constructors.
 * It should be used as parameters for [ConstructorDoc]
 * and [FunctionDoc].
 *
 * This information can then be used for generation.
 *
 * @param type the type of the expected parameter.
 * @param name the name of the parameter.
 * @param desc the description of the parameter.
 * @param isVarargs whether the parameter is varargs (accepts arbitrary number of arguments).
 * @param alternativeTypes alternative types for the parameter that may be expected.
 * @see TODO
 */
annotation class ParameterDoc(
    /**
     * The type of the expected parameter.
     */
    val type: KClass<out PrimitiveDefinition<*>>,
    /**
     * The name of the parameter.
     */
    val name: String,
    /**
     * The description of the parameter.
     */
    val desc: Array<String>,
    /**
     * Whether the parameter is varargs (accepts arbitrary number of arguments).
     */
    val isVarargs: Boolean = false,
    /**
     * Alternative types for the parameter that may be expected.
     */
    val alternativeTypes: Array<KClass<out PrimitiveDefinition<*>>> = [],
)
