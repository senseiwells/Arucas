package me.senseiwells.arucas.utils

import me.senseiwells.arucas.builtin.FunctionDef
import me.senseiwells.arucas.classes.instance.ClassInstance

/**
 * This class holds [ClassInstance] that have the definition of [FunctionDef], allows
 * for extremely fast lookup for functions as it searches by name and parameter count.
 */
class FunctionMap: Iterable<ClassInstance> {
    /**
     * Map of function names containing a map of function parameters to a function [ClassInstance].
     */
    private val map = lazy { HashMap<String, HashMap<Int, ClassInstance>>() }

    /**
     * Adds a [ClassInstance] to the map. The instance should have a definition
     * of [FunctionDef], if not then an [IllegalArgumentException] will be thrown.
     *
     * @param instance the instance to add.
     * @return true if no function was overwritten false otherwise.
     */
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

    /**
     * Adds an iterable of [ClassInstance], using [add].
     *
     * @see add
     */
    fun addAll(functions: Iterable<ClassInstance>) {
        functions.forEach { this.add(it) }
    }

    /**
     * Checks whether the function map is empty.
     *
     * @return whether the map is empty.
     */
    fun isEmpty(): Boolean {
        return !this.map.isInitialized() || this.map.value.isEmpty()
    }

    /**
     * Checks whether the map has a function with a given name (any number of parameters).
     *
     * @param name the name of the function.
     * @return whether the map contains a function with that name.
     */
    fun has(name: String): Boolean {
        return this.map.isInitialized() && this.map.value.containsKey(name)
    }

    /**
     * Checks whether the map has a function with a given name and number of parameters.
     *
     * @param name the name of the function.
     * @param parameters the number of parameters.
     * @return whether the map has a function with this signature.
     */
    fun has(name: String, parameters: Int): Boolean {
        return this.map.isInitialized() && this.map.value[name]?.get(parameters) !== null
    }

    /**
     * Gets a function from the map using the function name **only** if the function is not overloaded.
     *
     * @param name the name of the function.
     * @return the function [ClassInstance], null if the function is overloaded or not found.
     */
    fun get(name: String): ClassInstance? {
        if (!this.map.isInitialized()) {
            return null
        }
        return this.map.value[name]?.let { if (it.size == 1) it.entries.first().value else null }
    }

    /**
     * Gets a function from the map with a given name and number of parameters.
     * Vararg functions have a parameter count of `-1`, if you pass any number
     * `< -1` then simply [get] is called instead.
     *
     * @param name the name of the function.
     * @param parameters the number of parameters the function has.
     * @return the function [ClassInstance].
     */
    fun get(name: String, parameters: Int): ClassInstance? {
        if (!this.map.isInitialized()) {
            return null
        }
        // If the parameters are less than -2 then we just return if no overload
        // Then it tries to match the number of parameters
        // Otherwise it will try to find an arbitrary function
        return if (parameters <= -2) this.get(name) else this.map.value[name]?.let { it[parameters] ?: it[-1] }
    }

    /**
     * Returns an iterator of all the function [ClassInstance]s.
     *
     * @return an iterator of [ClassInstance].
     */
    override fun iterator(): Iterator<ClassInstance> {
        if (!this.map.isInitialized()) {
            return Util.Collection.emptyIterator()
        }
        return this.map.value.values.flatMap { it.values }.iterator()
    }
}