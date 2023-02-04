package me.senseiwells.arucas.api.docs.annotations

import me.senseiwells.arucas.classes.PrimitiveDefinition
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
annotation class FieldDoc(
    val isStatic: Boolean = true,
    val name: String,
    val desc: Array<String>,
    val type: KClass<out PrimitiveDefinition<*>>,
    val assignable: Boolean = false,
    val examples: Array<String>
)