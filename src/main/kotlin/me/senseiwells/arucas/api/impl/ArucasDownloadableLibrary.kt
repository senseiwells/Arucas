package me.senseiwells.arucas.api.impl

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import me.senseiwells.arucas.api.ArucasLibrary
import me.senseiwells.arucas.api.impl.ArucasDownloadableLibrary.LibraryCache
import me.senseiwells.arucas.interpreter.Interpreter
import me.senseiwells.arucas.utils.ExceptionUtils
import me.senseiwells.arucas.utils.FileUtils.ensureParentExists
import me.senseiwells.arucas.utils.NetworkUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

/**
 * This class is used for downloading arucas files.
 *
 * This assumes that the library source is able to push updates.
 * This implementation caches libraries locally to use until
 * they are marked as invalid by [secondsTillInvalidateCache].
 * Any libraries that are either not stored locally or marked
 * as invalid will be re-downloaded and re-cached.
 *
 * This class does the majority of the heavy lifting for
 * child classes, they only need to implement [getUpdatedCache]
 * which returns the latest [LibraryCache] for this class to handle.
 *
 * @see ArucasLibrary
 * @see GitHubArucasLibrary
 */
abstract class ArucasDownloadableLibrary(
    /**
     * The number of seconds until the cache is invalid.
     */
    private val secondsTillInvalidateCache: Long = 86_400
): ArucasLibrary {
    companion object {
        private val GSON = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    }

    /**
     * The import path.
     */
    abstract val importPath: Path

    /**
     * This method retrieves a library file. It then
     * saves the file locally and caches it.
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
    final override fun getImport(import: List<String>, local: Boolean, interpreter: Interpreter): String? {
        val name = import.joinToString("/")
        val filePath = this.importPath.resolve("${import.joinToString(File.separator)}.arucas").ensureParentExists()

        if (local) {
            interpreter.logDebug("Reading local import: '$name'")
            return this.read(filePath)
        }

        val (json, existingCache) = this.readCache(name)
        val currentTime = System.currentTimeMillis() / 1_000
        if (existingCache.lastUpdateSeconds + this.secondsTillInvalidateCache > currentTime && Files.exists(filePath)) {
            interpreter.logDebug("Import '$name' was read locally, last updated: ${currentTime - existingCache.lastUpdateSeconds}s ago")
            return this.read(filePath)
        }

        val newCache = this.getUpdatedCache(name) ?: return this.updateLastAndRead(json, existingCache, name, filePath, interpreter)
        if (newCache.sha == existingCache.sha && Files.exists(filePath)) {
            this.read(filePath)?.let {
                interpreter.logDebug("Import '$name' is up to date!")
                this.updateCacheAndWrite(json, newCache, name)
                return it
            }
        }

        val library = NetworkUtils.getStringFromUrl(newCache.downloadUrl) ?: return this.updateLastAndRead(json, existingCache, name, filePath, interpreter)
        interpreter.logDebug("Downloaded latest library for '$name'")
        Files.writeString(filePath, library)
        this.updateCacheAndWrite(json, newCache, name)
        return library
    }

    /**
     * This fetches an updated [LibraryCache] object with the latest sha and download url.
     *
     * @param name the name of the library.
     * @return the updated [LibraryCache].
     */
    protected abstract fun getUpdatedCache(name: String): LibraryCache?

    /**
     * This gets the path where the cache data is stored.
     *
     * @return the file where the cache data is stored.
     */
    protected open fun getCachePath(): Path {
        return this.importPath.resolve(".ArucasCache.json").ensureParentExists()
    }

    private fun readCache(name: String): Pair<JsonObject, LibraryCache> {
        val path = this.getCachePath()
        val cacheContent = this.read(path)
        val cache = cacheContent?.let {
            ExceptionUtils.catchAsNull {
                GSON.fromJson(
                    cacheContent,
                    JsonObject::class.java
                )
            }
        } ?: JsonObject()
        val json = cache.get(name)?.asJsonObject
        return cache to LibraryCache(
            json?.get("last")?.asLong ?: 0,
            json?.get("sha")?.asString ?: "",
            json?.get("url")?.asString ?: ""
        )
    }

    private fun read(filePath: Path): String? {
        return ExceptionUtils.catchAsNull { Files.readString(filePath) }
    }

    private fun updateLastAndRead(json: JsonObject, cache: LibraryCache, name: String, filePath: Path, interpreter: Interpreter): String? {
        interpreter.logDebug("Failed to retrieve import '$name', checking local libraries")
        this.updateCacheAndWrite(json, cache, name)
        val library = this.read(filePath)
        interpreter.logDebug(library?.let { "Successfully found library locally" } ?: "Failed to find library locally")
        return library
    }

    private fun updateCacheAndWrite(json: JsonObject, cache: LibraryCache, name: String) {
        json.add(name, JsonObject().also {
            it.addProperty("last", System.currentTimeMillis() / 1_000)
            it.addProperty("sha", cache.sha)
            it.addProperty("url", cache.downloadUrl)
        })
        Files.writeString(this.getCachePath(), GSON.toJson(json))
    }

    protected data class LibraryCache(var lastUpdateSeconds: Long, var sha: String, var downloadUrl: String)
}