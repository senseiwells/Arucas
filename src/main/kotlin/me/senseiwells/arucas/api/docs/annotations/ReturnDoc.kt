package me.senseiwells.arucas.api.docs.annotations

import me.senseiwells.arucas.classes.PrimitiveDefinition
import kotlin.reflect.KClass

annotation class ReturnDoc(
    val type: KClass<out PrimitiveDefinition<*>>,
    val desc: Array<String>
)
