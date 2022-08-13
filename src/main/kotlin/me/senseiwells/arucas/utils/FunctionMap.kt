package me.senseiwells.arucas.utils

import me.senseiwells.arucas.builtin.FunctionDef
import me.senseiwells.arucas.classes.ClassInstance

class FunctionMap: Iterable<ClassInstance> {
    companion object {
        fun of(vararg functions: ClassInstance): FunctionMap {
            val map = FunctionMap()
            for (function in functions) {
                map.add(function)
            }
            return map
        }
    }

    private val map = lazy { HashMap<String, HashMap<Int, ClassInstance>>() }

    fun add(instance: ClassInstance): Boolean {
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

    fun addAll(functions: Iterable<ClassInstance>) = functions.forEach { this.add(it) }

    fun isEmpty() = !this.map.isInitialized() || this.map.value.isEmpty()

    fun has(name: String) = this.map.isInitialized() && this.map.value.containsKey(name)

    fun has(name: String, parameters: Int) = this.map.isInitialized() && this.map.value[name]?.get(parameters) != null

    fun get(name: String): ClassInstance? {
        if (!this.map.isInitialized()) {
            return null
        }
        return this.map.value[name]?.let { if (it.size == 1) it.entries.first().value else null }
    }

    fun get(name: String, parameters: Int): ClassInstance? {
        if (!this.map.isInitialized()) {
            return null
        }
        // If the parameters are less than -2 then we just return if no overload
        // Then it tries to match the number of parameters
        // Otherwise it will try to find an arbitrary function
        return if (parameters <= -2) this.get(name) else this.map.value[name]?.let { it[parameters] ?: it[-1] }
    }

    override fun iterator(): Iterator<ClassInstance> {
        if (!this.map.isInitialized()) {
            return Util.Collection.emptyIterator()
        }
        return this.map.value.values.flatMap { it.values }.iterator()
    }
}