package me.senseiwells.arucas.utils.impl

import me.senseiwells.arucas.builtin.CollectionDef
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.interpreter.Interpreter
import java.util.*

/**
 * A collection that is implemented to be used in Arucas.
 *
 * All objects that inherit this class are classed as collections
 * in Arucas and can be wrapped in a definition that inherits
 * [CollectionDef].
 *
 * All collections also inherit [ArucasIterable].
 */
interface ArucasCollection: ArucasIterable {
    /**
     * This gets the length of the collection.
     *
     * @return the length of the collection.
     */
    fun length(): Int

    /**
     * This gets the [ArucasCollection] as a [Collection].
     */
    fun asCollection(): Collection<ClassInstance>

    /**
     * Converts the collection into a safe string -
     * this means that it does not evaluate its elements.
     *
     * @return the safe string for the collection.
     */
    fun toSafeString(): String

    /**
     * Formats the elements of the collection. Usually by a prefix and postfix.
     *
     * @return the formatted collection.
     */
    fun formatCollection(elements: String): String

    /**
     * Converts the collection to a string while evaluating its elements.
     *
     * @param interpreter the interpreter being called from.
     * @param trace the trace location.
     * @return the string representation of the collection.
     */
    fun toString(interpreter: Interpreter, trace: LocatableTrace): String {
        return this.toStringDeep(interpreter, trace, Stack())
    }

    /**
     * This converts all the collections elements into strings then formats it.
     * This will evaluate elements that are also of [ArucasCollection] however
     * keeps track of previously processed collections as to not infinitely recurse.
     * If a collection contains itself it will instead be represented by the [toSafeString] representation.
     *
     * @param interpreter the interpreter being called from.
     * @param trace the trace location.
     * @param processed the stack of already processed collections.
     */
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

    /**
     * This gets the iterator for the collection.
     *
     * @return the iterator.
     */
    override fun iterator(): ArucasIterator {
        return ArucasIterator.wrap(this.asCollection().iterator())
    }

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

            override fun asCollection() = listOf<ClassInstance>()

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