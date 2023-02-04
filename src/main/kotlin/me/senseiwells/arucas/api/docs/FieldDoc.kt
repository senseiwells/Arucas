package me.senseiwells.arucas.api.docs

@Deprecated("me.senseiwells.arucas.api.docs.annotations.FieldDoc should be used instead.")
@Target(AnnotationTarget.FIELD)
annotation class FieldDoc(
    val isStatic: Boolean = true,
    val name: String,
    val desc: Array<String>,
    val type: String,
    val assignable: Boolean = false,
    val examples: Array<String>
)