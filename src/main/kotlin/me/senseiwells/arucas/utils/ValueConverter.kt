package me.senseiwells.arucas.utils

import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.extensions.JavaDef

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
        var current: Class<*> = base
        while (current != Any::class.java) {
            val converter = this.getConverter(base, current)
            if (converter != null) {
                @Suppress("UNCHECKED_CAST")
                return (converter as Converter<S>)(any, interpreter)
            }
            for (iClass in current.interfaces) {
                val iConverter = this.getConverter(base, iClass)
                if (iConverter != null) {
                    @Suppress("UNCHECKED_CAST")
                    return (iConverter as Converter<S>)(any, interpreter)
                }
                for (iiClass in iClass.interfaces) {
                    val iiConverter = this.getConverter(base, iiClass)
                    if (iiConverter != null) {
                        @Suppress("UNCHECKED_CAST")
                        return (iiConverter as Converter<S>)(any, interpreter)
                    }
                }
            }
            current = current.superclass
        }

        interpreter.logDebug("Couldn't convert value '$any' returning Java value")
        return interpreter.create(JavaDef::class, any)
    }

    private fun getConverter(base: Class<*>, clazz: Class<*>): Converter<*>? {
        val converter = this.converterMap[clazz]
        if (converter != null) {
            this.converterMap[base] = converter
        }
        return converter
    }
}

typealias Converter<T> = (T, Interpreter) -> ClassInstance