package me.senseiwells.arucas.api.impl

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import me.senseiwells.arucas.core.Arucas
import me.senseiwells.arucas.utils.Util
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
): ArucasDownloadableLibrary() {
    /**
     * This fetches an updated library cache object with the latest sha and download url.
     *
     * @param name the name of the library.
     * @return the updated library cache.
     * @see ArucasDownloadableLibrary.getUpdatedCache
     */
    override fun getUpdatedCache(name: String): LibraryCache? {
        val raw = Util.Network.getStringFromUrl("${this.libraryURL}/$name.arucas") ?: return null
        val response = Util.Exception.catchAsNull { GSON.fromJson(raw, JsonObject::class.java) } ?: return null
        return LibraryCache(
            System.currentTimeMillis() / 1_000,
            response.get("sha").asString,
            response.get("download_url").asString
        )
    }
}