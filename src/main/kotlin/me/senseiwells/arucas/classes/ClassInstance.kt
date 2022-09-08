package me.senseiwells.arucas.classes

import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.core.Type
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.CallTrace
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.Trace
import kotlin.reflect.KClass

class ClassInstance internal constructor(val definition: ClassDefinition) {
    private val instanceFields by lazy { LinkedHashMap<String, HintedField>() }
    private var primitive: Any? = null

    @Deprecated("This method should not be called directly", ReplaceWith("interpreter.visitCall(instance, args)"))
    internal fun call(interpreter: Interpreter, args: List<ClassInstance>) = this.definition.call(this, interpreter, args)

    @JvmOverloads
    fun callMember(interpreter: Interpreter, name: String, args: List<ClassInstance>, trace: LocatableTrace, functionName: String = "<${this.definition.name}>.$name::${args.size}"): ClassInstance {
        val callTrace = CallTrace(trace, functionName)
        val arguments = ArrayList(args)
        val function = this.definition.memberFunctionAccess(this, name, arguments, trace)
        return interpreter.call(function, arguments, callTrace)
    }

    fun unary(interpreter: Interpreter, type: Type, trace: LocatableTrace) = this.definition.unary(this, interpreter, type, trace)

    fun binary(interpreter: Interpreter, type: Type, other: () -> ClassInstance, trace: LocatableTrace) = this.definition.binary(this, interpreter, type, other, trace)

    fun memberAccess(interpreter: Interpreter, name: String, trace: LocatableTrace) = this.definition.memberAccess(this, interpreter, name, trace)

    fun memberAssign(name: String, assignee: ClassInstance, trace: Trace) = this.definition.memberAssign(this, name, assignee, trace)

    fun bracketAccess(index: ClassInstance, interpreter: Interpreter, trace: LocatableTrace = Trace.INTERNAL) = this.definition.bracketAccess(this, interpreter, index, trace)

    fun bracketAssign(index: ClassInstance, interpreter: Interpreter, assignee: ClassInstance, trace: LocatableTrace = Trace.INTERNAL) = this.definition.bracketAssign(this, interpreter, index, assignee, trace)

    fun addInstanceField(name: String, field: HintedField) = this.instanceFields.putIfAbsent(name, field)

    fun getInstanceField(name: String) = this.instanceFields[name]

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

    fun <T: Any> asPrimitive(definition: PrimitiveDefinition<T>): T {
        return this.getPrimitive(definition)!!
    }

    fun isOf(definition: ClassDefinition): Boolean = this.definition.inheritsFrom(definition)

    fun isOf(klass: KClass<out PrimitiveDefinition<*>>): Boolean = this.isOf(this.definition.getPrimitiveDef(klass))

    fun isOf(clazz: Class<out PrimitiveDefinition<*>>): Boolean = this.isOf(this.definition.getPrimitiveDef(clazz))

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

    fun asJava() = this.definition.asJavaValue(this)

    fun copy(interpreter: Interpreter, trace: LocatableTrace = Trace.INTERNAL) = this.definition.copy(this, interpreter, trace)

    fun compare(interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace = Trace.INTERNAL) = this.definition.compare(this, interpreter, other, trace)

    @JvmOverloads
    fun hashCode(interpreter: Interpreter, trace: LocatableTrace = Trace.INTERNAL) = this.definition.hashCode(this, interpreter, trace)

    @JvmOverloads
    fun toString(interpreter: Interpreter, trace: LocatableTrace = Trace.INTERNAL) = this.definition.toString(this, interpreter, trace)

    @JvmOverloads
    fun equals(interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace = Trace.INTERNAL) = this.definition.equals(this, interpreter, other, trace)

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