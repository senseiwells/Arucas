package me.senseiwells.arucas.utils

import me.senseiwells.arucas.classes.PrimitiveDefinition

/**
 * This class maps a [Class] of a [PrimitiveDefinition] to it's [PrimitiveDefinition].
 */
class PrimitiveDefinitionMap: Iterable<PrimitiveDefinition<*>> {
    private val map = HashMap<Class<*>, PrimitiveDefinition<*>>()

    /**
     * Adds a definition to the map.
     *
     * @param definition the definition to add.
     */
    fun add(definition: PrimitiveDefinition<*>) {
        this.map[definition::class.java] = definition
    }

    /**
     * Gets a definition from its class.
     *
     * @param clazz the class of the definition you want to get.
     * @return the definition.
     */
    fun <T: PrimitiveDefinition<*>> get(clazz: Class<out T>): T? {
        val definition = this.map[clazz]
        @Suppress("UNCHECKED_CAST")
        return definition as? T
    }

    /**
     * Gets an iterator of all the primitive definitions.
     *
     * @return the primitive definition iterator.
     */
    override fun iterator(): Iterator<PrimitiveDefinition<*>> {
        return this.map.values.iterator()
    }
}