package me.senseiwells.arucas.api

import me.senseiwells.arucas.utils.BuiltInFunction

/**
 * This interface is used for extensions.
 */
interface ArucasExtension {
    /**
     * Gets the name of the extension.
     */
    fun getName(): String

    /**
     * Gets the functions in the extension.
     */
    fun getBuiltInFunctions(): List<BuiltInFunction>
}