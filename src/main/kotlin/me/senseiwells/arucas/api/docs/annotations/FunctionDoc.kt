package me.senseiwells.arucas.api.docs.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class FunctionDoc(
    val isStatic: Boolean = false,
    val deprecated: Array<String> = [],
    val name: String,
    val desc: Array<String>,
    val params: Array<ParameterDoc> = [],
    val returns: Array<ReturnDoc> = [],
    val examples: Array<String>
)