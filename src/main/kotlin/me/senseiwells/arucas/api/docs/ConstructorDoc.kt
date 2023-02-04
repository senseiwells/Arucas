package me.senseiwells.arucas.api.docs

@Deprecated("me.senseiwells.arucas.api.docs.annotations.ConstructorDoc should be used instead.")
@Target(AnnotationTarget.FUNCTION)
annotation class ConstructorDoc(
    val desc: Array<String>,
    val params: Array<String> = [],
    val examples: Array<String>
)
