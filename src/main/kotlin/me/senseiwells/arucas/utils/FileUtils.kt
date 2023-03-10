package me.senseiwells.arucas.utils

import java.nio.file.Files
import java.nio.file.Path

object FileUtils {
    @JvmStatic
    fun Path.ensureParentExists(): Path {
        this.parent.ensureExists()
        return this
    }

    @JvmStatic
    fun Path.ensureExists(): Path {
        if (!Files.exists(this)) {
            Files.createDirectories(this)
        }
        return this
    }
}