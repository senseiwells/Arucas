package me.senseiwells.arucas.api.impl

import me.senseiwells.arucas.api.ArucasLibrary
import me.senseiwells.arucas.core.Arucas
import me.senseiwells.arucas.core.Interpreter
import java.nio.file.Path

/**
 * This implementation of [ArucasLibrary] allows for
 * multiple [ArucasLibrary] to be registered in one object.
 *
 * All the registered libraries will be searched until
 * a library matching the import is found at which point
 * the search will halt.
 *
 * It is important to note that the order at which libraries
 * are added to this class determine the order in which
 * they are searched when importing.
 *
 * @param importPath the import path that will be used by all libraries.
 */
class MultiArucasLibrary(
    /**
     * The import path that will be used by all libraries.
     */
    override val importPath: Path = Arucas.PATH.resolve("libs")
): ArucasLibrary {
    /**
     * The ordered map of libraries, mapping their identifier to the library.
     */
    private val libraries = LinkedHashMap<String, ArucasLibrary>()

    /**
     * This adds a library to the Multi-Library. The library id
     * can be its name, which can later be used to remove the library.
     *
     * @param identifier the identifier which can be used to remove the library.
     * @param library the library generator, which provides the [Path].
     */
    @Synchronized
    fun addLibrary(identifier: String, library: (Path) -> ArucasLibrary) {
        this.libraries[identifier] = library(this.importPath)
    }

    /**
     * This removes a library from the Multi-Library.
     *
     * @param identifier the identifier to remove.
     */
    @Synchronized
    fun removeLibrary(identifier: String) {
        this.libraries.remove(identifier)
    }

    /**
     * Checks whether a given library id is currently registered to the Multi-Library.
     *
     * @param identifier the id to check.
     * @return whether the library is currently register.
     */
    @Synchronized
    fun hasLibrary(identifier: String): Boolean {
        return this.libraries.containsKey(identifier)
    }

    /**
     * Gets the library content for a given import.
     *
     * This searches through the registered libraries until it finds a library.
     *
     * @see ArucasLibrary.getImport
     */
    @Synchronized
    override fun getImport(import: List<String>, local: Boolean, interpreter: Interpreter): String? {
        for ((name, library) in this.libraries) {
            val content = library.getImport(import, local, interpreter)
            if (content != null) {
                interpreter.logDebug("MultiLibrary found library '${import.joinToString(".")}' in $name")
                return content
            }
        }
        interpreter.logDebug("MultiLibrary failed to find library '${import.joinToString(".")}'")
        return null
    }
}