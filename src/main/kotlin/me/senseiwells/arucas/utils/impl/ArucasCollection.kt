package me.senseiwells.arucas.utils.impl

import me.senseiwells.arucas.builtin.CollectionDef
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.LocatableTrace
import java.util.*

interface ArucasCollection: ArucasIterable {
    fun length(): Int

    fun asCollection(): Collection<ClassInstance>

    fun toSafeString(): String

    fun formatCollection(elements: String): String

    fun toString(interpreter: Interpreter, trace: LocatableTrace): String {
        return this.toStringDeep(interpreter, trace, Stack())
    }

    fun toStringDeep(interpreter: Interpreter, trace: LocatableTrace, processed: Stack<ArucasCollection>): String {
        // This could lead to possible deadlocks if multiple collections are
        // converted to a string on different threads at the same time?
        if (this in processed) {
            return this.toSafeString()
        }
        processed.push(this)
        val builder = StringBuilder()
        val iterator = this.iterator()
        while (iterator.hasNext()) {
            val instance = iterator.next()
            val collection = instance.getPrimitive(CollectionDef::class)
            val str = collection?.toStringDeep(interpreter, trace, processed) ?: instance.toString(interpreter, trace)
            builder.append(str)
            if (iterator.hasNext()) {
                builder.append(", ")
            }
        }
        processed.pop()
        return this.formatCollection(builder.toString())
    }

    override fun iterator(): ArucasIterator = ArucasIterator.wrap(this.asCollection().iterator())

    @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("collection.hashCode(interpreter)"))
    override fun hashCode(): Int

    @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("collection.equals(interpreter, other)"))
    override fun equals(other: Any?): Boolean

    @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("collection.toString(interpreter)"))
    override fun toString(): String

    fun needInterpreterForThis(): Nothing {
        throw UnsupportedOperationException("Needed interpreter for this operation")
    }

    companion object {
        val EMPTY = object: ArucasCollection {
            override fun length() = 0

            override fun asCollection()= listOf<ClassInstance>()

            override fun toSafeString() = "<collection>"

            override fun formatCollection(elements: String) = elements

            override fun iterator() = ArucasIterator.EMPTY

            @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("collection.toString(interpreter)"))
            override fun toString(): String {
                return this.toSafeString()
            }

            @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("collection.hashCode(interpreter)"))
            override fun hashCode(): Int {
                return System.identityHashCode(this)
            }

            @Deprecated("Need interpreter for this operation", replaceWith = ReplaceWith("collection.equals(interpreter, other)"))
            override fun equals(other: Any?): Boolean {
                return this === other
            }
        }

        fun safeCollection(collection: Collection<ClassInstance>): Array<ClassInstance> {
            return if (collection is ArucasList) collection.toArray() else collection.toTypedArray()
        }
    }
}