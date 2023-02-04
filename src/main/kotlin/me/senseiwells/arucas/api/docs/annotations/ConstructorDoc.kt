package me.senseiwells.arucas.api.docs.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class ConstructorDoc(
    val desc: Array<String>,
    val params: Array<ParameterDoc> = [],
    val examples: Array<String>
)
