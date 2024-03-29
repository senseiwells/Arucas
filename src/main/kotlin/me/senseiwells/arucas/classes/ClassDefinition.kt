package me.senseiwells.arucas.classes

import me.senseiwells.arucas.builtin.FunctionDef
import me.senseiwells.arucas.builtin.ObjectDef
import me.senseiwells.arucas.builtin.TypeDef
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.compiler.CallTrace
import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.compiler.Trace
import me.senseiwells.arucas.compiler.token.Type
import me.senseiwells.arucas.compiler.token.Type.*
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.extensions.JavaDef
import me.senseiwells.arucas.functions.builtin.BuiltInFunction
import me.senseiwells.arucas.interpreter.Interpreter
import me.senseiwells.arucas.typed.ArucasVariable
import me.senseiwells.arucas.utils.collections.FunctionMap
import kotlin.reflect.KClass

/**
 * The [ClassDefinition] which defines the behaviour of a [ClassInstance]
 * as well as the behaviour of the class itself.
 *
 * This class stores the static fields and methods as well as constructors
 * for creating [ClassInstance]. All methods of the [ClassInstance] are
 * also defined here as well as all the operators that the [ClassInstance]s
 * can be used with. Each [ClassDefinition] is stored in the [Interpreter] per
 * instance, definitions should **NOT** be shared over different
 * root interpreters as they will not be similar.
 *
 * There are different types of definition, the main two being
 * [PrimitiveDefinition] and [ArucasClassDefinition] being
 * the definition that represents a definition defined in Java/Kotlin
 * or Arucas respectively.
 *
 * @param name the name of the class.
 * @param interpreter the interpreter that the definition was defined on.
 * @see ClassInstance
 * @see PrimitiveDefinition
 * @see ArucasClassDefinition
 */
abstract class ClassDefinition(
    /**
     * The name of the class.
     */
    val name: String,
    /**
     * The interpreter that the definition was defined on.
     */
    val interpreter: Interpreter,
) {
    /**
     * A set of all superclasses for quick lookups.
     */
    private val superclasses = lazy { HashSet<ClassDefinition>() }

    /**
     * The [TypeDef] type instance of the definition.
     */
    private val typeInstance = lazy { this.interpreter.create(TypeDef::class, this) }

    /**
     * The constructors for the class.
     */
    internal val constructors = lazy { FunctionMap() }

    /**
     * The methods that the [ClassInstance] are able to call.
     */
    internal val methods = lazy { FunctionMap() }

    /**
     * The static fields that belong to the definition.
     */
    internal val staticFields = lazy { HashMap<String, ArucasVariable>() }

    /**
     * The static methods that belong to the definition.
     */
    internal val staticMethods = lazy { FunctionMap() }

    /**
     * This method initialises a [ClassInstance] of this definition by calling the respective constructor.
     *
     * @param interpreter the interpreter that instantiated the [ClassInstance].
     * @param instance the instance that is to be instantiated.
     * @param args the arguments parsed into the instantiation for the constructor.
     * @param trace the trace that this was called from.
     */
    open fun init(interpreter: Interpreter, instance: ClassInstance, args: MutableList<ClassInstance>, trace: CallTrace) {
        if (this.constructors.isInitialized() && !this.constructors.value.isEmpty()) {
            val constructorInstance = this.constructors.value.get("", args.size + 1)
            constructorInstance ?: runtimeError("No such constructor with ${args.size} parameters exists for ${this.name}", trace)

            if (!constructorInstance.asPrimitive(FunctionDef::class).accessible(interpreter)) {
                runtimeError("The constructor for '$name' with ${args.size} parameters is inaccessible", trace)
            }

            args.add(0, instance)
            interpreter.call(constructorInstance, args, trace)
        }
    }

    /**
     * Whether this definition can be extended by another definition.
     *
     * This does not apply for [PrimitiveDefinition] only [ArucasClassDefinition].
     * And this should return `false` if you want your class to be final.
     *
     * @return whether the class should be able to be extended in Arucas.
     */
    open fun canExtend(): Boolean {
        return true
    }

    /**
     * Whether this definition can be constructed with the `new` keywords.
     *
     * If this returns false then the definition can be considered `abstract`
     * as it will still be able to be called from `super()`.
     *
     * @return whether the class can be constructed directly.
     */
    open fun canConstructDirectly(): Boolean {
        return this.constructors.isInitialized() && !this.constructors.value.isEmpty()
    }

    /**
     * This returns the set of interfaces that the class is implementing.
     *
     * @return the set of interfaces.
     */
    open fun interfaces(): Set<InterfaceDefinition> {
        return setOf()
    }

    /**
     * This returns the superclass of the current definition,
     * by default this is [ObjectDef].
     *
     * @return the superclass definition.
     */
    open fun superclass(): ClassDefinition {
        return this.getPrimitiveDef(ObjectDef::class)
    }

    /**
     * This returns one of the superclass' with a given name.
     *
     * If no superclass with [name] is found then [ObjectDef] is returned.
     *
     * @param name the name of the wanted class.
     * @return the superclass definition.
     */
    open fun superclassOf(name: String): ClassDefinition {
        if (this.name == name) {
            return this.superclass()
        }
        return this.superclass().superclassOf(name)
    }

    /**
     * This checks whether the class definition inherits,
     * or is, a given [definition].
     *
     * @param definition the definition to check.
     * @return whether the class definition inherits from [definition].
     */
    open fun inheritsFrom(definition: ClassDefinition): Boolean {
        // The reason we cache them here is that if we do it when the class is
        // constructed we cannot guarantee that the superclass has been defined yet
        if (!this.superclasses.isInitialized()) {
            this.cacheSuperclasses()
        }
        return this == definition || this.superclasses.value.contains(definition)
    }

    /**
     * Checks whether the class inherits from all the classes in a list.
     * If the list is empty then this will return true.
     *
     * @param classDefinitions a list of all the definitions.
     * @return whether the class inherits from all the definitions.
     * @see inheritsFrom
     */
    fun inheritsFrom(classDefinitions: List<ClassDefinition>): Boolean {
        if (classDefinitions.isEmpty()) {
            return true
        }
        for (definition in classDefinitions) {
            if (this.inheritsFrom(definition)) {
                return true
            }
        }
        return false
    }

    /**
     * This checks whether the class definition inherits,
     * or is, a given definition.
     *
     * @param T the definition type.
     * @param definitionKlass the class of the definition.
     * @return whether the class definition inherits from definition.
     */
    fun <T: PrimitiveDefinition<*>> inheritsFrom(definitionKlass: KClass<out T>): Boolean {
        return this.inheritsFrom(definitionKlass.java)
    }

    /**
     * This checks whether the class definition inherits,
     * or is, a given definition.
     *
     * @param T the definition type.
     * @param definitionClass the class of the definition.
     * @return whether the class definition inherits from definition.
     */
    fun <T: PrimitiveDefinition<*>> inheritsFrom(definitionClass: Class<out T>): Boolean {
        return this.inheritsFrom(this.getPrimitiveDef(definitionClass))
    }

    /**
     * Gets the [ClassInstance] of the definition [TypeDef]
     * of the class definition.
     *
     * @return the type class instance.
     */
    fun getTypeInstance(): ClassInstance {
        return this.typeInstance.value
    }

    /**
     * Gets a primitive class definition for it's class.
     *
     * @param T the primitive definition type.
     * @param clazz the primitive definition class.
     * @return the primitive definition.
     */
    fun <T: PrimitiveDefinition<*>> getPrimitiveDef(clazz: Class<out T>): T {
        return this.interpreter.getPrimitive(clazz)
    }

    /**
     * Gets a primitive class definition for it's class.
     *
     * @param T the primitive definition type.
     * @param klass the primitive definition class.
     * @return the primitive definition.
     */
    fun <T: PrimitiveDefinition<*>> getPrimitiveDef(klass: KClass<out T>): T {
        return this.interpreter.getPrimitive(klass)
    }

    /**
     * This method defines the behaviour of bracket-accessing the object.
     * ```
     * list = ["a", "b", "c"];
     * list[1]; // Bracket access
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the instance being bracket-accessed.
     * @param interpreter the interpreter that called this method.
     * @param index the value passed as the 'index'.
     * @param trace the trace from where it was called.
     * @return the [ClassInstance] from accessing the [instance].
     */
    open fun bracketAccess(instance: ClassInstance, interpreter: Interpreter, index: ClassInstance, trace: LocatableTrace): ClassInstance {
        return this.superclass().bracketAccess(instance, interpreter, index, trace)
    }

    /**
     * This method defines the behaviour of bracket-assigning the object.
     * ```
     * list = [null];
     * list[0] = "foobar"; // Bracket assign
     * ```
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the instance being bracket-assigned.
     * @param interpreter the interpreter that called this method.
     * @param index the 'index' to assign to.
     * @param assignee the [ClassInstance] to assign to the [index].
     * @param trace the trace from where it was called.
     * @return the result of the bracket-assignment.
     */
    open fun bracketAssign(instance: ClassInstance, interpreter: Interpreter, index: ClassInstance, assignee: ClassInstance, trace: LocatableTrace): ClassInstance {
        return this.superclass().bracketAssign(instance, interpreter, index, assignee, trace)
    }

    /**
     * This method defines the behaviour of the `copy` method on the object.
     *
     * If the behaviour is not overridden, the default behaviour from [ObjectDef] is used.
     *
     * @param instance the instance to be copied.
     * @param interpreter the interpreter that called this method.
     * @param trace the trace from where it was called.
     * @return the copied instance.
     */
    open fun copy(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): ClassInstance {
        return this.superclass().copy(instance, interpreter, trace)
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
    open fun equals(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Boolean {
        return this.superclass().equals(instance, interpreter, other, trace)
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
    open fun hashCode(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Int {
        return this.superclass().hashCode(instance, interpreter, trace)
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
    open fun toString(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): String {
        return this.superclass().toString(instance, interpreter, trace)
    }

    /**
     * This method is called when the value is converted into a [JavaDef].
     *
     * @param instance the instance to convert into a [Object].
     * @return the java [Object] counterpart.
     * @see PrimitiveDefinition.asJavaValue
     */
    open fun asJavaValue(instance: ClassInstance): Any? {
        return null
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
    protected open fun not(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Any? {
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
    protected open fun plus(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Any? {
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
    protected open fun minus(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Any? {
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
    protected open fun plus(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
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
    protected open fun minus(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
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
    protected open fun multiply(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
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
    protected open fun divide(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
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
    protected open fun power(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
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
    protected open fun and(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
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
    protected open fun or(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
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
    protected open fun bitAnd(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
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
    protected open fun bitOr(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
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
    protected open fun xor(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
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
    protected open fun shiftLeft(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
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
    protected open fun shiftRight(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
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
    protected open fun compare(instance: ClassInstance, interpreter: Interpreter, type: Type, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().compare(instance, interpreter, type, other, trace)
    }

    internal open fun cacheSuperclasses() {
        // We cache our superclasses to make
        // type checks much faster
        val superclass = this.superclass()
        val superclassSuperclasses = superclass.superclasses
        if (!superclassSuperclasses.isInitialized()) {
            superclass.cacheSuperclasses()
        }
        this.superclasses.value.add(superclass)
        this.superclasses.value.addAll(superclassSuperclasses.value)
        this.superclasses.value.addAll(this.interfaces())
    }

    internal open fun accessConstructor(interpreter: Interpreter, trace: LocatableTrace): ClassInstance {
        if (!this.canConstructDirectly()) {
            runtimeError("The class '${this.name}' cannot be constructed", trace)
        }

        val branch = interpreter.branch()
        val delegate = BuiltInFunction.arb("\$delegate.<${this.name}>.constructor", {
            val arguments = ArrayList(it.arguments)
            callConstructor(branch, arguments, CallTrace(trace, "new ${this.name}::${arguments.size}"))
        })
        return interpreter.create(FunctionDef::class, delegate)
    }

    internal open fun callConstructor(interpreter: Interpreter, args: MutableList<ClassInstance>, trace: CallTrace): ClassInstance {
        if (!this.canConstructDirectly()) {
            runtimeError("The class '${this.name}' cannot be constructed", trace)
        }

        val thisInstance = ClassInstance(this)
        this.init(interpreter, thisInstance, args, trace)

        for (field in thisInstance.getInstanceFields()) {
            field.finalise(trace)
        }
        return thisInstance
    }

    internal open fun call(instance: ClassInstance, interpreter: Interpreter, args: List<ClassInstance>): ClassInstance {
        return this.superclass().call(instance, interpreter, args)
    }

    internal open fun memberFunctionAccess(instance: ClassInstance, interpreter: Interpreter, name: String, args: MutableList<ClassInstance>, trace: Trace, origin: ClassDefinition = this): ClassInstance {
        if (this.methods.isInitialized()) {
            this.methods.value.get(name, args.size + 1)?.let {
                if (!it.asPrimitive(FunctionDef::class).accessible(interpreter)) {
                    runtimeError("The method '$name' with ${args.size} parameters is inaccessible", trace)
                }
                args.add(0, instance)
                return it
            }
        }
        return this.superclass().memberFunctionAccess(instance, interpreter, name, args, trace, origin)
    }

    internal open fun hasMemberFunction(instance: ClassInstance, name: String): Boolean {
        if (this.methods.isInitialized() && this.methods.value.has(name)) {
            return true
        }
        return this.superclass().hasMemberFunction(instance, name)
    }

    internal open fun hasMemberFunction(instance: ClassInstance, name: String, parameters: Int): Boolean {
        if (this.methods.isInitialized() && this.methods.value.has(name, parameters + 1)) {
            return true
        }
        return this.superclass().hasMemberFunction(instance, name, parameters)
    }

    // Returns true if the class has the function, and it is accessible, false otherwise
    internal open fun canOverride(name: String, parameters: Int): Boolean {
        if (this.methods.isInitialized()) {
            val function = this.methods.value.get(name, parameters)
            if (function !== null) {
                return !function.asPrimitive(FunctionDef::class).private
            }
        }
        return this.superclass().canOverride(name, parameters)
    }

    internal fun staticFunctionCall(interpreter: Interpreter, name: String, arguments: List<ClassInstance>, trace: LocatableTrace): ClassInstance {
        val callTrace = CallTrace(trace, "${this.name}.$name::${arguments.size}")
        val function = this.staticFunctionAccess(interpreter, name, arguments.size, trace)
        return interpreter.call(function, arguments, callTrace)
    }

    internal open fun staticFunctionAccess(interpreter: Interpreter, name: String, parameters: Int, trace: Trace): ClassInstance {
        if (this.staticMethods.isInitialized()) {
            this.staticMethods.value.get(name, parameters)?.let {
                if (!it.asPrimitive(FunctionDef::class).accessible(interpreter)) {
                    runtimeError("The static method '$name' with $parameters parameters is inaccessible", trace)
                }
                return it
            }
        }
        if (this.staticFields.isInitialized()) {
            this.staticFields.value[name]?.let {
                val field = it.get(interpreter, trace)
                if (field.definition.inheritsFrom(FunctionDef::class)) {
                    return field
                }
            }
        }
        val error = if (parameters == 0) "" else " with $parameters parameter${if (parameters == 1) "" else "s"}"
        runtimeError("Method '${this.name}.$name'$error is not defined", trace)
    }

    internal open fun memberAccess(instance: ClassInstance, interpreter: Interpreter, name: String, trace: LocatableTrace): ClassInstance {
        val instanceField = instance.getInstanceField(name)

        return instanceField?.get(interpreter, trace) ?: kotlin.run {
            if (instance.definition.hasMemberFunction(instance, name)) {
                // We need to create a branch because we cannot guarantee
                // that this function will be run on the same thread
                val branch = interpreter.branch()
                // We create a temporary function that take arbitrary
                // number of parameters that tries to call the instance
                val delegate = BuiltInFunction.arb("\$delegate.<${instance.definition.name}>.$name", {
                    val arguments = ArrayList(it.arguments)
                    instance.callMember(branch.branch(), name, arguments, trace)
                })
                return interpreter.create(FunctionDef::class, delegate)
            }
            null
        } ?: runtimeError("No such field '$name' exists for class '${instance.definition.name}'", trace)
    }

    internal open fun memberAssign(instance: ClassInstance, interpreter: Interpreter, name: String, assignee: ClassInstance, trace: Trace): ClassInstance {
        val instanceField = instance.getInstanceField(name)
        instanceField ?: runtimeError("No such field '$name' exists for class '${instance.definition.name}'", trace)
        instanceField.set(assignee, interpreter, trace)
        return assignee
    }

    internal open fun staticMemberAccess(interpreter: Interpreter, name: String, trace: LocatableTrace): ClassInstance {
        if (name == "type") {
            return this.getTypeInstance()
        }

        if (this.staticFields.isInitialized()) {
            this.staticFields.value[name]?.let { return it.get(interpreter, trace) }
        }
        if (this.staticMethods.isInitialized() && this.staticMethods.value.has(name)) {
            val child = interpreter.branch()
            val delegate = BuiltInFunction.arb("\$delegate.${this.name}.$name", {
                this.staticFunctionCall(child.branch(), name, it.arguments, trace)
            })
            return this.interpreter.create(FunctionDef::class, delegate)
        }
        runtimeError("No such static field '$name' exists for class '${this.name}'", trace)
    }

    internal open fun staticMemberAssign(interpreter: Interpreter, name: String, newValue: ClassInstance, trace: LocatableTrace) {
        if (this.staticFields.isInitialized()) {
            this.staticFields.value[name]?.let {
                it.set(newValue, interpreter, trace)
                return
            }
        }
        runtimeError("No such static field '$name' exists for class '${this.name}'", trace)
    }

    internal open fun unary(instance: ClassInstance, interpreter: Interpreter, type: Type, trace: LocatableTrace): ClassInstance {
        return interpreter.convertValue(when (type) {
            NOT -> this.not(instance, interpreter, trace)
            MINUS -> this.minus(instance, interpreter, trace)
            PLUS -> this.plus(instance, interpreter, trace)
            else -> runtimeError("Unknown unary operator '$type'", trace)
        })
    }

    internal open fun binary(instance: ClassInstance, interpreter: Interpreter, type: Type, other: () -> ClassInstance, trace: LocatableTrace): ClassInstance {
        return this.binary(instance, interpreter, type, other(), trace)
    }

    internal open fun binary(instance: ClassInstance, interpreter: Interpreter, type: Type, other: ClassInstance, trace: LocatableTrace): ClassInstance {
        return interpreter.convertValue(when (type) {
            PLUS -> this.plus(instance, interpreter, other, trace)
            MINUS -> this.minus(instance, interpreter, other, trace)
            MULTIPLY -> this.multiply(instance, interpreter, other, trace)
            DIVIDE -> this.divide(instance, interpreter, other, trace)
            POWER -> this.power(instance, interpreter, other, trace)
            AND -> this.and(instance, interpreter, other, trace)
            OR -> this.or(instance, interpreter, other, trace)
            BIT_AND -> this.bitAnd(instance, interpreter, other, trace)
            BIT_OR -> this.bitOr(instance, interpreter, other, trace)
            XOR -> this.xor(instance, interpreter, other, trace)
            SHIFT_LEFT -> this.shiftLeft(instance, interpreter, other, trace)
            SHIFT_RIGHT -> this.shiftRight(instance, interpreter, other, trace)
            EQUALS -> this.equals(instance, interpreter, other, trace)
            NOT_EQUALS -> this.notEquals(instance, interpreter, other, trace)
            LESS_THAN, LESS_THAN_EQUAL, MORE_THAN, MORE_THAN_EQUAL -> this.compare(instance, interpreter, type, other, trace)
            else -> runtimeError("Unknown binary operator '$type'", trace)
        })
    }

    internal open fun compare(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Int {
        return this.superclass().compare(instance, interpreter, other, trace)
    }

    internal open fun notEquals(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Boolean {
        return this.superclass().notEquals(instance, interpreter, other, trace)
    }

    final override fun equals(other: Any?): Boolean {
        return this === other
    }

    final override fun hashCode(): Int {
        return this.name.hashCode()
    }

    final override fun toString(): String {
        return this.name
    }
}
