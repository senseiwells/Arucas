package me.senseiwells.arucas.api

import java.awt.Desktop
import java.io.File

/**
 * Interface to handle files for the Interpreter.
 */
interface ArucasFileHandler {
    /**
     * This method should open a file on the user's os.
     *
     * @param file the file to open.
     */
    fun open(file: File) {
        Desktop.getDesktop().open(file)
    }
}