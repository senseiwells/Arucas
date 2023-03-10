package me.senseiwells.arucas.utils

import me.senseiwells.arucas.builtin.NumberDef
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.interpreter.Interpreter
import me.senseiwells.arucas.utils.impl.ArucasIterable
import me.senseiwells.arucas.utils.impl.ArucasIterator

object CollectionUtils {
    private val emptyIterator = object: Iterator<Nothing> {
        override fun hasNext() = false

        override fun next() = throw NoSuchElementException("Empty iterator has no values")
    }

    infix fun <A, B, C> Pair<A, B>.to(that: C): Triple<A, B, C> = Triple(this.first, this.second, that)

    @JvmStatic
    fun <T> emptyIterator(): Iterator<T> {
        return this.emptyIterator
    }

    @JvmStatic
    fun rangeIterable(interpreter: Interpreter, end: Double, start: Double = 0.0, step: Double = 1.0): ArucasIterable {
        return object: ArucasIterable {
            override fun iterator(): ArucasIterator {
                return RangeIterator(start, end, step) { interpreter.create(NumberDef::class, it) }
            }
        }
    }

    private class RangeIterator(
        var current: Double,
        val end: Double,
        val step: Double,
        val converter: (Double) -> ClassInstance
    ): ArucasIterator {
        override fun hasNext(): Boolean {
            return if (this.step > 0) this.current < this.end else this.end < this.current
        }

        override fun next(): ClassInstance {
            val next = this.converter(this.current)
            this.current += this.step
            return next
        }
    }
}