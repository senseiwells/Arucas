package me.senseiwells.arucas.classes

import me.senseiwells.arucas.builtin.FunctionDef
import me.senseiwells.arucas.builtin.ObjectDef
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.classes.instance.HintedField
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.*

/**
 * This class represents a [ClassDefinition] that has a
 * 'primitive' value - meaning that it supports a [ClassInstance]
 * that 'wraps' a Java [Object].
 *
 * This provides an API for defining static fields, static methods,
 * constructors, and methods in Java/Kotlin.
 *
 * This class should be extended for your custom definition
 * if you do not wish people to be able to use the `create`
 * method. Otherwise, see [CreatableDefinition].
 *
 * @param T the primitive value type that you are wrapping.
 * @param name the name of the class.
 * @param interpreter the interpreter that the definition was defined on.
 * @see ClassDefinition
 * @see CreatableDefinition
 */
abstract class PrimitiveDefinition<T: Any>(
    name: String,
    interpreter: Interpreter
): ClassDefinition(name, interpreter) {
    /**
     * Initialises the [ClassInstance]. If the primitive assigned to the instance
     * is not present, `null`, then an error is thrown.
     *
     * @param interpreter the interpreter that instantiated the [ClassInstance].
     * @param instance the instance that is to be instantiated.
     * @param args the arguments parsed into the instantiation for the constructor.
     * @param trace the trace that this was called from.
     */
    override fun init(interpreter: Interpreter, instance: ClassInstance, args: MutableList<ClassInstance>, trace: CallTrace) {
        super.init(interpreter, instance, args, trace)
        if (instance.getPrimitive(this) == null) {
            throw IllegalStateException("Primitive '${this.name}' was not initialised correctly")
        }
    }

    /**
     * This returns the superclass of the current definition,
     * by default this is [ObjectDef], this may only be a [PrimitiveDefinition],
     * and the primitive value must inherit the parent.
     *
     * @return the superclass definition.
     */
    override fun superclass(): PrimitiveDefinition<in T> {
        return this.getPrimitiveDef(ObjectDef::class)
    }

    /**
     * This returns a list of [PrimitiveField] which is used
     * to define a static field.
     *
     * @return the list of [PrimitiveField]s.
     * @see PrimitiveField
     */
    open fun defineStaticFields(): List<PrimitiveField>? {
        return null
    }

    /**
     * This returns a list of all the [ConstructorFunction]s of the class definition.
     * These are used to create instances of the class.
     *
     * @return a list of the constructor functions.
     * @see ConstructorFunction
     */
    open fun defineConstructors(): List<ConstructorFunction>? {
        return null
    }

    /**
     * This returns a list of all the [MemberFunction]s of the class. These
     * define the methods (behaviours) of the class. The methods can be overloaded.
     *
     * @return a list of member functions.
     * @see MemberFunction
     */
    open fun defineMethods(): List<MemberFunction>? {
        return null
    }

    /**
     * This returns a list of all the static functions of the class. These
     * define static methods, much like regular functions. The methods can be overloaded.
     *
     * @return a list of built-in functions.
     * @see BuiltInFunction
     */
    open fun defineStaticMethods(): List<BuiltInFunction>? {
        return null
    }

    /**
     * This creates an instance of the definition setting the primitive value of it.
     *
     * @param value the primitive value to create the [ClassInstance] from.
     * @return the [ClassInstance].
     */
    protected open fun create(value: T): ClassInstance {
        return ClassInstance(this).also { it.setPrimitive(this, value) }
    }

    protected fun construct(): ClassInstance {
        return ClassInstance(this)
    }

    final override fun interfaces(): Set<InterfaceDefinition> {
        return super.interfaces()
    }

    override fun asJavaValue(instance: ClassInstance): Any? {
        return instance.asPrimitive(this)
    }

    override fun equals(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Boolean {
        val otherPrimitive = other.getPrimitive(this) ?: return false
        if (this.superclass() != interpreter.getPrimitive(ObjectDef::class)) {
            return this.superclass().equals(instance, interpreter, other, trace)
        }
        return instance.asPrimitive(this) == otherPrimitive
    }

    override fun hashCode(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Int {
        if (this.superclass() != interpreter.getPrimitive(ObjectDef::class)) {
            return this.superclass().hashCode(instance, interpreter, trace)
        }
        return instance.asPrimitive(this).hashCode()
    }

    override fun toString(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): String {
        if (this.superclass() != interpreter.getPrimitive(ObjectDef::class)) {
            return this.superclass().toString(instance, interpreter, trace)
        }
        return instance.asPrimitive(this).toString()
    }

    internal fun merge() {
        val functionDef = this.interpreter.getPrimitive(FunctionDef::class)
        this.defineConstructors()?.let { constructor ->
            constructor.forEach { this.constructors.value.add(functionDef.create(it)) }
        }
        this.defineMethods()?.let { methods ->
            methods.forEach { this.methods.value.add(functionDef.create(it)) }
        }
        this.defineStaticMethods()?.let { methods ->
            methods.forEach { this.staticMethods.value.add(functionDef.create(it)) }
        }
        this.defineStaticFields()?.let {
            for ((name, value, assignable) in it) {
                val instance = this.interpreter.convertValue(value)
                this.staticFields.value[name] = HintedField.of("${this.name}.$name", instance, assignable)
            }
        }
    }

    /**
     * Simple data class representing a field.
     *
     * @param name the name of the field.
     * @param value the value of the field.
     * @param isAssignable whether the field can be re-assigned.
     */
    data class PrimitiveField(val name: String, val value: Any?, val isAssignable: Boolean)
}