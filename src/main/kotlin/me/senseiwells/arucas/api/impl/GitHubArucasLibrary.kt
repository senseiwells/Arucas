package me.senseiwells.arucas.api.impl

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import me.senseiwells.arucas.api.ArucasLibrary
import me.senseiwells.arucas.core.Arucas
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.Util
import me.senseiwells.arucas.utils.Util.File.ensureParentExists
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

private const val LIBRARY_URL = "https://api.github.com/repos/senseiwells/ArucasLibraries/contents/libs"
private val GSON = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

/**
 * Default Implementation of the Arucas Library Manager.
 *
 * This implementation pulls libraries from a GitHub repository.
 *
 * @param importPath the path where libraries should be downloaded to.
 * @param libraryURL the GitHub library URL.
 */
open class GitHubArucasLibrary @JvmOverloads constructor(
    /**
     * The path where libraries should be downloaded to.
     */
    override val importPath: Path = Arucas.PATH.resolve("libs"),
    /**
     * The GitHub library URL.
     */
    private val libraryURL: String = LIBRARY_URL
): ArucasDownloadableLibrary(importPath) {
    /**
     * This method retrieves a library file. We do this
     * by fetching the files from a GitHub repository.
     *
     * We keep a cache of the hash of each file to check if
     * they need updating, and we keep recently loaded
     * libraries in a cache at runtime.
     *
     * @param import the import path, e.g. `["util", "StringUtils"]`.
     * @param local whether to read the import locally only.
     * @param interpreter the interpreter that is importing the library.
     * @return the library contents, null if not found.
     * @see ArucasLibrary.getImport
     */
    override fun getImport(import: List<String>, local: Boolean, interpreter: Interpreter): String? {
        val name = import.joinToString("/")
        val filePath = this.importPath.resolve("${import.joinToString(File.separator)}.arucas").ensureParentExists()

        if (local) {
            interpreter.logDebug(".Reading local import: '$name'")
            return this.read(filePath)
        }

        val cachePath = this.getCachePath()
        val cacheContent = if (Files.exists(cachePath)) Files.readString(cachePath) else null
        val cache = cacheContent?.let {
            Util.Exception.catchAsNull {
                GSON.fromJson(
                    cacheContent,
                    JsonObject::class.java
                )
            }
        } ?: JsonObject()
        val thisCache = cache.get(name)?.asJsonObject ?: JsonObject()

        val lastUpdateTime = thisCache.get("last")?.asLong
        val currentTime = System.currentTimeMillis() / 1_000
        if (lastUpdateTime != null && lastUpdateTime + 86_400 > currentTime && Files.exists(filePath)) {
            interpreter.logDebug("Import '$name' was read locally, last updated: ${currentTime - lastUpdateTime}s ago")
            return this.read(filePath)
        }

        val sha = thisCache.get("sha")?.asString
        val raw = Util.Network.getStringFromUrl("${this.libraryURL}/$name.arucas")
            ?: return this.updateLastAndRead(cache, thisCache, name, filePath, interpreter)
        val response = Util.Exception.catchAsNull { GSON.fromJson(raw, JsonObject::class.java) }
            ?: return this.updateLastAndRead(cache, thisCache, name, filePath, interpreter)

        val newSha = response.get("sha")
        if (newSha.asString == sha && Files.exists(filePath)) {
            this.read(filePath)?.let {
                interpreter.logDebug("Import '$name' is up to date!")
                this.updateCacheAndWrite(cache, thisCache, name)
                return it
            }
        }
        val library = Util.Network.getStringFromUrl(response.get("download_url").asString)
            ?: return this.updateLastAndRead(cache, thisCache, name, filePath, interpreter)
        interpreter.logDebug("Downloaded latest library for '$name'")
        thisCache.add("sha", newSha)
        Files.writeString(filePath, library)
        this.updateCacheAndWrite(cache, thisCache, name)
        return library
    }

    private fun updateCacheAndWrite(cache: JsonObject, thisCache: JsonObject, name: String) {
        thisCache.addProperty("last", System.currentTimeMillis() / 1_000)
        cache.add(name, thisCache)
        Files.writeString(this.getCachePath(), GSON.toJson(cache))
    }

    private fun updateLastAndRead(cache: JsonObject, thisCache: JsonObject, name: String, filePath: Path, interpreter: Interpreter): String? {
        interpreter.logDebug("Failed to retrieve import '$name', checking local libraries")
        this.updateCacheAndWrite(cache, thisCache, name)
        val library = this.read(filePath)
        interpreter.logDebug(library?.let { "Successfully found library locally" } ?: "Failed to find library locally")
        return library
    }
}