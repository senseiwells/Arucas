package me.senseiwells.arucas.utils.impl

import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.core.Interpreter

interface ArucasIterable: Iterable<ClassInstance> {
    companion object {
        val EMPTY = object: ArucasIterable {
            override fun iterator() = ArucasIterator.EMPTY
        }

        fun wrap(interpreter: Interpreter, iterable: Iterable<Any?>): ArucasIterable {
            return object: ArucasIterable {
                override fun iterator() = ArucasIterator.wrap(interpreter, iterable.iterator())
            }
        }
    }

    override fun iterator(): ArucasIterator
}