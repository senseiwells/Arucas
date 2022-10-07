package me.senseiwells.arucas.classes

import me.senseiwells.arucas.builtin.*
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.core.Type
import me.senseiwells.arucas.core.Type.*
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.nodes.Expression
import me.senseiwells.arucas.utils.*
import me.senseiwells.arucas.utils.impl.ArucasEnum
import me.senseiwells.arucas.utils.impl.ArucasList
import kotlin.reflect.KClass

abstract class ClassDefinition(
    val name: String,
    val interpreter: Interpreter,
) {
    val constructors = lazy { FunctionMap() }
    val methods = lazy { FunctionMap() }

    val staticFields = lazy { HashMap<String, HintedField>() }
    val staticMethods = lazy { FunctionMap() }

    private val superclasses = lazy { HashSet<ClassDefinition>() }
    private val typeInstance = lazy { this.interpreter.create(TypeDef::class, this) }

    open fun init(interpreter: Interpreter, instance: ClassInstance, args: MutableList<ClassInstance>, trace: CallTrace) {
        if (this.constructors.isInitialized() && !this.constructors.value.isEmpty()) {
            val constructorInstance = this.constructors.value.get("", args.size + 1)
            constructorInstance ?: runtimeError("No such constructor with ${args.size} parameters exists for ${this.name}", trace)

            args.add(0, instance)
            interpreter.call(constructorInstance, args, trace)
        }
    }

    fun getTypeInstance() = this.typeInstance.value

    fun inheritsFrom(vararg classDefinitions: ClassDefinition) = this.inheritsFrom(classDefinitions.asList())

    open fun inheritsFrom(classDefinitions: List<ClassDefinition>): Boolean {
        // The reason we cache them here is that if we do it when the class is
        // constructed we cannot guarantee that the superclass has been defined yet
        if (!this.superclasses.isInitialized()) {
            this.cacheSuperclasses()
        }

        for (definition in classDefinitions) {
            if (this == definition || this.superclasses.value.contains(definition)) {
                return true
            }
        }
        return false
    }

    fun <T: PrimitiveDefinition<*>> inheritsFrom(definition: KClass<out T>) = this.inheritsFrom(definition.java)

    fun <T: PrimitiveDefinition<*>> inheritsFrom(definitionClass: Class<out T>) = this.inheritsFrom(this.getPrimitiveDef(definitionClass))

    fun <T: PrimitiveDefinition<*>> getPrimitiveDef(clazz: Class<out T>) = this.interpreter.getPrimitive(clazz)

    fun <T: PrimitiveDefinition<*>> getPrimitiveDef(klass: KClass<out T>) = this.interpreter.getPrimitive(klass)

    open fun canExtend(): Boolean = true

    open fun canConstructDirectly(): Boolean = this.constructors.isInitialized() && !this.constructors.value.isEmpty()

    open fun interfaces(): Set<InterfaceDefinition> = setOf()

    open fun superclass(): ClassDefinition = this.getPrimitiveDef(ObjectDef::class)

    open fun superclassOf(name: String): ClassDefinition {
        if (this.name == name) {
            return this.superclass()
        }
        return this.superclass().superclassOf(name)
    }

    abstract fun asJavaValue(instance: ClassInstance): Any?

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

    internal open fun accessConstructor(trace: Trace): ClassInstance {
        if (!this.canConstructDirectly()) {
            runtimeError("The class '${this.name}' cannot be constructed", trace)
        }
        runtimeError("The constructor for class '${this.name}' cannot be delegated", trace)
    }

    internal open fun callConstructor(interpreter: Interpreter, args: MutableList<ClassInstance>, trace: CallTrace): ClassInstance {
        if (!this.canConstructDirectly()) {
            runtimeError("The class '${this.name}' cannot be constructed", trace)
        }

        val thisInstance = ClassInstance(this)
        this.init(interpreter, thisInstance, args, trace)
        return thisInstance
    }

    internal open fun call(instance: ClassInstance, interpreter: Interpreter, args: List<ClassInstance>): ClassInstance = this.superclass().call(instance, interpreter, args)

    internal open fun memberFunctionAccess(instance: ClassInstance, name: String, args: MutableList<ClassInstance>, trace: Trace, origin: ClassDefinition = this): ClassInstance {
        if (this.methods.isInitialized()) {
            this.methods.value.get(name, args.size + 1)?.let {
                args.add(0, instance)
                return it
            }
        }
        return this.superclass().memberFunctionAccess(instance, name, args, trace, origin)
    }

    internal open fun hasMemberFunction(name: String): Boolean {
        if (this.methods.isInitialized() && this.methods.value.has(name)) {
            return true
        }
        return this.superclass().hasMemberFunction(name)
    }

    internal open fun hasMemberFunction(name: String, parameters: Int): Boolean {
        if (this.methods.isInitialized() && this.methods.value.has(name, parameters + 1)) {
            return true
        }
        return this.superclass().hasMemberFunction(name, parameters)
    }

    internal fun staticFunctionCall(interpreter: Interpreter, name: String, arguments: List<ClassInstance>, trace: LocatableTrace): ClassInstance {
        val callTrace = CallTrace(trace, "${this.name}.$name::${arguments.size}")
        val function = this.staticFunctionAccess(name, arguments.size, trace)
        return interpreter.call(function, arguments, callTrace)
    }

    internal open fun staticFunctionAccess(name: String, parameters: Int, trace: Trace): ClassInstance {
        if (this.staticMethods.isInitialized()) {
            this.staticMethods.value.get(name, parameters)?.let { return it }
        }
        if (this.staticFields.isInitialized()) {
            this.staticFields.value[name]?.let {
                val field = it.instance
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

        return instanceField?.instance ?: kotlin.run {
            if (instance.definition.hasMemberFunction(name)) {
                // We need to create a branch because we cannot guarantee
                // that this function will be run on the same thread
                val child = interpreter.branch()
                // We create a temporary function that take arbitrary
                // number of parameters that tries to call the instance
                val delegate = BuiltInFunction.arb("\$delegate.<${instance.definition.name}>.$name", {
                    val arguments = ArrayList(it.arguments)
                    instance.callMember(child.branch(), name, arguments, trace)
                })
                return interpreter.create(FunctionDef::class, delegate)
            }
            return@run null
        } ?: runtimeError("No such field '$name' exists for class '${instance.definition.name}'", trace)
    }

    internal open fun staticMemberAccess(interpreter: Interpreter, name: String, trace: LocatableTrace): ClassInstance {
        if (name == "type") {
            return this.getTypeInstance()
        }

        if (this.staticFields.isInitialized()) {
            this.staticFields.value[name]?.let { return it.instance }
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
                it.set(newValue, trace)
                return
            }
        }
        runtimeError("No such static field '$name' exists for class '${this.name}'", trace)
    }

    internal open fun memberAssign(instance: ClassInstance, name: String, assignee: ClassInstance, trace: Trace): ClassInstance {
        val instanceField = instance.getInstanceField(name)
        instanceField ?: runtimeError("No such field '$name' exists for class '${instance.definition.name}'", trace)
        instanceField.set(assignee, trace)
        return assignee
    }

    internal open fun bracketAccess(instance: ClassInstance, interpreter: Interpreter, index: ClassInstance, trace: LocatableTrace): ClassInstance = this.superclass().bracketAccess(instance, interpreter, index, trace)

    internal open fun bracketAssign(instance: ClassInstance, interpreter: Interpreter, index: ClassInstance, assignee: ClassInstance, trace: LocatableTrace): ClassInstance = this.superclass().bracketAssign(instance, interpreter, index, assignee, trace)

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

    protected open fun binary(instance: ClassInstance, interpreter: Interpreter, type: Type, other: ClassInstance, trace: LocatableTrace): ClassInstance {
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

    internal open fun copy(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): ClassInstance {
        return this.superclass().copy(instance, interpreter, trace)
    }

    protected open fun not(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Any? {
        return this.superclass().not(instance, interpreter, trace)
    }

    protected open fun plus(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Any? {
        return this.superclass().plus(instance, interpreter, trace)
    }

    protected open fun minus(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Any? {
        return this.superclass().minus(instance, interpreter, trace)
    }

    protected open fun plus(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().plus(instance, interpreter, other, trace)
    }

    protected open fun minus(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().minus(instance, interpreter, other, trace)
    }

    protected open fun multiply(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().multiply(instance, interpreter, other, trace)
    }

    protected open fun divide(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().divide(instance, interpreter, other, trace)
    }

    protected open fun power(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().power(instance, interpreter, other, trace)
    }

    protected open fun and(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().and(instance, interpreter, other, trace)
    }

    protected open fun or(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().or(instance, interpreter, other, trace)
    }

    protected open fun bitAnd(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().bitAnd(instance, interpreter, other, trace)
    }

    protected open fun bitOr(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().bitOr(instance, interpreter, other, trace)
    }

    protected open fun xor(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().xor(instance, interpreter, other, trace)
    }

    protected open fun shiftLeft(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().shiftLeft(instance, interpreter, other, trace)
    }

    protected open fun shiftRight(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().shiftRight(instance, interpreter, other, trace)
    }

    protected open fun compare(instance: ClassInstance, interpreter: Interpreter, type: Type, other: ClassInstance, trace: LocatableTrace): Any? {
        return this.superclass().compare(instance, interpreter, type, other, trace)
    }

    internal open fun compare(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Int {
        return this.superclass().compare(instance, interpreter, other, trace)
    }

    internal open fun notEquals(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Boolean {
        return this.superclass().notEquals(instance, interpreter, other, trace)
    }

    internal open fun equals(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Boolean {
        return this.superclass().equals(instance, interpreter, other, trace)
    }

    internal open fun hashCode(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Int {
        return this.superclass().hashCode(instance, interpreter, trace)
    }

    internal open fun toString(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): String {
        return this.superclass().toString(instance, interpreter, trace)
    }

    final override fun equals(other: Any?) = this === other

    final override fun hashCode() = this.name.hashCode()
}

class InterfaceDefinition(
    name: String,
    interpreter: Interpreter,
    private val requiredMethods: List<Pair<String, Int>>
): ClassDefinition(name, interpreter) {
    fun hasRequiredMethods(classDefinition: ClassDefinition): Boolean {
        for ((name, parameters) in this.requiredMethods) {
            if (!classDefinition.hasMemberFunction(name, parameters + 1)) {
                return false
            }
        }
        return true
    }

    override fun init(interpreter: Interpreter, instance: ClassInstance, args: MutableList<ClassInstance>, trace: CallTrace) {
        runtimeError("Cannot create an interface class", trace)
    }

    override fun inheritsFrom(classDefinitions: List<ClassDefinition>): Boolean {
        return false
    }

    override fun asJavaValue(instance: ClassInstance): Any? {
        throw IllegalStateException("Tried to convert interface instance into java value, this is a bug!")
    }

    override fun accessConstructor(trace: Trace): ClassInstance {
        runtimeError("Cannot construct an interface class", trace)
    }

    override fun callConstructor(interpreter: Interpreter, args: MutableList<ClassInstance>, trace: CallTrace): ClassInstance {
        runtimeError("Cannot construct an interface class", trace)
    }

    override fun memberFunctionAccess(instance: ClassInstance, name: String, args: MutableList<ClassInstance>, trace: Trace, origin: ClassDefinition): ClassInstance {
        throw IllegalStateException("Tried to access method in an interface class, this is a bug!")
    }

    override fun hasMemberFunction(name: String): Boolean {
        throw IllegalStateException("Tried to check methods in an interface class, this is a bug!")
    }

    override fun hasMemberFunction(name: String, parameters: Int): Boolean {
        throw IllegalStateException("Tried to check methods in an interface class, this is a bug!")
    }

    override fun memberAccess(instance: ClassInstance, interpreter: Interpreter, name: String, trace: LocatableTrace): ClassInstance {
        throw IllegalStateException("Tried to access field in an interface class, this is a bug!")
    }

    override fun memberAssign(instance: ClassInstance, name: String, assignee: ClassInstance, trace: Trace): ClassInstance {
        throw IllegalStateException("Tried to assign field in an interface class, this is a bug!")
    }
}

abstract class PrimitiveDefinition<T: Any>(
    name: String,
    interpreter: Interpreter
): ClassDefinition(name, interpreter) {
    override fun init(interpreter: Interpreter, instance: ClassInstance, args: MutableList<ClassInstance>, trace: CallTrace) {
        super.init(interpreter, instance, args, trace)
        if (instance.getPrimitive(this) == null) {
            throw IllegalStateException("Primitive '${this.name}' was not initialised correctly")
        }
    }
    override fun accessConstructor(trace: Trace): ClassInstance {
        if (!this.canConstructDirectly()) {
            runtimeError("The cannot construct class '${this.name}'", trace)
        }
        return super.accessConstructor(trace)
    }

    override fun callConstructor(interpreter: Interpreter, args: MutableList<ClassInstance>, trace: CallTrace): ClassInstance {
        if (!this.canConstructDirectly()) {
            runtimeError("The cannot construct class '${this.name}'", trace)
        }
        return super.callConstructor(interpreter, args, trace)
    }

    protected fun construct(): ClassInstance {
        return ClassInstance(this)
    }

    protected open fun create(value: T): ClassInstance {
        return ClassInstance(this).also { it.setPrimitive(this, value) }
    }

    override fun superclass(): PrimitiveDefinition<in T> {
        return this.getPrimitiveDef(ObjectDef::class)
    }

    final override fun interfaces(): Set<InterfaceDefinition> {
        return super.interfaces()
    }

    open fun defineStaticFields(): List<Triple<String, Any?, Boolean>>? = null

    open fun defineConstructors(): List<ConstructorFunction>? = null

    open fun defineMethods(): List<MemberFunction>? = null

    open fun defineStaticMethods(): List<BuiltInFunction>? = null

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

    override fun asJavaValue(instance: ClassInstance): Any? = instance.asPrimitive(this)
}

abstract class CreatableDefinition<T: Any>(
    name: String,
    interpreter: Interpreter
): PrimitiveDefinition<T>(name, interpreter) {
    public override fun create(value: T): ClassInstance {
        return super.create(value)
    }
}

open class ArucasClassDefinition(
    name: String,
    interpreter: Interpreter,
    private val localTable: StackTable,
    private val superclass: ClassDefinition?,
    private val interfaces: Set<InterfaceDefinition>?
): ClassDefinition(name, interpreter) {
    val fields = lazy { HashMap<Parameter, Expression>() }
    val operators = lazy { OperatorMap() }

    init {
        this.constructors.value // Unlazy
    }

    override fun init(interpreter: Interpreter, instance: ClassInstance, args: MutableList<ClassInstance>, trace: CallTrace) {
        if (this.fields.isInitialized()) {
            for ((argument, expression) in this.fields.value.entries) {
                val fieldName = "<${this.name}>.${argument.name}"
                val value = interpreter.evaluate(this.localTable, expression)
                val field = HintedField(fieldName, argument.definitions(this.localTable, trace), true, value)
                instance.addInstanceField(argument.name, field)
            }
        }

        super.init(interpreter, instance, args, trace)

        for (field in instance.getInstanceFields()) {
            field.checkInstanceType(trace)
        }
    }

    override fun canConstructDirectly(): Boolean {
        return true
    }

    override fun superclass(): ClassDefinition {
        return this.superclass ?: super.superclass()
    }

    override fun interfaces(): Set<InterfaceDefinition> {
        return this.interfaces ?: super.interfaces()
    }

    override fun asJavaValue(instance: ClassInstance) = instance

    override fun hasMemberFunction(name: String, parameters: Int): Boolean {
        return this.methods.isInitialized() && this.methods.value.has(name, parameters)
    }

    override fun bracketAccess(instance: ClassInstance, interpreter: Interpreter, index: ClassInstance, trace: LocatableTrace): ClassInstance {
        if (this.operators.isInitialized()) {
            this.operators.value.get(LEFT_SQUARE_BRACKET, 2)?.let {
                return interpreter.call(it, listOf(instance, index), CallTrace(trace, "${instance.definition.name}[${index.definition.name}]"))
            }
        }
        return super.bracketAccess(instance, interpreter, index, trace)
    }

    override fun bracketAssign(instance: ClassInstance, interpreter: Interpreter, index: ClassInstance, assignee: ClassInstance, trace: LocatableTrace): ClassInstance {
        if (this.operators.isInitialized()) {
            this.operators.value.get(LEFT_SQUARE_BRACKET, 3)?.let {
                return interpreter.call(it, listOf(instance, index, assignee), CallTrace(trace, "${instance.definition.name}[${index.definition.name}] = ${assignee.definition.name}"))
            }
        }
        return super.bracketAssign(instance, interpreter, index, assignee, trace)
    }

    override fun unary(instance: ClassInstance, interpreter: Interpreter, type: Type, trace: LocatableTrace): ClassInstance {
        if (this.operators.isInitialized()) {
            this.operators.value.get(type, 1)?.let {
                return interpreter.call(it, listOf(instance), CallTrace(trace, "$type${instance.definition.name}"))
            }
        }
        return super.unary(instance, interpreter, type, trace)
    }

    override fun binary(instance: ClassInstance, interpreter: Interpreter, type: Type, other: ClassInstance, trace: LocatableTrace): ClassInstance {
        if (this.operators.isInitialized()) {
            this.operators.value.get(type, 2)?.let {
                return interpreter.call(it, listOf(instance, other), CallTrace(trace, "${instance.definition.name} $type ${other.definition.name}"))
            }
        }
        return super.binary(instance, interpreter, type, other, trace)
    }

    override fun copy(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): ClassInstance {
        if (instance.definition.hasMemberFunction("copy", 1)) {
            return instance.callMember(interpreter, "copy", listOf(), instance.definition, trace)
        }
        return super.copy(instance, interpreter, trace)
    }

    override fun equals(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Boolean {
        if (this.operators.isInitialized()) {
            this.operators.value.get(EQUALS, 2)?.let {
                val callTrace = CallTrace(trace, "${instance.definition.name} == ${other.definition.name}")
                val returnVal = interpreter.call(it, listOf(instance, other), callTrace)
                return returnVal.getPrimitive(BooleanDef::class) ?: runtimeError("Expected '==' operator to return a Boolean")
            }
        }
        return super.equals(instance, interpreter, other, trace)
    }

    override fun hashCode(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Int {
        if (this.hasMemberFunction("hashCode", 1)) {
            return instance.callMemberPrimitive(interpreter, "hashCode", listOf(), NumberDef::class, trace).toInt()
        }
        return super.hashCode(instance, interpreter, trace)
    }

    override fun toString(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): String {
        if (this.hasMemberFunction("toString", 1)) {
            return instance.callMemberPrimitive(interpreter, "toString", listOf(), StringDef::class, trace)
        }
        return super.toString(instance, interpreter, trace)
    }
}

class EnumDefinition(
    name: String,
    interpreter: Interpreter,
    localTable: StackTable,
    interfaces: Set<InterfaceDefinition>?
): ArucasClassDefinition(name, interpreter, localTable, interpreter.getPrimitive(EnumDef::class), interfaces) {
    private val enums = lazy { HashMap<String, ClassInstance>() }

    init {
        val valuesMethod = BuiltInFunction.of("values", this::values)
        val fromStringMethod = BuiltInFunction.of("fromString", this::fromString)
        this.staticMethods.value.add(this.interpreter.create(FunctionDef::class, valuesMethod))
        this.staticMethods.value.add(this.interpreter.create(FunctionDef::class, fromStringMethod))
    }

    fun addEnum(interpreter: Interpreter, name: String, arguments: MutableList<ClassInstance>, trace: LocatableTrace) {
        val callTrace = CallTrace(trace, "new ${this.name}::${arguments.size}")
        val enum = ClassInstance(this)
        enum.setPrimitive(this.superclass(), ArucasEnum(name, this.enums.value.size))
        this.init(interpreter, enum, arguments, callTrace)
        this.enums.value[name] = enum
    }

    fun getEnum(name: String): ClassInstance? {
        return if (this.enums.isInitialized()) this.enums.value[name] else null;
    }

    fun getNames(): Collection<String> {
        return if (!this.enums.isInitialized()) listOf() else this.enums.value.keys
    }

    override fun canExtend(): Boolean {
        return false
    }

    override fun superclass(): EnumDef {
        return super.superclass() as EnumDef
    }

    override fun accessConstructor(trace: Trace): Nothing {
        runtimeError("Enums cannot be constructed", trace)
    }

    override fun callConstructor(interpreter: Interpreter, args: MutableList<ClassInstance>, trace: CallTrace): Nothing {
        this.accessConstructor(trace)
    }

    override fun staticMemberAccess(interpreter: Interpreter, name: String, trace: LocatableTrace): ClassInstance {
        if (this.enums.isInitialized()) {
            this.enums.value[name]?.let { return it }
        }
        return super.staticMemberAccess(interpreter, name, trace)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun values(arguments: Arguments): ArucasList {
        val list = ArucasList()
        if (this.enums.isInitialized()) {
            list.addAll(this.enums.value.values)
        }
        return list
    }

    private fun fromString(arguments: Arguments): ClassInstance {
        val name = arguments.nextPrimitive(StringDef::class)
        return this.getEnum(name) ?: arguments.interpreter.getNull()
    }
}