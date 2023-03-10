package me.senseiwells.arucas.api.docs.visitor

import me.senseiwells.arucas.utils.StringUtils
import me.senseiwells.arucas.api.docs.annotations.FieldDoc as FieldDocAnnotation

/**
 * This class serves as a wrapper for [FieldDocAnnotation].
 *
 * @param origin the doc parser where this class was created.
 * @param doc the [FieldDocAnnotation] to wrap.
 */
class FieldDoc(
    private val origin: ArucasDocParser,
    private val doc: FieldDocAnnotation
): Describable {
    private val lazyDescription by lazy { StringUtils.punctuate(this.doc.desc) }
    private val lazyType by lazy { this.origin.getClassDoc(this.doc.type.java) }

    /**
     * This gets the name of the field.
     *
     * @return the name of the field.
     */
    fun getName(): String {
        return this.doc.name
    }

    /**
     * This gets the description of the field.
     *
     * @return the description of the field.
     */
    override fun getDescription(): Array<String> {
        return this.lazyDescription
    }

    /**
     * This gets the field type.
     *
     * @return the [ClassDoc] of the field type.
     */
    fun getType(): ClassDoc {
        return this.lazyType
    }

    /**
     * This gets whether the field is static.
     *
     * @return whether the field is static.
     */
    fun isStatic(): Boolean {
        return this.doc.isStatic
    }

    /**
     * This gets whether the field is assignable.
     *
     * @return whether the field is assignable.
     */
    fun isAssignable(): Boolean {
        return this.doc.assignable
    }

    /**
     * This gets the examples for the function.
     *
     * @return the examples.
     */
    fun getExamples(): Array<String> {
        return this.doc.examples
    }
}