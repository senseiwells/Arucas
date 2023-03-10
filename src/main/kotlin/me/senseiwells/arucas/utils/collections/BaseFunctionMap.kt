package me.senseiwells.arucas.utils.collections

import me.senseiwells.arucas.utils.CollectionUtils

/**
 * This class holds [T] that have a name and a given number of parameters, allows
 * for extremely fast lookup for functions as it searches by name and parameter count.
 */
abstract class BaseFunctionMap<T>: Iterable<T> {
    /**
     * Map of function names containing a map of function parameters to a function [T].
     */
    protected val map = lazy { HashMap<String, HashMap<Int, T>>() }

    /**
     * Adds a [T] to the map.
     *
     * @param instance the instance to add.
     * @return true if no function was overwritten false otherwise.
     */
    abstract fun add(instance: T): Boolean

    /**
     * Adds an iterable of [T], using [add].
     *
     * @see add
     */
    fun addAll(functions: Iterable<T>) {
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
     * @return the function [T], null if the function is overloaded or not found.
     */
    fun get(name: String): T? {
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
     * @return the function [T].
     */
    fun get(name: String, parameters: Int): T? {
        if (!this.map.isInitialized()) {
            return null
        }
        // If the parameters are less than -2 then we just return if no overload
        // Then it tries to match the number of parameters
        // Otherwise it will try to find an arbitrary function
        return if (parameters <= -2) this.get(name) else this.map.value[name]?.let { it[parameters] ?: it[-1] }
    }

    /**
     * Returns an iterator of all the function [T]s.
     *
     * @return an iterator of [T].
     */
    override fun iterator(): Iterator<T> {
        if (!this.map.isInitialized()) {
            return CollectionUtils.emptyIterator()
        }
        return this.map.value.values.flatMap { it.values }.iterator()
    }
}