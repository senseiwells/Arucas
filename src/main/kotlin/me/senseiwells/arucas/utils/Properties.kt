package me.senseiwells.arucas.utils

import me.senseiwells.arucas.core.Interpreter
import java.util.*

/**
 * A properties class for the [Interpreter].
 */
data class Properties(
    /**
     * Whether the interpreter is in debug mode.
     */
    var isDebug: Boolean = false,
    /**
     * Whether experimental features should be enabled.
     */
    var isExperimental: Boolean = false,
    /**
     * Whether deprecated features/functions should be logged.
     */
    var logDeprecated: Boolean = false,
    /**
     * The maximum amount of characters for a formatted error.
     */
    var errorMaxLength: Int = 60,
    /**
     * The unique id of the interpreter, this is shared with its children.
     */
    var id: UUID = UUID.randomUUID()
)