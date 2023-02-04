package me.senseiwells.arucas.api

import me.senseiwells.arucas.utils.BuiltInFunction

/**
 * This interface is used for extensions.
 */
interface ArucasExtension {
    /**
     * Gets the functions in the extension.
     *
     * @return the list of built-in functions.
     */
    fun getBuiltInFunctions(): List<BuiltInFunction>
}