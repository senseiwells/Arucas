package me.senseiwells.arucas.api.docs.annotations

import me.senseiwells.arucas.classes.PrimitiveDefinition
import kotlin.reflect.KClass

annotation class ParameterDoc(
    val type: KClass<out PrimitiveDefinition<*>>,
    val name: String,
    val desc: Array<String>,
    val isVarargs: Boolean = false,
    val alternativeTypes: Array<KClass<out PrimitiveDefinition<*>>> = [],
)
