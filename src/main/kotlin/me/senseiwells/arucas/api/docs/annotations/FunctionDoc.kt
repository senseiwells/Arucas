package me.senseiwells.arucas.api.docs.annotations

import me.senseiwells.arucas.api.ArucasExtension
import me.senseiwells.arucas.builtin.NullDef
import me.senseiwells.arucas.classes.PrimitiveDefinition

/**
 * This annotation should be used to document any
 * functions/methods declared in a [PrimitiveDefinition]
 * or [ArucasExtension].
 * The annotation should be placed on your methods:
 * ```
 * class ExampleDef(
 *     interpreter: Interpreter
 * ): PrimitiveDefinition<Any>("Example", interpreter) {
 *     @FunctionDoc(
 *         isStatic = true,
 *         deprecated = ["This function is deprecated because... You should use..."],
 *         name = "foobar",
 *         desc = ["Description of the function."],
 *         params = [ParameterDoc(NumberDef::class, "parameter", ["Parameter description."])],
 *         returns = ReturnDoc(NumberDef::class, ["Return value description."]),
 *         examples = ["Example.foobar(3.5);"]
 *     )
 *     private fun foobar(arguments: Arguments) {
 *         // ...
 *     }
 * }
 * ```
 * This information can then be used for generation.
 *
 * @param isStatic whether the method is static, this can be ignored for [ArucasExtension].
 * @param deprecated the deprecation message.
 * @param name the name of the function.
 * @param desc the description of the function.
 * @param params the parameters for the function, see [ParameterDoc].
 * @param returns the return value documentation, see [ReturnDoc].
 * @param examples examples of the method in use.
 * @see me.senseiwells.arucas.api.docs.visitor.FunctionDoc
 */
@Target(AnnotationTarget.FUNCTION)
annotation class FunctionDoc(
    /**
     * Whether the method is static, this can be ignored for [ArucasExtension].
     */
    val isStatic: Boolean = false,
    /**
     * The deprecation message.
     */
    val deprecated: Array<String> = [],
    /**
     * The name of the function.
     * This should follow Pascal casing.
     */
    val name: String,
    /**
     * The description of the function.
     */
    val desc: Array<String>,
    /**
     * The parameters for the function, see [ParameterDoc].
     */
    val params: Array<ParameterDoc> = [],
    /**
     * The return value documentation, see [ReturnDoc].
     */
    val returns: ReturnDoc = ReturnDoc(NullDef::class, []),
    /**
     * Examples of the method in use.
     */
    val examples: Array<String>
)