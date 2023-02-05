package me.senseiwells.arucas.api.docs.annotations

import me.senseiwells.arucas.builtin.ObjectDef
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.utils.Util
import kotlin.reflect.KClass

/**
 * This annotation should be used to document
 * your built-in [PrimitiveDefinition]s. The annotation
 * should be placed on your definition class:
 * ```
 * @ClassDoc(
 *     name = "Example",
 *     desc = [
 *         "This example class is for example purposes only!",
 *         "You can also have multiple lines."
 *     ],
 *     superclass = CollectionDef::class,
 *     language = Util.Language.Kotlin
 * )
 * class ExampleDef(
 *     interpreter: Interpreter
 * ): PrimitiveDefinition<Any>("Example", interpreter) {
 *     // ...
 * }
 * ```
 * This information can then be used to for generation.
 *
 * @param name the name of the class.
 * @param desc the description of the class.
 * @param superclass the class' superclass [KClass].
 * @param language the language that the class was written in.
 * @see me.senseiwells.arucas.api.docs.visitor.ClassDoc
 */
@Target(AnnotationTarget.CLASS)
annotation class ClassDoc(
    /**
     * The name of the class.
     */
    val name: String,
    /**
     * The description of the class.
     */
    val desc: Array<String>,
    /**
     * The class' superclass [KClass].
     */
    val superclass: KClass<out PrimitiveDefinition<*>> = ObjectDef::class,
    /**
     * The language that the class was written in.
     */
    val language: Util.Language = Util.Language.Kotlin
)
