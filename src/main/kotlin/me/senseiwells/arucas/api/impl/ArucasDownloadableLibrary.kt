package me.senseiwells.arucas.api.impl

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import me.senseiwells.arucas.api.ArucasLibrary
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.Util
import me.senseiwells.arucas.utils.Util.File.ensureParentExists
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

private val GSON = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

/**
 * This class is used for downloading arucas files.
 */
abstract class ArucasDownloadableLibrary(
    /**
     * This import path.
     */
    open val importPath: Path,
    /**
     * The number of seconds until the cache is invalid.
     */
    private val secondsTillInvalidateCache: Long = 86_400
): ArucasLibrary {
    override fun getImport(import: List<String>, local: Boolean, interpreter: Interpreter): String? {
        TODO()
        val name = import.joinToString("/")
        val filePath = this.importPath.resolve("${import.joinToString(File.separator)}.arucas").ensureParentExists()

        if (local) {
            interpreter.logDebug("Reading local import: '$name'")
            return this.read(filePath)
        }

        var cache = this.readCache(name)
        val currentTime = System.currentTimeMillis() / 1_000
        if (cache.lastUpdateSeconds + this.secondsTillInvalidateCache > currentTime && Files.exists(filePath)) {
            interpreter.logDebug("Import '$name' was read locally, last updated: ${currentTime - cache.lastUpdateSeconds}s ago")
            return this.read(filePath)
        }

        //cache = this.getUpdatedCache(name) ?: return null

    }

    //protected abstract fun getUpdatedCache(name: String): LibraryCache?

    protected open fun getCachePath(): Path {
        return this.importPath.resolve(".ArucasCache.json").ensureParentExists()
    }

    protected fun readCache(name: String): LibraryCache {
        val path = this.getCachePath()
        val cacheContent = this.read(path)
        val cache = cacheContent?.let {
            Util.Exception.catchAsNull {
                GSON.fromJson(
                    cacheContent,
                    JsonObject::class.java
                )
            }
        }
        return LibraryCache.fromJson(cache?.get(name)?.asJsonObject)
    }

    protected fun read(filePath: Path): String? {
        return Util.Exception.catchAsNull { Files.readString(filePath) }
    }
}