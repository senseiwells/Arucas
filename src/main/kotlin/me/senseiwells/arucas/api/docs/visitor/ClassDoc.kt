package me.senseiwells.arucas.api.docs.visitor

import me.senseiwells.arucas.utils.Util
import me.senseiwells.arucas.api.docs.annotations.ClassDoc as ClassDocAnnotation

/**
 * This class serves as a wrapper for [ClassDocAnnotation].
 *
 * @param doc the [ClassDocAnnotation] to wrap.
 */
class ClassDoc(private val doc: ClassDocAnnotation): Describable {
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
        return this.doc.desc
    }

    /**
     * This returns whether the class is importable.
     *
     * @return whether the class is importable.
     */
    fun isImportable(): Boolean {
        return this.doc.importPath.isNotBlank()
    }

    /**
     * This gets the import path of the class.
     *
     * @return the import path of the class.
     */
    fun getImportPath(): String {
        return this.doc.importPath
    }

    /**
     * This gets the [ClassDoc] for the superclass
     * of the current [ClassDoc].
     *
     * @return the [ClassDoc] of the superclass.
     */
    fun getSuperclass(): ClassDoc {
        return ClassDoc(this.doc.superclass.java.getAnnotation(ClassDocAnnotation::class.java))
    }

    /**
     * This gets the language that the class was written in.
     *
     * @return the [Util.Language] that the class was written in.
     */
    fun getLanguage(): Util.Language {
        return this.doc.language
    }
}