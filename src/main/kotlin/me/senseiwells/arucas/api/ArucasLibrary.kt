package me.senseiwells.arucas.api

import me.senseiwells.arucas.api.impl.ArucasDownloadableLibrary
import me.senseiwells.arucas.interpreter.Interpreter

/**
 * This interface provides the ability to get an import
 * library from a given location.
 *
 * This can be implemented and only serves as a provider -
 * any libraries that implement this interface should
 * have defined imports otherwise [ArucasDownloadableLibrary]
 * should be implemented where it downloads libraries
 * from an outside source at runtime.
 *
 * @see ArucasDownloadableLibrary
 */
interface ArucasLibrary {
    /**
     * This method should retrieve a library.
     *
     * @param import the import file, e.g. `["util", "StringUtils"]`.
     * @param local whether to import locally only.
     * @param interpreter the interpreter that is importing the library.
     * @return The file contents, null if it could not be retrieved.
     */
    fun getImport(import: List<String>, local: Boolean, interpreter: Interpreter): String?
}