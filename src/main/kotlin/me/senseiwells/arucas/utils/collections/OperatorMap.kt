package me.senseiwells.arucas.utils.collections

import me.senseiwells.arucas.builtin.FunctionDef
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.compiler.token.Type
import me.senseiwells.arucas.utils.CollectionUtils
import java.util.*

class OperatorMap: Iterable<Pair<Type, ClassInstance>> {
    private val map = lazy { EnumMap<Type, HashMap<Int, ClassInstance>>(Type::class.java) }

    fun add(type: Type, instance: ClassInstance) {
        val function = instance.getPrimitive(FunctionDef::class)
        function ?: throw IllegalArgumentException("Tried to add non function value ${instance.definition.name} to operator map")

        val map = this.map.value.computeIfAbsent(type) { HashMap() }
        map[function.count] = instance
    }

    fun addAll(operators: Iterable<Pair<Type, ClassInstance>>) = operators.forEach { this.add(it.first, it.second) }

    fun get(type: Type, parameters: Int): ClassInstance? {
        return if (this.map.isInitialized()) this.map.value[type]?.let { it[parameters] } else null
    }

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