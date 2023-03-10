package me.senseiwells.arucas.api.impl

import me.senseiwells.arucas.api.ArucasLibrary
import me.senseiwells.arucas.core.Interpreter
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.stream.Collectors

/**
 * Implementation of [ArucasLibrary] that allows for
 * reading of libraries located in the resources' directory.
 *
 * This class may be extended to change the source of the stream
 * by overriding [getResourceStream].
 *
 * @param libraryPath the library path in the resource folder.
 * @see ArucasLibrary
 */
open class ResourceArucasLibrary(
    /**
     * The library path in the resource folder.
     */
    val libraryPath: String
): ArucasLibrary {
    /**
     * This method retrieves a library from the resources' directory.
     *
     * @param import the import file, e.g. `["util", "StringUtils"]`.
     * @param local whether to import locally only, this parameter has no significance in this call.
     * @param interpreter the interpreter that is importing the library.
     * @return The file contents, null if it could not be retrieved.
     */
   override fun getImport(import: List<String>, local: Boolean, interpreter: Interpreter): String? {
       val path = "${this.libraryPath}/${import.joinToString("/")}.arucas"
       return this.getResourceStream(path).use { stream ->
           if (stream == null) {
               interpreter.logDebug("Failed to load '$path' library from resources")
               return null
           }
           InputStreamReader(stream).use { reader ->
               BufferedReader(reader).use {
                   it.lines().collect(Collectors.joining(System.lineSeparator()))
               }
           }
       }
   }

    /**
     * Gets the [InputStream] from the given library path.
     *
     * @param path the path of the library.
     * @return the input stream, may be null in which case the library is not imported.
     */
    protected open fun getResourceStream(path: String): InputStream? {
        return ResourceArucasLibrary::class.java.classLoader.getResourceAsStream(path)
    }
}