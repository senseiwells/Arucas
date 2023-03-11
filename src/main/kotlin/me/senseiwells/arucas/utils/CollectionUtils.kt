package me.senseiwells.arucas.utils

import me.senseiwells.arucas.builtin.*
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.interpreter.Interpreter
import me.senseiwells.arucas.utils.impl.ArucasIterable
import me.senseiwells.arucas.utils.impl.ArucasIterator

/**
 * Utility object for collections.
 */
object CollectionUtils {
    private val emptyIterator = object: Iterator<Nothing> {
        override fun hasNext() = false

        override fun next() = throw NoSuchElementException("Empty iterator has no values")
    }

    /**
     * Creates a triple by combining a pair with another value:
     * ```kt
     * 0 to 1 to 2
     * ```
     *
     * @param that the third value.
     * @return the created triple.
     */
    infix fun <A, B, C> Pair<A, B>.to(that: C): Triple<A, B, C> {
        return Triple(this.first, this.second, that)
    }

    /**
     * Returns an iterator that has no values.
     *
     * @return the empty iterator.
     */
    @JvmStatic
    fun <T> emptyIterator(): Iterator<T> {
        return this.emptyIterator
    }

    /**
     * Creates an [ArucasIterable] that iterates over a range.
     *
     * @param interpreter the interpreter that called the range.
     * @param end the end of the range.
     * @param start the start of the range.
     * @param step the step of the range.
     * @return the range iterable.
     */
    @JvmStatic
    fun rangeIterable(interpreter: Interpreter, end: Double, start: Double = 0.0, step: Double = 1.0): ArucasIterable {
        return object: ArucasIterable {
            override fun iterator(): ArucasIterator {
                return RangeIterator(start, end, step) { interpreter.create(NumberDef::class, it) }
            }
        }
    }

    /**
     * Wraps an implemented iterator instance from Arucas to Kotlin.
     *
     * @param interpreter the interpreter that wants to iterate.
     * @param instance the instance to iterator.
     * @param trace the trace location.
     * @return the wrapped iterator.
     */
    @JvmStatic
    fun wrapIterator(interpreter: Interpreter, instance: ClassInstance, trace: LocatableTrace): ArucasIterator {
        val iter = instance.getPrimitiveOrThrow(IteratorDef::class, "Cannot iterate a non Iterator value: ${instance.toString(interpreter, trace)}", trace)
        // Implemented natively - faster
        if (iter !== ArucasIterator.EMPTY) {
            return iter
        }
        return object: ArucasIterator {
            override fun hasNext(): Boolean {
                return instance.callMemberPrimitive(interpreter, "hasNext", listOf(), BooleanDef::class, trace)
            }

            override fun next(): ClassInstance {
                return instance.callMember(interpreter, "next", listOf(), trace)
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