package me.senseiwells.arucas.exceptions

import me.senseiwells.arucas.classes.ClassInstance
import kotlin.RuntimeException

sealed class Propagator(message: String? = null): RuntimeException(message) {
    override fun fillInStackTrace(): Throwable {
        return this
    }

    class Continue private constructor(): Propagator("Tried to continue outside a loop") {
        companion object {
            val INSTANCE = Continue()
        }
    }

    class Break private constructor(): Propagator("Tried to break outside a loop") {
        companion object {
            val INSTANCE = Break()
        }
    }

    class Stop private constructor(): Propagator() {
        companion object {
            val INSTANCE = Stop()
        }
    }

    class Return(returnValue: ClassInstance): Propagator() {
        var returnValue: ClassInstance = returnValue
            internal set
    }
}