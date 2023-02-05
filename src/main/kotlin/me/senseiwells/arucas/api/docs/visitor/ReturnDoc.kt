package me.senseiwells.arucas.api.docs.visitor

import me.senseiwells.arucas.api.docs.annotations.ClassDoc as ClassDocAnnotation
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc as ReturnDocAnnotation

/**
 * This class serves as a wrapper for [ReturnDocAnnotation].
 *
 * @param doc the [ReturnDocAnnotation] to wrap.
 */
class ReturnDoc(private val doc: ReturnDocAnnotation): Describable {
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
        return ClassDoc(this.doc.type.java.getAnnotation(ClassDocAnnotation::class.java))
    }
}