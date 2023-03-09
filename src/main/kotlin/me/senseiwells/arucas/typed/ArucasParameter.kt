package me.senseiwells.arucas.typed

import me.senseiwells.arucas.classes.ClassDefinition

class ArucasParameter(
    val name: String,
    private val definitions: LazyDefinitions = LazyDefinitions.of()
) {
    fun getTypes(): List<ClassDefinition> {
        return this.definitions.get()
    }

    fun typesAsString(): String {
        return this.definitions.get().joinToString(" | ")
    }
}