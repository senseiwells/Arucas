package me.senseiwells.arucas.api.docs.visitor

import me.senseiwells.arucas.utils.StringUtils
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc as FunctionDocAnnotation

/**
 * This class serves as a wrapper for [FunctionDocAnnotation].
 *
 * @param origin the doc parser where this class was created.
 * @param doc the [FunctionDocAnnotation] to wrap.
 */
class FunctionDoc(
    private val origin: ArucasDocParser,
    private val doc: FunctionDocAnnotation
): Describable {
    private val lazyDescription by lazy { StringUtils.ensurePunctuation(this.doc.desc) }
    private val lazyParameters by lazy { this.doc.params.map { ParameterDoc(this.origin, it) } }

    /**
     * This gets the name of the function.
     *
     * @return the name of the function.
     */
    fun getName(): String {
        return this.doc.name
    }

    /**
     * This gets the description of the function.
     *
     * @return the description of the function.
     */
    override fun getDescription(): Array<String> {
        return this.lazyDescription
    }

    /**
     * This gets the [ParameterDoc]s for the function.
     *
     * @return the parameter documentations for the function.
     */
    fun getParameters(): List<ParameterDoc> {
        return this.lazyParameters
    }

    /**
     * This gets the number of parameters the function has.
     *
     * @return the number of parameters the function has.
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
     * This gets the [ReturnDoc] for the function.
     *
     * @return the return documentation for the function.
     */
    fun getReturns(): ReturnDoc {
        return ReturnDoc(this.origin, this.doc.returns)
    }

    /**
     * This gets whether the function has a return value.
     *
     * @return whether the function has a described return value.
     */
    fun hasReturns(): Boolean {
        return this.doc.returns.desc.isNotEmpty()
    }

    /**
     * This gets whether the function is static.
     *
     * @return whether the function is static.
     */
    fun isStatic(): Boolean {
        return this.doc.isStatic
    }

    /**
     * This gets the examples for the function.
     *
     * @return the examples.
     */
    fun getExamples(): Array<String> {
        return this.doc.examples
    }

    /**
     * This gets whether the function is deprecated.
     *
     * @return whether the function is deprecated.
     */
    fun isDeprecated(): Boolean {
        return this.doc.deprecated.isNotEmpty()
    }

    /**
     * This gets the deprecation message of the function.
     *
     * @return the deprecated message.
     */
    fun getDeprecated(): Array<String> {
        return this.doc.deprecated
    }

    /**
     * This gets the examples for the function but formatted with a [separator].
     *
     * @return the deprecated message.
     */
    @JvmOverloads
    fun getFormattedDeprecated(separator: String = " "): String {
        return this.doc.deprecated.joinToString(separator)
    }
}