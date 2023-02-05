package me.senseiwells.arucas.api.docs.visitor

import me.senseiwells.arucas.api.docs.annotations.ClassDoc as ClassDocAnnotation
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc as ParameterDocAnnotation

/**
 * This class serves as a wrapper for [ParameterDocAnnotation].
 *
 * @param doc the [ParameterDocAnnotation] to wrap.
 */
class ParameterDoc(private val doc: ParameterDocAnnotation): Describable {
    /**
     * This gets the name of the parameter.
     *
     * @return the name of the parameter.
     */
    fun getName(): String {
        return this.doc.name
    }

    /**
     * This gets the description of the parameter.
     *
     * @return the description of the parameter.
     */
    override fun getDescription(): Array<String> {
        return this.doc.desc
    }

    /**
     * This gets the main type of the parameter.
     *
     * @return the [ClassDoc] of the main type.
     */
    fun getType(): ClassDoc {
        return ClassDoc(this.doc.type.java.getAnnotation(ClassDocAnnotation::class.java))
    }

    /**
     * This gets all the possible types for the
     * parameter.
     *
     * @return the list of [ClassDoc]s of the parameter types.
     */
    fun getAllTypes(): List<ClassDoc> {
        val types = mutableListOf(this.getType())
        types.addAll(this.doc.alternativeTypes.map {
            ClassDoc(it.java.getAnnotation(ClassDocAnnotation::class.java))
        })
        return types
    }

    /**
     * This returns whether the parameter
     * has a variable amount of arguments.
     *
     * @return whether the parameter is varargs.
     */
    fun isVarargs(): Boolean {
        return this.doc.isVarargs
    }
}