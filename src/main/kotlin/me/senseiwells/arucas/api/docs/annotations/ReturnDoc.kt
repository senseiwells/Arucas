package me.senseiwells.arucas.api.docs.annotations

import me.senseiwells.arucas.classes.PrimitiveDefinition
import kotlin.reflect.KClass

/**
 * This annotation is used to document any
 * return values from functions/methods.
 * It should be used as parameters for [FunctionDoc].
 *
 * This information can then be used for generation.
 *
 * @param type the type of the return value.
 * @param desc description of the return value.
 * @see me.senseiwells.arucas.api.docs.visitor.ReturnDoc
 */
annotation class ReturnDoc(
    /**
     * The type of the return value.
     */
    val type: KClass<out PrimitiveDefinition<*>>,
    /**
     * Description of the return value.
     */
    val desc: Array<String>
)
