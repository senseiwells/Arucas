package me.senseiwells.arucas.utils.collections

import me.senseiwells.arucas.builtin.FunctionDef
import me.senseiwells.arucas.classes.instance.ClassInstance

/**
 * This class holds [ClassInstance] that have the definition of [FunctionDef], allows
 * for extremely fast lookup for functions as it searches by name and parameter count.
 */
class FunctionMap: BaseFunctionMap<ClassInstance>() {
    /**
     * Adds a [ClassInstance] to the map. The instance should have a definition
     * of [FunctionDef], if not then an [IllegalArgumentException] will be thrown.
     *
     * @param instance the instance to add.
     * @return true if no function was overwritten false otherwise.
     */
    override fun add(instance: ClassInstance): Boolean {
        val function = instance.getPrimitive(FunctionDef::class)
        function ?: throw IllegalArgumentException("Tried to add non function value ${instance.definition.name} to function map")

        val map = this.map.value.computeIfAbsent(function.name) { HashMap() }
        if (map.containsKey(function.count)) {
            map[function.count] = instance
            return false
        }

        map[function.count] = instance
        return true
    }
}