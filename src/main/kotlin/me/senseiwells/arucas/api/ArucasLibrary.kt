package me.senseiwells.arucas.api

import me.senseiwells.arucas.core.Interpreter
import java.nio.file.Path

/**
 * This interface is used for downloading arucas files.
 */
interface ArucasLibrary {
    /**
     * This import path.
     */
    val importPath: Path

    /**
     * This method should retrieve a library.
     *
     * @param import The import file, e.g. `["util", "StringUtils"]`.
     * @param local whether to import locally only.
     * @param interpreter the interpreter that is importing the library.
     * @return The file contents, null if it could not be retrieved.
     */
    fun getImport(import: List<String>, local: Boolean, interpreter: Interpreter): String?
}