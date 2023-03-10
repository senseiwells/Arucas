package me.senseiwells.arucas.utils

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

/**
 * Utility object for handling network requests.
 */
object NetworkUtils {
    /**
     * Retrieves the content from a given [url] and reads it as a string.
     *
     * @param url the url to request.
     * @return the content as a string, null if the request failed.
     */
    @JvmStatic
    fun getStringFromUrl(url: String): String? {
        return try {
            val input = URL(url).openStream()
            input.bufferedReader().use { it.readText() }
        } catch (exception: IOException) {
            null
        }
    }

    /**
     * Downloads the contents of an [url] to a [file].
     *
     * @param url the url to download from.
     * @param file the file to download to.
     * @return whether the download was successful.
     */
    @JvmStatic
    fun downloadFile(url: String, file: File): Boolean {
        return try {
            FileOutputStream(file).use { output ->
                URL(url).openStream().use { input ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: IOException) {
            false
        }
    }
}