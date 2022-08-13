package me.senseiwells.arucas.utils

import me.senseiwells.arucas.extensions.JavaDef
import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.core.Interpreter

class ValueConverter {
    private val converterMap = HashMap<Class<*>, Converter<*>>()

    fun <T: Any> addClass(clazz: Class<T>, converter: Converter<T>) {
        this.converterMap[clazz] = converter
    }

    fun convertFrom(any: Any?, interpreter: Interpreter): ClassInstance {
        return if (any == null) interpreter.getNull() else this.fromNotNull(any, interpreter)
    }

    private fun <T: S, S: Any> fromNotNull(any: T, interpreter: Interpreter): ClassInstance {
        if (any is ClassInstance) {
            return any
        }

        val base: Class<*> = any::class.java
        var current = base
        while (current != Any::class.java) {
            val converter = this.converterMap[current]
            if (converter != null) {
                @Suppress("UNCHECKED_CAST")
                return (converter as Converter<S>)(any, interpreter)
            }
            current = current.superclass
        }
        for (iClass in base.interfaces) {
            val converter = this.converterMap[iClass]
            if (converter != null) {
                @Suppress("UNCHECKED_CAST")
                return (converter as Converter<S>)(any, interpreter)
            }
        }

        interpreter.logDebug("Couldn't convert value '$any' returning Java value")
        return interpreter.create(JavaDef::class, any)
    }

}

typealias Converter<T> = (T, Interpreter) -> ClassInstance