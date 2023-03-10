package me.senseiwells.arucas.utils.collections

import me.senseiwells.arucas.builtin.FunctionDef
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.compiler.token.Type
import me.senseiwells.arucas.utils.CollectionUtils
import java.util.*

/**
 * Much like the [FunctionMap] but instead of storing `Strings -> Ints -> ClassInstance`
 * this class stores `Type -> Ints -> ClassInstance` allowing for extremely fast lookup
 * for operators.
 */
class OperatorMap: Iterable<Pair<Type, ClassInstance>> {
    /**
     * The map of operator to parameters to functions.
     */
    private val map = lazy { EnumMap<Type, HashMap<Int, ClassInstance>>(Type::class.java) }

    /**
     * Adds a function for a specific operator to the map.
     *
     * @param type the operator type.
     * @param instance the function instance.
     */
    fun add(type: Type, instance: ClassInstance) {
        val function = instance.getPrimitive(FunctionDef::class)
        function ?: throw IllegalArgumentException("Tried to add non function value ${instance.definition.name} to operator map")

        val map = this.map.value.computeIfAbsent(type) { HashMap() }
        map[function.count] = instance
    }

    /**
     * Adds an iterable of operators and functions to the map, see [add].
     *
     * @param operators the iterable of operators and functions.
     */
    fun addAll(operators: Iterable<Pair<Type, ClassInstance>>) {
        operators.forEach { this.add(it.first, it.second) }
    }

    /**
     * Gets a function for operator type and number of parameters.
     *
     * @param type the operator type.
     * @param parameters the number of parameters for the operator.
     * @return the function [ClassInstance], null if not found.
     */
    fun get(type: Type, parameters: Int): ClassInstance? {
        return if (this.map.isInitialized()) this.map.value[type]?.let { it[parameters] } else null
    }

    /**
     * The iterator for all the operators and their respective functions.
     *
     * @return the iterator of the operators and their respective functions.
     */
    override fun iterator(): Iterator<Pair<Type, ClassInstance>> {
        if (!this.map.isInitialized()) {
            return CollectionUtils.emptyIterator()
        }
        return this.map.value.entries.map {
            val type = it.key
            it.value.map { pair -> type to pair.value }
        }.flatten().iterator()
    }
}