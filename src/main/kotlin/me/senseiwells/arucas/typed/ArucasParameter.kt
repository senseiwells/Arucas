package me.senseiwells.arucas.typed

import me.senseiwells.arucas.classes.ClassDefinition

/**
 * Class representation a parameter.
 *
 * The parameter contains the name as well as the hint types.
 *
 * @param name the name of the parameter.
 * @param definitions the lazy definitions.
 */
class ArucasParameter(
    /**
     * The name of the parameter.
     */
    val name: String,
    /**
     * The lazy definitions.
     */
    private val definitions: LazyDefinitions = LazyDefinitions.of()
) {
    /**
     * Gets the type hints for the parameter.
     *
     * @return the list of hinted types.
     */
    fun getTypes(): List<ClassDefinition> {
        return this.definitions.get()
    }

    /**
     * Gets the type hints as a string.
     *
     * @return the string representation of the types.
     */
    fun typesAsString(): String {
        return this.definitions.get().joinToString(" | ")
    }
}