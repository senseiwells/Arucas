package me.senseiwells.arucas.utils.impl

import me.senseiwells.arucas.builtin.IteratorDef
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.interpreter.Interpreter

/**
 * An iterable that is implemented to be used in Arucas.
 *
 * All objects that inherit this class are classed as iterables
 * in Arucas and can be wrapped in a definition that inherits
 * [IteratorDef].
 *
 * All collections also inherit [ArucasIterable].
 */
interface ArucasIterable: Iterable<ClassInstance> {
    companion object {
        /**
         * An empty iterable that will always return an empty iterator.
         *
         * @see ArucasIterator.EMPTY
         */
        val EMPTY = object: ArucasIterable {
            override fun iterator() = ArucasIterator.EMPTY
        }

        /**
         * This method wraps a regular [Iterable] of [Any] into an [ArucasIterable].
         *
         * @param interpreter the interpreter to convert the values.
         * @param iterable the regular iterable to wrap.
         * @return the wrapped iterable.
         */
        fun wrap(interpreter: Interpreter, iterable: Iterable<Any?>): ArucasIterable {
            return object: ArucasIterable {
                override fun iterator() = ArucasIterator.wrap(interpreter, iterable.iterator())
            }
        }
    }

    /**
     * The iterator to iterate the iterable's elements.
     *
     * @return the iterator.
     * @see ArucasIterator
     */
    override fun iterator(): ArucasIterator
}