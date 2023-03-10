package me.senseiwells.arucas.api.docs.visitor

import me.senseiwells.arucas.utils.StringUtils
import me.senseiwells.arucas.utils.misc.Language
import me.senseiwells.arucas.api.docs.annotations.ClassDoc as ClassDocAnnotation

/**
 * This class serves as a wrapper for [ClassDocAnnotation].
 *
 * @param origin the doc parser where this class was created.
 * @param doc the [ClassDocAnnotation] to wrap.
 * @param importPath the import path of the class.
 */
class ClassDoc(
    private val origin: ArucasDocParser,
    private val doc: ClassDocAnnotation,
    private val importPath: String?
): Describable {
    private val lazyDescription by lazy { StringUtils.punctuate(this.doc.desc) }
    private val lazySuperclass by lazy { this.origin.getClassDoc(this.doc.superclass.java) }

    /**
     * This gets the name of the class.
     *
     * @return the name of the class.
     */
    fun getName(): String {
        return this.doc.name
    }

    /**
     * This gets the description of the class.
     *
     * @return the description of the class.
     */
    override fun getDescription(): Array<String> {
        return this.lazyDescription
    }

    /**
     * This returns whether the class is importable.
     *
     * @return whether the class is importable.
     */
    fun isImportable(): Boolean {
        return this.importPath != null
    }

    /**
     * This gets the import path of the class.
     *
     * @return the import path of the class.
     */
    fun getImportPath(): String {
        return this.importPath!!
    }

    /**
     * This gets the [ClassDoc] for the superclass
     * of the current [ClassDoc].
     *
     * @return the [ClassDoc] of the superclass.
     */
    fun getSuperclass(): ClassDoc {
        return this.lazySuperclass
    }

    /**
     * This gets the language that the class was written in.
     *
     * @return the [Language] that the class was written in.
     */
    fun getLanguage(): Language {
        return this.doc.language
    }
}