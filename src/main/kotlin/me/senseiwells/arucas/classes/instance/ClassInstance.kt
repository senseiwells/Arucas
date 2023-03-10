package me.senseiwells.arucas.classes.instance

import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.compiler.CallTrace
import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.compiler.Trace
import me.senseiwells.arucas.compiler.token.Type
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.interpreter.Interpreter
import me.senseiwells.arucas.typed.ArucasVariable
import kotlin.reflect.KClass

/**
 * This class represents an instance of a [ClassDefinition].
 * This holds a reference to its [definition] as it defines
 * the instance's behaviour. However, a [ClassInstance] still
 * holds its fields, as well as a primitive value (if the defining
 * definition is a [PrimitiveDefinition]).
 *
 * @param definition the definition that this [ClassInstance] is an instance of.
 * @see ClassDefinition
 */
class ClassInstance internal constructor(
    /**
     * The definition that this [ClassInstance] is an instance of.
     */
    val definition: ClassDefinition
) {
    private val instanceFields by lazy { LinkedHashMap<String, ArucasVariable>() }
    private var primitive: Any? = null

    @Deprecated("This method should not be called directly", ReplaceWith("interpreter.call(instance, args)"))
    internal fun call(interpreter: Interpreter, args: List<ClassInstance>) = this.definition.call(this, interpreter, args)

    @JvmOverloads
    fun callMember(interpreter: Interpreter, name: String, args: List<ClassInstance>, trace: LocatableTrace, functionName: String = "<${this.definition.name}>.$name::${args.size}"): ClassInstance {
        val callTrace = CallTrace(trace, functionName)
        val arguments = ArrayList(args)
        val function = this.definition.memberFunctionAccess(this, interpreter, name, arguments, trace)
        return interpreter.call(function, arguments, callTrace)
    }

    fun unary(interpreter: Interpreter, type: Type, trace: LocatableTrace): ClassInstance {
        return this.definition.unary(this, interpreter, type, trace)
    }

    fun binary(interpreter: Interpreter, type: Type, other: () -> ClassInstance, trace: LocatableTrace): ClassInstance {
        return this.definition.binary(this, interpreter, type, other, trace)
    }

    fun memberAccess(interpreter: Interpreter, name: String, trace: LocatableTrace): ClassInstance {
        return this.definition.memberAccess(this, interpreter, name, trace)
    }

    fun memberAssign(interpreter: Interpreter, name: String, assignee: ClassInstance, trace: Trace): ClassInstance {
        return this.definition.memberAssign(this, interpreter, name, assignee, trace)
    }

    fun bracketAccess(index: ClassInstance, interpreter: Interpreter, trace: LocatableTrace = Trace.INTERNAL): ClassInstance {
        return this.definition.bracketAccess(this, interpreter, index, trace)
    }

    fun bracketAssign(index: ClassInstance, interpreter: Interpreter, assignee: ClassInstance, trace: LocatableTrace = Trace.INTERNAL): ClassInstance {
        return this.definition.bracketAssign(this, interpreter, index, assignee, trace)
    }

    fun addInstanceField(name: String, field: ArucasVariable): ArucasVariable? {
        return this.instanceFields.putIfAbsent(name, field)
    }

    fun getInstanceField(name: String): ArucasVariable? {
        return this.instanceFields[name]
    }

    fun getInstanceFields(): Iterable<ArucasVariable> {
        return this.instanceFields.values
    }

    fun <T: Any> setPrimitive(definition: PrimitiveDefinition<T>, value: T) {
        if (!this.isOf(definition)) {
            throw IllegalStateException("Tried to set '${definition.name}' to '${this.definition}' instance")
        }
        this.primitive = value
    }

    fun <T: PrimitiveDefinition<V>, V: Any> getPrimitive(klass: KClass<out T>): V? {
        return this.getPrimitive(klass.java)
    }

    fun <T: PrimitiveDefinition<V>, V: Any> getPrimitive(clazz: Class<out T>): V? {
        return this.getPrimitive(this.definition.getPrimitiveDef(clazz))
    }

    fun <T: Any> getPrimitive(definition: PrimitiveDefinition<T>): T? {
        if (this.isOf(definition)) {
            @Suppress("UNCHECKED_CAST")
            return this.primitive as? T
        }
        return null
    }

    fun <T: PrimitiveDefinition<V>, V: Any> asPrimitive(klass: KClass<out T>): V {
        return this.asPrimitive(klass.java)
    }

    fun <T: PrimitiveDefinition<V>, V: Any> asPrimitive(clazz: Class<out T>): V {
        return this.asPrimitive(this.definition.getPrimitiveDef(clazz))
    }

    fun <T: Any> asPrimitive(definition: PrimitiveDefinition<T>): T {
        return this.getPrimitive(definition)!!
    }

    fun isOf(definition: ClassDefinition): Boolean {
        return this.definition.inheritsFrom(definition)
    }

    fun isOf(klass: KClass<out PrimitiveDefinition<*>>): Boolean {
        return this.isOf(this.definition.getPrimitiveDef(klass))
    }

    fun isOf(clazz: Class<out PrimitiveDefinition<*>>): Boolean {
        return this.isOf(this.definition.getPrimitiveDef(clazz))
    }

    fun callMember(interpreter: Interpreter, name: String, args: List<ClassInstance>, returnType: ClassDefinition, trace: LocatableTrace): ClassInstance {
        val functionName = "<${this.definition.name}>.$name::${args.size}"
        val instance = this.callMember(interpreter, name, args, trace, functionName)
        if (!instance.isOf(returnType)) {
            runtimeError("Expected function '$functionName' to return '${returnType.name}'", trace)
        }
        return instance
    }

    fun <T: PrimitiveDefinition<*>> callMember(interpreter: Interpreter, name: String, args: List<ClassInstance>, returnType: Class<T>, trace: LocatableTrace): ClassInstance {
        return this.callMember(interpreter, name, args, this.definition.getPrimitiveDef(returnType), trace)
    }

    fun <T: PrimitiveDefinition<*>> callMember(interpreter: Interpreter, name: String, args: List<ClassInstance>, returnType: KClass<T>, trace: LocatableTrace): ClassInstance {
        return this.callMember(interpreter, name, args, returnType.java, trace)
    }

    fun <T: PrimitiveDefinition<V>, V: Any> callMemberPrimitive(interpreter: Interpreter, name: String, args: List<ClassInstance>, returnType: Class<T>, trace: LocatableTrace = Trace.INTERNAL): V {
        return this.callMember(interpreter, name, args, returnType, trace).getPrimitive(returnType)!!
    }

    fun <T: PrimitiveDefinition<V>, V: Any> callMemberPrimitive(interpreter: Interpreter, name: String, args: List<ClassInstance>, returnType: KClass<T>, trace: LocatableTrace = Trace.INTERNAL): V {
        return this.callMemberPrimitive(interpreter, name, args, returnType.java, trace)
    }

    fun asJava(): Any? {
        return this.definition.asJavaValue(this)
    }

    fun copy(interpreter: Interpreter, trace: LocatableTrace = Trace.INTERNAL): ClassInstance {
        return this.definition.copy(this, interpreter, trace)
    }

    fun compare(interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace = Trace.INTERNAL): Int {
        return this.definition.compare(this, interpreter, other, trace)
    }

    @JvmOverloads
    fun hashCode(interpreter: Interpreter, trace: LocatableTrace = Trace.INTERNAL): Int {
        return this.definition.hashCode(this, interpreter, trace)
    }

    @JvmOverloads
    fun toString(interpreter: Interpreter, trace: LocatableTrace = Trace.INTERNAL): String {
        return this.definition.toString(this, interpreter, trace)
    }

    @JvmOverloads
    fun equals(interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace = Trace.INTERNAL): Boolean {
        return this.definition.equals(this, interpreter, other, trace)
    }

    @Deprecated("ClassInstances should be compared with interpreter context", ReplaceWith("instance.equals(interpreter, other)"))
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    @Deprecated("ClassInstances should be hashed with interpreter context", ReplaceWith("instance.equals(interpreter, other)"))
    override fun hashCode(): Int {
        return super.hashCode()
    }

    @Deprecated("ClassInstances should be stringified with interpreter context", ReplaceWith("instance.equals(interpreter, other)"))
    override fun toString(): String {
        return super.toString()
    }
}