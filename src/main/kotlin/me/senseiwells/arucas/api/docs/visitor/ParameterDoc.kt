package me.senseiwells.arucas.api.docs.visitor

import me.senseiwells.arucas.utils.StringUtils
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc as ParameterDocAnnotation

/**
 * This class serves as a wrapper for [ParameterDocAnnotation].
 *
 * @param origin the doc parser where this class was created.
 * @param doc the [ParameterDocAnnotation] to wrap.
 */
class ParameterDoc(
    private val origin: ArucasDocParser,
    private val doc: ParameterDocAnnotation
): Describable {
    private val lazyDescription by lazy { StringUtils.punctuate(this.doc.desc) }
    private val lazyType by lazy { this.origin.getClassDoc(this.doc.type.java) }
    private val lazyAllTypes by lazy {
        val types = mutableListOf(this.getType())
        types.addAll(this.doc.alternativeTypes.map {
            this.origin.getClassDoc(it.java)
        })
        types
    }

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
        return this.lazyDescription
    }

    /**
     * This gets the main type of the parameter.
     *
     * @return the [ClassDoc] of the main type.
     */
    fun getType(): ClassDoc {
        return this.lazyType
    }

    /**
     * This gets all the possible types for the
     * parameter.
     *
     * @return the list of [ClassDoc]s of the parameter types.
     */
    fun getAllTypes(): List<ClassDoc> {
        return this.lazyAllTypes
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