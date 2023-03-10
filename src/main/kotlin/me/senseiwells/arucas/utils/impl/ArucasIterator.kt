package me.senseiwells.arucas.utils.impl

import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.interpreter.Interpreter

interface ArucasIterator: Iterator<ClassInstance> {
    companion object {
        val EMPTY = object: ArucasIterator {
            override fun hasNext() = false

            override fun next() = runtimeError("Iterator is empty!")
        }

        fun wrap(iterator: Iterator<ClassInstance>): ArucasIterator {
            if (iterator is ArucasIterator) {
                return iterator
            }
            return object: ArucasIterator {
                override fun hasNext() = iterator.hasNext()

                override fun next() = iterator.next()
            }
        }

        fun wrap(interpreter: Interpreter, iterator: Iterator<Any?>): ArucasIterator {
            return object: ArucasIterator {
                override fun hasNext() = iterator.hasNext()

                override fun next() = interpreter.convertValue(iterator.next())
            }
        }
    }
}