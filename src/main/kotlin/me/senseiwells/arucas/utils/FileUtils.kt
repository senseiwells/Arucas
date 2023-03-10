package me.senseiwells.arucas.utils

import java.io.File
import java.nio.file.Files
import java.nio.file.Path

/**
 * Utility object for some [File] and [Path] utilities.
 */
object FileUtils {
    /**
     * Makes sure that all parents of a given
     * path exists, if they do not already exist
     * then the directories will be created.
     *
     * @return the path.
     */
    @JvmStatic
    fun Path.ensureParentExists(): Path {
        this.parent.ensureExists()
        return this
    }

    /**
     * Makes sure that the given path exists,
     * if it does not already exist then the
     * directories will be created.
     *
     * @return the path.
     */
    @JvmStatic
    fun Path.ensureExists(): Path {
        if (!Files.exists(this)) {
            Files.createDirectories(this)
        }
        return this
    }
}