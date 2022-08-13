package me.senseiwells.arucas.api.docs

@Target(AnnotationTarget.FUNCTION)
annotation class ConstructorDoc(
    val desc: Array<String>,
    val params: Array<String> = [],
    val examples: Array<String>
)
