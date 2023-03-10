package me.senseiwells.arucas.utils.misc

import me.senseiwells.arucas.api.ArucasExtension
import me.senseiwells.arucas.api.docs.annotations.ClassDoc
import me.senseiwells.arucas.api.docs.annotations.ExtensionDoc
import me.senseiwells.arucas.classes.PrimitiveDefinition

/**
 * This enum class is used to represent the
 * different languages that can natively implement
 * a [PrimitiveDefinition] and [ArucasExtension],
 * and is used in [ClassDoc] and [ExtensionDoc].
 */
enum class Language {
    Java, Kotlin, Other;

    override fun toString(): String {
        return this.name
    }
}