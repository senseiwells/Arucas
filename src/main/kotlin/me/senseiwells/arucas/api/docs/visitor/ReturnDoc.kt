package me.senseiwells.arucas.api.docs.visitor

import me.senseiwells.arucas.api.docs.annotations.ReturnDoc as ReturnDocAnnotation

/**
 * This class serves as a wrapper for [ReturnDocAnnotation].
 *
 * @param origin the doc parser where this class was created.
 * @param doc the [ReturnDocAnnotation] to wrap.
 */
class ReturnDoc(
    private val origin: ArucasDocParser,
    private val doc: ReturnDocAnnotation
): Describable {
    private val lazyType by lazy { this.origin.getClassDoc(this.doc.type.java) }

    /**
     * This gets the description of the return value.
     *
     * @return the description of the return value.
     */
    override fun getDescription(): Array<String> {
        return this.doc.desc
    }

    /**
     * This gets the return type.
     *
     * @return the [ClassDoc] of the return type.
     */
    fun getType(): ClassDoc {
        return this.lazyType
    }
}