package me.senseiwells.arucas.api.docs.annotations

import me.senseiwells.arucas.classes.PrimitiveDefinition

/**
 * This annotation should be used to document any
 * constructors declared in a [PrimitiveDefinition].
 * The annotation should be placed on your constructor method:
 * ```
 * class ExampleDef(
 *     interpreter: Interpreter
 * ): PrimitiveDefinition<Any>("Example", interpreter) {
 *     fun defineConstructors(): List<ConstructorFunction> {
 *         return listOf(
 *             ConstructorFunction.of(1, this::construct)
 *         )
 *     }
 *
 *     @ConstructorDoc(
 *         desc = ["This creates a new Example object with a string."],
 *         params = [ParameterDoc(StringDef::class, "string", ["The string."])],
 *         examples = ["new Example('FooBar');"]
 *     )
 *     private fun construct(arguments: Arguments) {
 *         val instance = arguments.next()
 *         val primitive = arguments.nextPrimitive(StringDef::class)
 *         instance.setPrimitive(this, primitive)
 *     }
 * }
 * ```
 * This information can then be used for generation.
 *
 * @param desc the description of the constructor.
 * @param params the parameters being passed into the constructor, see [ParameterDoc].
 * @param examples examples of the constructor being used.
 * @see TODO
 */
@Target(AnnotationTarget.FUNCTION)
annotation class ConstructorDoc(
    /**
     * The description of the constructor.
     */
    val desc: Array<String>,
    /**
     * The parameters being passed into the constructor, see [ParameterDoc].
     */
    val params: Array<ParameterDoc> = [],
    /**
     * Examples of the constructor being used.
     */
    val examples: Array<String>
)
