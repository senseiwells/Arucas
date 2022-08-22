package me.senseiwells.arucas.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import me.senseiwells.arucas.core.Arucas
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.Util
import me.senseiwells.arucas.utils.Util.Exception
import me.senseiwells.arucas.utils.Util.File.ensureParentExists
import java.nio.file.Files
import java.nio.file.Path

private const val LIBRARY_URL = "https://api.github.com/repos/senseiwells/ArucasLibraries/contents/libs"
private val GSON = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

/**
 * Default Implementation of the Arucas Library Manager.
 */

open class ImplArucasLibrary @JvmOverloads constructor(
    override val importPath: Path = Arucas.PATH.resolve("libs"),
    private val libraryURL: String = LIBRARY_URL
): ArucasLibrary {

    /**
     * This method retrieves a library file. We do this
     * by fetching the files from a GitHub repository.
     *
     * We keep a cache of the hash of each file to check if
     * they need updating, and we keep recently loaded
     * libraries in a cache at runtime.
     */
    override fun getImport(import: List<String>, local: Boolean, interpreter: Interpreter): String? {
        val name = import.joinToString("/")
        val filePath = this.importPath.resolve("$name.arucas").ensureParentExists()

        if (local) {
            interpreter.logDebug("Reading local import: '$name'")
            return this.read(filePath)
        }

        val cachePath = this.getCachePath()
        val cacheContent = if (Files.exists(cachePath)) Files.readString(cachePath) else null
        val cache = cacheContent?.let { Exception.catchAsNull { GSON.fromJson(cacheContent, JsonObject::class.java) } } ?: JsonObject()
        val thisCache = cache.get(name)?.asJsonObject ?: JsonObject()

        val lastUpdateTime = thisCache.get("last")?.asLong
        val currentTime = System.currentTimeMillis() / 1_000
        if (lastUpdateTime != null && lastUpdateTime + 86_400 > currentTime ) {
            interpreter.logDebug("Import '$name' was read locally, last updated: ${currentTime - lastUpdateTime}s ago")
            return this.read(filePath)
        }

        val sha = thisCache.get("sha")?.asString
        val raw = Util.Network.getStringFromUrl("${this.libraryURL}/$name.arucas")
            ?: return this.updateLastAndRead(cache, thisCache, name, filePath, interpreter)
        val response = Exception.catchAsNull { GSON.fromJson(raw, JsonObject::class.java) }
            ?: return this.updateLastAndRead(cache, thisCache, name, filePath, interpreter)

        val newSha = response.get("sha")
        if (newSha.asString == sha) {
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
        this.updateCacheAndWrite(cache, thisCache, name)
        return library
    }

    protected open fun getCachePath(): Path = this.importPath.resolve("ArucasCache.json").ensureParentExists()

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

    private fun read(filePath: Path): String? {
        return Exception.catchAsNull { Files.readString(filePath) }
    }
}