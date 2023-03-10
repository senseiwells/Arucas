package me.senseiwells.arucas.api.docs.visitor

import me.senseiwells.arucas.utils.StringUtils
import me.senseiwells.arucas.utils.misc.Language
import me.senseiwells.arucas.api.docs.annotations.ExtensionDoc as ExtensionDocAnnotation

/**
 * This class serves as a wrapper for [ExtensionDocAnnotation].
 *
 * @param doc the [ExtensionDocAnnotation] to wrap.
 */
class ExtensionDoc(private val doc: ExtensionDocAnnotation): Describable {
    private val lazyDescription by lazy { StringUtils.punctuate(this.doc.desc) }

    /**
     * This gets the name of the extension.
     *
     * @return the name of the extension.
     */
    fun getName(): String {
        return this.doc.name
    }

    /**
     * This gets the description of the extension.
     *
     * @return the description of the extension.
     */
    override fun getDescription(): Array<String> {
        return this.lazyDescription
    }

    /**
     * This gets the language that the extension was written in.
     *
     * @return the [Language] that the extension was written in.
     */
    fun getLanguage(): Language {
        return this.doc.language
    }
}