package me.senseiwells.arucas.classes

import me.senseiwells.arucas.builtin.FunctionDef
import me.senseiwells.arucas.builtin.ObjectDef
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.typed.ArucasVariable
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.core.Type
import me.senseiwells.arucas.extensions.JavaDef
import me.senseiwells.arucas.functions.builtin.BuiltInFunction
import me.senseiwells.arucas.functions.builtin.ConstructorFunction
import me.senseiwells.arucas.functions.builtin.MemberFunction
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

    /**
     * This returns the set of interfaces that the class is implementing.
     * Primitives cannot inherit interfaces.
     * 
     * @return the set of interfaces.
     */
    final override fun interfaces(): Set<InterfaceDefinition> {
        return super.interfaces()
    }
    
    /**
     * This method defines the behaviour of the `==` operator.
     *
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the instance to check equality on.
     * @param interpreter the interpreter that called this method.
     * @param other the instance to check equality against.
     * @param trace the trace from where it was called.
     * @return whether they [instance] and [other] are equal.
     */
    override fun equals(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Boolean {
        val otherPrimitive = other.getPrimitive(this) ?: return false
        if (this.superclass() != interpreter.getPrimitive(ObjectDef::class)) {
            return this.superclass().equals(instance, interpreter, other, trace)
        }
        return instance.asPrimitive(this) == otherPrimitive
    }

    /**
     * This method is called when hashing instances of the [ClassDefinition].
     *
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the instance to check equality on.
     * @param interpreter the interpreter that called this method.
     * @param trace the trace from where it was called.
     * @return the hashcode of the [instance].
     */
    override fun hashCode(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Int {
        if (this.superclass() != interpreter.getPrimitive(ObjectDef::class)) {
            return this.superclass().hashCode(instance, interpreter, trace)
        }
        return instance.asPrimitive(this).hashCode()
    }

    /**
     * This method is called when converting instances of the [ClassDefinition] into a string.
     *
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the instance to convert into a string.
     * @param interpreter the interpreter that called this method.
     * @param trace the trace from where it was called.
     * @return the [instance] converted into a string.
     */
    override fun toString(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): String {
        if (this.superclass() != interpreter.getPrimitive(ObjectDef::class)) {
            return this.superclass().toString(instance, interpreter, trace)
        }
        return instance.asPrimitive(this).toString()
    }

    /**
     * This method is called when the value is converted into a [JavaDef].
     *
     * @param instance the instance to convert into a [Object].
     * @return the java [Object] counterpart.
     * @see PrimitiveDefinition.asJavaValue
     */
    override fun asJavaValue(instance: ClassInstance): Any? {
        return instance.asPrimitive(this)
    }

    /**
     * This method defines the behaviour of the `!` unary operator.
     * ```
     * !true; // Not operator
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the instance to use the not operator on.
     * @param interpreter the interpreter that called this method.
     * @param trace the trace from where it was called.
     * @return the not-ed [instance].
     */
    override fun not(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Any? {
        return this.superclass().not(instance, interpreter, trace)
    }
    
    /**
     * This method defines the behaviour of the `+` unary operator.
     * ```
     * +10; // Plus operator
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the instance to use the plus on.
     * @param interpreter the interpreter that called this method.
     * @param trace the trace from where it was called.
     * @return the positive [instance].
     */
    override fun plus(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Any? {
        return this.superclass().plus(instance, interpreter, trace)
    }

    /**
     * This method defines the behaviour of the `-` unary operator.
     * ```
     * -10; // Minus operator
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the instance to use the minus on.
     * @param interpreter the interpreter that called this method.
     * @param trace the trace from where it was called.
     * @return the negative [instance].
     */
    override fun minus(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Any? {
        return this.superclass().minus(instance, interpreter, trace)
    }

    /**
     * This method defines the behaviour of the `+` binary operator.
     * ```
     * 10 + 10; // Plus operator
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the instance to use the plus on.
     * @param interpreter the interpreter that called this method.
     * @param other the instance that is being added to [instance].
     * @param trace the trace from where it was called.
     * @return the result of [instance] + [other].
     */
    override fun plus(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().plus(instance, interpreter, other, trace)
    }

    /**
     * This method defines the behaviour of the `-` binary operator.
     * ```
     * 10 - 10; // Minus operator
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the instance to use the minus on.
     * @param interpreter the interpreter that called this method.
     * @param other the instance that is being subtracted [instance].
     * @param trace the trace from where it was called.
     * @return the result of [instance] - [other].
     */
    override fun minus(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().minus(instance, interpreter, other, trace)
    }

    /**
     * This method defines the behaviour of the `*` binary operator.
     * ```
     * 10 * 10; // Multiplication operator
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the instance to multiply.
     * @param interpreter the interpreter that called this method.
     * @param other the instance that is being multiplied.
     * @param trace the trace from where it was called.
     * @return the result of the multiplication.
     */
    override fun multiply(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().multiply(instance, interpreter, other, trace)
    }

    /**
     * This method defines the behaviour of the `/` binary operator.
     * ```
     * 10 / 10; // Division operator
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the numerator.
     * @param interpreter the interpreter that called this method.
     * @param other the instance divisor.
     * @param trace the trace from where it was called.
     * @return the result of the division.
     */
    override fun divide(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().divide(instance, interpreter, other, trace)
    }

    /**
     * This method defines the behaviour of the `^` binary operator.
     * ```
     * 10 ^ 2; // Power operator
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the base of the exponent.
     * @param interpreter the interpreter that called this method.
     * @param other the exponent of the base.
     * @param trace the trace from where it was called.
     * @return the result of the power.
     */
    override fun power(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().power(instance, interpreter, other, trace)
    }

    /**
     * This method defines the behaviour of the `&&` binary operator.
     * ```
     * true && false; // And operator
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the [ClassInstance] on the left of the and.
     * @param interpreter the interpreter that called this method.
     * @param other the [ClassInstance] on the right of the and.
     * @param trace the trace from where it was called.
     * @return the result of the and.
     */
    override fun and(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().and(instance, interpreter, other, trace)
    }

    /**
     * This method defines the behaviour of the `||` binary operator.
     * ```
     * true || false; // Or operator
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the [ClassInstance] on the left of the or.
     * @param interpreter the interpreter that called this method.
     * @param other the [ClassInstance] on the right of the or.
     * @param trace the trace from where it was called.
     * @return the result of the or.
     */
    override fun or(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().or(instance, interpreter, other, trace)
    }

    /**
     * This method defines the behaviour of the `&` binary operator.
     * ```
     * true & false; // Bitwise and operator
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the [ClassInstance] on the left of the and.
     * @param interpreter the interpreter that called this method.
     * @param other the [ClassInstance] on the right of the and.
     * @param trace the trace from where it was called.
     * @return the result of the bitwise and.
     */
    override fun bitAnd(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().bitAnd(instance, interpreter, other, trace)
    }

    /**
     * This method defines the behaviour of the `|` binary operator.
     * ```
     * true | false; // Bitwise or operator
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the [ClassInstance] on the left of the or.
     * @param interpreter the interpreter that called this method.
     * @param other the [ClassInstance] on the right of the or.
     * @param trace the trace from where it was called.
     * @return the result of the bitwise or.
     */
    override fun bitOr(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().bitOr(instance, interpreter, other, trace)
    }

    /**
     * This method defines the behaviour of the `~` binary operator.
     * ```
     * true ~ false; // Xor operator
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the [ClassInstance] on the left of the xor.
     * @param interpreter the interpreter that called this method.
     * @param other the [ClassInstance] on the right of the xor.
     * @param trace the trace from where it was called.
     * @return the result of the xor.
     */
    override fun xor(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().xor(instance, interpreter, other, trace)
    }

    /**
     * This method defines the behaviour of the `<<` binary operator.
     * ```
     * 4 << 2; // Bitwise shift left operator
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the [ClassInstance] that is being shifted.
     * @param interpreter the interpreter that called this method.
     * @param other the [ClassInstance] to shift by.
     * @param trace the trace where it was called.
     * @return the result of the shift.
     */
    override fun shiftLeft(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().shiftLeft(instance, interpreter, other, trace)
    }

    /**
     * This method defines the behaviour of the `>>` binary operator.
     * ```
     * 4 >> 2; // Bitwise shift right operator
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the [ClassInstance] that is being shifted.
     * @param interpreter the interpreter that called this method.
     * @param other the [ClassInstance] to shift by.
     * @param trace the trace where it was called.
     * @return the result of the shift.
     */
    override fun shiftRight(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().shiftRight(instance, interpreter, other, trace)
    }

    /**
     * This method defines the behaviour of the comparison operators, `>=`, `>`, `<`, `<=`.
     * ```
     * 70 > 98; // Comparison operator
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the right hand side being compared.
     * @param interpreter the interpreter that called this method.
     * @param other the left hand side being compared.
     * @param trace the trace where it was called.
     * @return the result of the comparison.
     */
    override fun compare(instance: ClassInstance, interpreter: Interpreter, type: Type, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().compare(instance, interpreter, type, other, trace)
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
                this.staticFields.value[name] = ArucasVariable(instance, name, this.name, assignable)
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