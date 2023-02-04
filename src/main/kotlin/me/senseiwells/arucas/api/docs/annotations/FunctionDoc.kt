package me.senseiwells.arucas.api.docs.annotations

import me.senseiwells.arucas.builtin.NullDef

@Target(AnnotationTarget.FUNCTION)
annotation class FunctionDoc(
    val isStatic: Boolean = false,
    val deprecated: Array<String> = [],
    val name: String,
    val desc: Array<String>,
    val params: Array<ParameterDoc> = [],
    val returns: ReturnDoc = ReturnDoc(NullDef::class, []),
    val examples: Array<String>
)