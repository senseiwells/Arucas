package me.senseiwells.arucas.api

import me.senseiwells.arucas.api.docs.annotations.ExtensionDoc
import me.senseiwells.arucas.utils.BuiltInFunction

/**
 * This interface is used for extensions.
 */
interface ArucasExtension {
    /**
     * Gets the functions in the extension.
     *
     * @return the list of built-in functions.
     */
    fun getBuiltInFunctions(): List<BuiltInFunction>

    /**
     * Gets the name of the extension.
     *
     * @return the name of the extension.
     */
    fun getName(): String {
        val doc = this::class.java.getAnnotation(ExtensionDoc::class.java)
        doc ?: throw IllegalStateException("Extension was not annotated with documentation")
        return doc.name
    }
}