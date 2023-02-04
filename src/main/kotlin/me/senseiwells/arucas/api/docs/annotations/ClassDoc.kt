package me.senseiwells.arucas.api.docs.annotations

import me.senseiwells.arucas.builtin.ObjectDef
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.utils.Util
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class ClassDoc(
    val name: String,
    val desc: Array<String>,
    val importPath: String = "",
    val superclass: KClass<out PrimitiveDefinition<*>> = ObjectDef::class,
    val language: Util.Language = Util.Language.Kotlin
)
