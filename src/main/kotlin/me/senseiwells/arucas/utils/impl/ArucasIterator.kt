package me.senseiwells.arucas.utils.impl

import me.senseiwells.arucas.builtin.IteratorDef
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.interpreter.Interpreter

/**
 * An iterator that is implemented to be used in Arucas.
 *
 * All objects that inherit this class are classed as iterators in
 * Arucas and can be wrapped in a definition that inherits [IteratorDef].
 */
interface ArucasIterator: Iterator<ClassInstance> {
    companion object {
        /**
         * An empty iterator with no elements.
         */
        val EMPTY = object: ArucasIterator {
            override fun hasNext() = false

            override fun next() = runtimeError("Iterator is empty!")
        }

        /**
         * This method wraps a regular [Iterator] of [ClassInstance] into an [ArucasIterator].
         *
         * @param iterator the regular iterator to wrap.
         * @return the wrapped iterator.
         */
        fun wrap(iterator: Iterator<ClassInstance>): ArucasIterator {
            if (iterator is ArucasIterator) {
                return iterator
            }
            return object: ArucasIterator {
                override fun hasNext() = iterator.hasNext()

                override fun next() = iterator.next()
            }
        }

        /**
         * This method wraps a regular [Iterator] of [Any] into an [ArucasIterator].
         *
         * @param interpreter the interpreter to convert the values.
         * @param iterator the regular iterator to wrap.
         * @return the wrapped iterator.
         */
        fun wrap(interpreter: Interpreter, iterator: Iterator<Any?>): ArucasIterator {
            return object: ArucasIterator {
                override fun hasNext() = iterator.hasNext()

                override fun next() = interpreter.convertValue(iterator.next())
            }
        }
    }
}