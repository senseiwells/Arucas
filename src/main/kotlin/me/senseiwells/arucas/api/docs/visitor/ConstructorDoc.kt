package me.senseiwells.arucas.api.docs.visitor

import me.senseiwells.arucas.utils.StringUtils
import me.senseiwells.arucas.api.docs.annotations.ConstructorDoc as ConstructorDocAnnotation

/**
 * This class serves as a wrapper for [ConstructorDocAnnotation].
 *
 * @param origin the doc parser where this class was created.
 * @param doc the [ConstructorDocAnnotation] to wrap.
 */
class ConstructorDoc(
    private val origin: ArucasDocParser,
    private val doc: ConstructorDocAnnotation
): Describable {
    private val lazyDescription by lazy { StringUtils.punctuate(this.doc.desc) }
    private val lazyParameters by lazy { this.doc.params.map { ParameterDoc(this.origin, it) } }

    /**
     * This gets the description of the constructor.
     *
     * @return the description of the constructor.
     */
    override fun getDescription(): Array<String> {
        return this.lazyDescription
    }

    /**
     * This gets the [ParameterDoc]s for the constructor.
     *
     * @return the parameter documentations for the constructors.
     */
    fun getParameters(): List<ParameterDoc> {
        return this.lazyParameters
    }

    /**
     * This gets the number of parameters the constructor has.
     *
     * @return the number of parameters the constructor has.
     */
    fun getParameterCount(): Int {
        return this.doc.params.size
    }

    /**
     * This gets whether the function has parameters.
     *
     * @return whether the function has parameters.
     */
    fun hasParameters(): Boolean {
        return this.getParameterCount() != 0
    }

    /**
     * This gets whether the function has a varargs parameter.
     *
     * @return whether the function has a vararg parameter.
     */
    fun isVarArgs(): Boolean {
        return this.doc.params.any { it.isVarargs }
    }

    /**
     * This gets the examples for the constructor.
     *
     * @return the examples.
     */
    fun getExamples(): Array<String> {
        return this.doc.examples
    }
}