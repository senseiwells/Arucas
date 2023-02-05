package me.senseiwells.arucas.api.docs.annotations

import me.senseiwells.arucas.api.ArucasExtension
import me.senseiwells.arucas.utils.Util

/**
 * This annotation should be used to document
 * your built-in [ArucasExtension]s. The annotation
 * should be placed on your extension class:
 * ```
 * @ExtensionDoc(
 *     name = "ExampleExtension",
 *     desc = ["An extension that is an example"],
 *     language = Util.Language.Kotlin
 * )
 * class ExampleExtension: ArucasExtension {
 *    // ...
 * }
 * ```
 * This information can then be used to for generation.
 *
 * @param name the name of the extension.
 * @param desc the description for the extension.
 * @param language the language that the extension was written in.
 * @see me.senseiwells.arucas.api.docs.visitor.ExtensionDoc
 */
@Target(AnnotationTarget.CLASS)
annotation class ExtensionDoc(
    /**
     * The name of the extension.
     */
    val name: String,
    /**
     * The description for the extension.
     */
    val desc: Array<String>,
    /**
     * The language that the extension was written in.
     */
    val language: Util.Language = Util.Language.Kotlin
)