package me.senseiwells.arucas.utils

import me.senseiwells.arucas.classes.PrimitiveDefinition

class PrimitiveDefinitionMap: Iterable<PrimitiveDefinition<*>> {
    private val map = HashMap<Class<*>, PrimitiveDefinition<*>>()

    fun add(definition: PrimitiveDefinition<*>) {
        this.map[definition::class.java] = definition
    }

    fun <T: PrimitiveDefinition<*>> get(clazz: Class<out T>): T? {
        val definition = this.map[clazz]
        @Suppress("UNCHECKED_CAST")
        return definition as? T
    }

    override fun iterator(): Iterator<PrimitiveDefinition<*>> {
        return this.map.values.iterator()
    }
}