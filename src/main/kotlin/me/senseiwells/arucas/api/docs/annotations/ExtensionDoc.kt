package me.senseiwells.arucas.api.docs.annotations

import me.senseiwells.arucas.utils.Util

@Target(AnnotationTarget.CLASS)
annotation class ExtensionDoc(
    val name: String,
    val desc: Array<String>,
    val language: Util.Language = Util.Language.Kotlin
)