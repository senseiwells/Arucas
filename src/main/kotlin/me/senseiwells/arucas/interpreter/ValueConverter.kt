package me.senseiwells.arucas.interpreter

import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.extensions.JavaDef

/**
 * This class maps Java objects into Arucas [ClassInstance]s. This works
 * by having a map of [Class] with [Converter] that is able to convert
 * instances of that given class into a [ClassInstance].
 */
class ValueConverter {
    /**
     * Map of [Class] to [Converter] that maps classes to their Arucas converters.
     */
    private val converterMap = HashMap<Class<*>, Converter<*>>()

    /**
     * Adds a Java [Class] conversion, to map instances of that
     * class into [ClassInstance]s.
     *
     * @param T the class type.
     * @param clazz the class to map from.
     * @param converter the converter, converting instances of [clazz].
     */
    fun <T: Any> addClass(clazz: Class<T>, converter: Converter<T>) {
        this.converterMap[clazz] = converter
    }

    /**
     * Converts a Java class instance into an Arucas [ClassInstance].
     *
     * @param any the Java class instance.
     * @param interpreter the interpreter to use for conversions.
     * @return the [ClassInstance], if no [Converter] was found it will be of [JavaDef].
     */
    fun convertFrom(any: Any?, interpreter: Interpreter): ClassInstance {
        return if (any == null) interpreter.getNull() else this.fromNotNull(any, interpreter)
    }

    /**
     * Converts any not null Java class instance into a [ClassInstance].
     *
     * @param T the type of the object.
     * @param S a parent class of [T].
     * @param any the class instance.
     * @param interpreter the interpreter to use for conversions.
     * @return the [ClassInstance].
     * @see convertFrom
     */
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

    /**
     * Gets the converter for [clazz], if it is not null then
     * we cache it for [base] so for future use we do not need to
     * check [base]'s parent's for [Converter]s.
     *
     * @param base the base class.
     * @param clazz some parent class of [base].
     * @return the [Converter] for [clazz], may be null.
     */
    private fun getConverter(base: Class<*>, clazz: Class<*>): Converter<*>? {
        val converter = this.converterMap[clazz]
        if (converter != null) {
            this.converterMap[base] = converter
        }
        return converter
    }
}

typealias Converter<T> = (T, Interpreter) -> ClassInstance