package me.senseiwells.arucas.api.docs.annotations

import me.senseiwells.arucas.classes.PrimitiveDefinition
import kotlin.reflect.KClass

/**
 * This annotation should be used to document any
 * fields declared in a [PrimitiveDefinition].
 * The annotation should be placed on your fields:
 * ```
 * class ExampleDef(
 *     interpreter: Interpreter
 * ): PrimitiveDefinition<Unit>("Example", interpreter) {
 *     @FieldDoc(
 *         name = "pi",
 *         desc = ["Has the value of pi, 3.1415926535..."],
 *         type = NumberDef::class,
 *         examples = ["Example.pi;"]
 *     )
 *     private val pi = Math.PI
 *
 *     override fun defineStaticFields(): List<PrimitiveField> {
 *         return listOf(
 *             PrimitiveField("pi", this.pi, false)
 *         )
 *     }
 * ```
 * This information can then be used for generation.
 *
 * @param isStatic whether the field is static.
 * @param name the name of the field.
 * @param desc the description of the field.
 * @param type the type of the field represented by the [KClass] of a [PrimitiveDefinition].
 * @param assignable whether the field is re-assignable.
 * @param examples examples of the field in use.
 * @see me.senseiwells.arucas.api.docs.visitor.FieldDoc
 */
@Target(AnnotationTarget.FIELD)
annotation class FieldDoc(
    /**
     * Whether the field is static.
     */
    val isStatic: Boolean = true,
    /**
     * The name of the field.
     * This should follow either Pascal or macro casing.
     */
    val name: String,
    /**
     * The description of the field.
     */
    val desc: Array<String>,
    /**
     * The type of the field represented by the [KClass] of a [PrimitiveDefinition].
     */
    val type: KClass<out PrimitiveDefinition<*>>,
    /**
     * Whether the field is re-assignable.
     */
    val assignable: Boolean = false,
    /**
     * Examples of the field in use.
     */
    val examples: Array<String>
)