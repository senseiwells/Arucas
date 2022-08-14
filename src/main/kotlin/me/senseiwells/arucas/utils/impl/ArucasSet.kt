package me.senseiwells.arucas.utils.impl

import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.core.Interpreter

class ArucasSet(private val map: ArucasMap): ArucasCollection {
    private companion object {
        fun setToMap(interpreter: Interpreter, set: Set<*>): ArucasMap {
            val map = ArucasOrderedMap()
            for (element in set) {
                map.put(interpreter, interpreter.convertValue(element), interpreter.getNull())
            }
            return map
        }
    }

    constructor(): this(ArucasOrderedMap())

    constructor(interpreter: Interpreter, set: ArucasSet): this(ArucasOrderedMap(interpreter, set.map))

    constructor(interpreter: Interpreter, set: Set<*>): this(setToMap(interpreter, set))

    override fun length() = this.map.length()

    override fun asCollection() = this.map.asCollection()

    override fun toSafeString() = "<set>"

    override fun formatCollection(elements: String) = "<$elements>"

    fun add(interpreter: Interpreter, value: ClassInstance): Boolean {
        return this.map.put(interpreter, value, interpreter.getNull()) === null
    }

    fun get(interpreter: Interpreter, value: ClassInstance): ClassInstance? {
        return this.map.getKey(interpreter, value)
    }

    fun remove(interpreter: Interpreter, value: ClassInstance): Boolean {
        return this.map.remove(interpreter, value) !== null
    }

    fun contains(interpreter: Interpreter, value: ClassInstance): Boolean {
        return this.map.containsKey(interpreter, value)
    }

    fun containsAll(interpreter: Interpreter, collection: Collection<ClassInstance>): Boolean {
        for (instance in ArucasCollection.safeCollection(collection)) {
            if (!this.contains(interpreter, instance)) {
                return false
            }
        }
        return true
    }

    fun addAll(interpreter: Interpreter, collection: Collection<ClassInstance>) {
        for (instance in ArucasCollection.safeCollection(collection)) {
            this.add(interpreter, instance)
        }
    }

    fun removeAll(interpreter: Interpreter, collection: Collection<ClassInstance>) {
        for (instance in ArucasCollection.safeCollection(collection)) {
            this.remove(interpreter, instance)
        }
    }

    fun clear() = this.map.clear()

    fun isEmpty() = this.map.isEmpty()

    fun equals(interpreter: Interpreter, other: ArucasSet): Boolean {
        return this.map.equals(interpreter, other.map)
    }

    fun hashCode(interpreter: Interpreter): Int {
        return this.map.hashCode(interpreter)
    }

    @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("set.toString(interpreter)"))
    override fun toString() = this.toSafeString()

    @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("set.hashCode(interpreter)"))
    override fun hashCode() = System.identityHashCode(this)

    @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("set.equals(interpreter, other)"))
    override fun equals(other: Any?) = this === other
}