package me.senseiwells.arucas.api.docs

@Target(AnnotationTarget.FUNCTION)
annotation class FunctionDoc(
    val isVarArgs: Boolean = false,
    val isStatic: Boolean = false,
    val deprecated: Array<String> = [],
    val name: String,
    val desc: Array<String>,
    val params: Array<String> = [],
    val returns: Array<String> = [],
    @Deprecated("This field will no longer be included")
    val throwMsgs: Array<String> = [],
    val examples: Array<String>
)