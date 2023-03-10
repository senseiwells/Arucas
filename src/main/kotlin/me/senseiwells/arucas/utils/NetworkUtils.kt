package me.senseiwells.arucas.utils

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

object NetworkUtils {
    @JvmStatic
    fun getStringFromUrl(url: String): String? {
        return try {
            val input = URL(url).openStream()
            input.bufferedReader().use { it.readText() }
        } catch (exception: IOException) {
            null
        }
    }

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