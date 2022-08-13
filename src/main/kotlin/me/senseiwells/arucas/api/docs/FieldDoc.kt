package me.senseiwells.arucas.api.docs

@Target(AnnotationTarget.FIELD)
annotation class FieldDoc(
    val isStatic: Boolean = true,
    val name: String,
    val desc: Array<String>,
    val type: String,
    val assignable: Boolean = false,
    val examples: Array<String>
)