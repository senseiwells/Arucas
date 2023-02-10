package me.senseiwells.arucas.utils

import me.senseiwells.arucas.api.ArucasObfuscator
import me.senseiwells.arucas.builtin.StringDef
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.RuntimeError
import me.senseiwells.arucas.exceptions.runtimeError
import net.bytebuddy.implementation.bind.annotation.AllArguments
import net.bytebuddy.implementation.bind.annotation.Origin
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.VarHandle
import java.lang.invoke.WrongMethodTypeException
import java.lang.reflect.*

object ReflectionUtils {
    private val methodCache = HashMap<MethodId, MethodWithHandle>()
    private val constructorCache = HashMap<ConstructorId, MethodWithHandle>()
    private val fieldCache = HashMap<FieldId, FieldWithHandle>()

    fun callMethod(callingClass: Class<*>, callingObject: Any?, name: String, arguments: List<ClassInstance>, obfuscator: ArucasObfuscator): Any? {
        val (args, types) = this.argumentsToJava(arguments)
        val id = MethodId(callingClass, name, types)
        var methodWithHandle = this.methodCache[id]
        if (methodWithHandle == null) {
            val method = this.getMethod(callingClass, callingObject, obfuscator.obfuscateMethodName(callingClass, name), types)
            if (method == null) {
                val typeNames = types.joinToString(prefix = "(", postfix = ")") { obfuscator.deobfuscateClass(it) }
                runtimeError("No such method '$name' with type parameters $typeNames exists for Java type '${obfuscator.deobfuscateClass(callingClass)}'")
            }
            methodWithHandle = this.methodWithHandle(method)
            this.methodCache[id] = methodWithHandle
        }
        if (callingObject != null) {
            args.add(0, callingObject)
        }
        return this.invokeHandle(methodWithHandle, args)
    }

    fun callConstructor(clazz: Class<*>, arguments: List<ClassInstance>, obfuscator: ArucasObfuscator): Any? {
        val (args, types) = this.argumentsToJava(arguments)
        val id = ConstructorId(clazz, types)
        var methodHandle = this.constructorCache[id]
        if (methodHandle == null) {
            val constructor = this.getConstructor(clazz, types)
            if (constructor == null) {
                val typeNames = types.joinToString(prefix = "(", postfix = ")") { obfuscator.deobfuscateClass(it) }
                runtimeError("No such constructor with parameters $typeNames exists for ${obfuscator.deobfuscateClass(clazz)}")
            }
            methodHandle = this.methodWithHandle(constructor)
            this.constructorCache[id] = methodHandle
        }
        return this.invokeHandle(methodHandle, args)
    }

    fun getField(callingClass: Class<*>, callingObject: Any?, name: String, obfuscator: ArucasObfuscator): Any? {
        val handle = this.getVarHandle(callingClass, name, obfuscator)
        return this.getVarHandle(handle, callingObject)
    }

    fun setField(callingClass: Class<*>, callingObject: Any?, setObject: ClassInstance, name: String, obfuscator: ArucasObfuscator) {
        val handle = this.getVarHandle(callingClass, name, obfuscator)
        this.setVarHandle(handle, callingObject, setObject.asJava())
    }

    fun getClass(name: String, obfuscator: ArucasObfuscator): Class<*> {
        val obfuscatedName = obfuscator.obfuscateClassName(name)
        try {
            return Class.forName(obfuscatedName)
        } catch (e: Exception) {
            runtimeError("Failed to get Java class", e)
        }
    }

    fun functionToInterceptor(interpreter: Interpreter, function: ClassInstance): Any {
        return Interceptor(interpreter, function)
    }

    private fun getVarHandle(clazz: Class<*>, name: String, obfuscator: ArucasObfuscator): FieldWithHandle {
        val id = FieldId(clazz, name)
        var varHandle = this.fieldCache[id]
        if (varHandle == null) {
            val field = this.getField(clazz, obfuscator.obfuscateFieldName(clazz, name))
                ?: runtimeError("No such field with name '${name}' exists for ${obfuscator.deobfuscateClass(clazz)}")
            varHandle = this.getVarHandle(field)
            this.fieldCache[id] = varHandle
        }
        return varHandle
    }

    private fun argumentsToJava(arguments: List<ClassInstance>): Pair<ArrayList<Any?>, Array<Class<*>>> {
        val args = ArrayList<Any?>(arguments.size)
        val types = Array<Class<*>>(arguments.size) { i ->
            val argument = arguments[i].asJava()
            args.add(argument)
            if (argument != null) {
                this.wrapClass(argument::class.java)
            } else {
                Void.TYPE
            }
        }
        return args to types
    }

    private fun getMethod(callingClass: Class<*>, callingObject: Any?, name: String, types: Array<Class<*>>): Method? {
        val isStatic = callingObject == null
        for (method in callingClass.methods) {
            val matchStatic = Modifier.isStatic(method.modifiers) == isStatic
            if (matchStatic && method.name == name && this.doParametersMatch(method, types)) {
                val accessible = this.getAccessibleMethod(callingClass, callingObject, method)
                return accessible ?: method
            }
        }
        return null
    }

    private fun getConstructor(clazz: Class<*>, types: Array<Class<*>>): Constructor<*>? {
        for (constructor in clazz.constructors) {
            if (this.doParametersMatch(constructor, types)) {
                return constructor
            }
        }
        return null
    }

    private fun getField(clazz: Class<*>, name: String): Field? {
        for (field in clazz.fields) {
            if (field.name == name) {
                return field
            }
        }
        return null
    }

    private fun doParametersMatch(executable: Executable, given: Array<Class<*>>): Boolean {
        val required = executable.parameterTypes

        if (executable.isVarArgs) {
            // Technically VarArgs can be empty
            if (required.size - 1 > given.size) {
                return false
            }
        } else if (required.size != given.size) {
            return false
        }

        for (i in required.indices) {
            if (executable.isVarArgs && i == required.lastIndex) {
                val component = required[i].componentType
                for (gi in i..given.lastIndex) {
                    if (!areTypesEqual(given[gi], component)) {
                        return false
                    }
                }
                return true
            }
            if (i > given.lastIndex) {
                return false
            }

            if (!areTypesEqual(given[i], required[i])) {
                return false
            }
        }

        return true
    }

    private fun areTypesEqual(given: Class<*>, required: Class<*>): Boolean {
        if (required.isPrimitive && given == Void.TYPE) {
            return false
        }
        val requiredWrapped = this.wrapClass(required)
        // Something that we allow is casting from any number type to any number
        if (Number::class.java.isAssignableFrom(requiredWrapped) && Number::class.java.isAssignableFrom(given)) {
            return true
        }
        if (given != Void.TYPE && !requiredWrapped.isAssignableFrom(given)) {
            return false
        }
        return true
    }

    private fun getAccessibleMethod(callingClass: Class<*>, callingObject: Any?, method: Method): Method? {
        // The direct method may be inaccessible (for example it's a member of an inaccessible inner class)
        // but accessible via an interface or a superclass. Notably this happens for synthetic lambda classes.
        // This is a workaround which looks for an accessible method in a superclass or interface.

        if (method.canAccess(callingObject)) {
            return method
        }

        var mutMethod = method
        try {
            mutMethod = callingClass.getMethod(mutMethod.name, *mutMethod.parameterTypes)
            if (mutMethod.canAccess(callingObject)) {
                return mutMethod
            }
        } catch (_: NoSuchMethodException) {
            return null
        }

        if (!callingClass.isInterface) {
            val superClass = callingClass.superclass
            if (superClass != null) {
                val accessible = this.getAccessibleMethod(superClass, callingObject, mutMethod)
                if (accessible != null) {
                    return accessible
                }
            }
        }

        for (itf in callingClass.interfaces) {
            val accessible = this.getAccessibleMethod(itf, callingObject, mutMethod)
            if (accessible != null) {
                return accessible
            }
        }

        return null
    }

    private fun methodWithHandle(method: Method): MethodWithHandle {
        return MethodWithHandle(method, MethodHandles.publicLookup().unreflect(method))
    }

    private fun methodWithHandle(constructor: Constructor<*>): MethodWithHandle {
        return MethodWithHandle(constructor, MethodHandles.publicLookup().unreflectConstructor(constructor))
    }

    private fun getVarHandle(field: Field): FieldWithHandle {
        return FieldWithHandle(field, MethodHandles.publicLookup().unreflectVarHandle(field))
    }

    private fun invokeHandle(methodHandle: MethodWithHandle, args: List<Any?>): Any? {
        try {
            return methodHandle.invoke(args)
        } catch (e: RuntimeError) {
            throw e
        } catch (e: ClassCastException) {
            runtimeError("Incorrect parameter type was found", e)
        } catch (e: WrongMethodTypeException) {
            runtimeError("Method was invoked incorrectly", e)
        } catch (e: Exception) {
            runtimeError("An unexpected error was thrown during java method call", e)
        }
    }

    private fun setVarHandle(varHandle: FieldWithHandle, calling: Any?, value: Any?) {
        try {
            varHandle.set(calling, value)
        } catch (e: RuntimeError) {
            throw e
        } catch (e: ClassCastException) {
            runtimeError("Incorrect object for field assignment", e)
        } catch (e: WrongMethodTypeException) {
            val error = if (calling == null) "statically but expected virtual" else "virtually but expected static"
            runtimeError("Field was set $error assignment", e)
        } catch (e: UnsupportedOperationException) {
            runtimeError("Field cannot be reassigned", e)
        } catch (e: Exception) {
            runtimeError("An unexpected error was thrown during java field assignment", e)
        }
    }

    private fun getVarHandle(varHandle: FieldWithHandle, calling: Any?): Any? {
        try {
            return varHandle.get(calling)
        } catch (e: RuntimeError) {
            throw e
        } catch (e: ClassCastException) {
            runtimeError("Incorrect object for field access", e)
        } catch (e: WrongMethodTypeException) {
            val error = if (calling == null) "statically but expected virtual" else "virtually but expected static"
            runtimeError("Field was accessed $error access", e)
        } catch (e: Exception) {
            runtimeError("An unexpected error was thrown during java field access", e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> wrapClass(type: Class<T>): Class<T> {
        return when (type) {
            Boolean::class.java -> java.lang.Boolean::class.java
            Byte::class.java -> java.lang.Byte::class.java
            Char::class.java -> java.lang.Character::class.java
            Double::class.java -> java.lang.Double::class.java
            Float::class.java -> java.lang.Float::class.java
            Int::class.java -> java.lang.Integer::class.java
            Long::class.java -> java.lang.Long::class.java
            Short::class.java -> java.lang.Short::class.java
            Void.TYPE -> Void::class.java
            else -> type
        } as Class<T>
    }

    private fun castToNumber(any: Any?, clazz: Class<*>): Any? {
        if (any !is Number) {
            return any
        }
        return when (clazz) {
            java.lang.Byte::class.java -> any.toByte()
            java.lang.Short::class.java -> any.toShort()
            java.lang.Integer::class.java -> any.toInt()
            java.lang.Long::class.java -> any.toLong()
            java.lang.Float::class.java -> any.toFloat()
            java.lang.Double::class.java -> any.toDouble()
            else -> any // ???
        }
    }

    private data class MethodId(val clazz: Class<*>, val name: String, val types: Array<Class<*>>) {
        val hash: Int

        init {
            var hash = this.clazz.hashCode()
            hash = 31 * hash + this.name.hashCode()
            hash = 31 * hash + this.types.contentHashCode()
            this.hash = hash
        }

        override fun equals(other: Any?): Boolean {
            if (other !is MethodId || this.clazz != other.clazz || this.name != other.name) {
                return false
            }
            return this.types.contentEquals(other.types)
        }

        override fun hashCode() = this.hash
    }

    private data class ConstructorId(val clazz: Class<*>, val types: Array<Class<*>>) {
        val hash = 31 * this.clazz.hashCode() + this.types.contentHashCode()

        override fun equals(other: Any?): Boolean {
            if (other !is ConstructorId || this.clazz != other.clazz) {
                return false
            }
            return this.types.contentEquals(other.types)
        }

        override fun hashCode() = this.hash
    }

    private data class FieldId(val clazz: Class<*>, val name: String) {
        val hash = 31 * this.clazz.hashCode() + this.name.hashCode()

        override fun equals(other: Any?): Boolean {
            return other is FieldId && this.clazz == other.clazz && this.name == other.name
        }

        override fun hashCode(): Int {
            return this.hash
        }
    }

    private data class MethodWithHandle(val method: Executable, val handle: MethodHandle) {
        private val casts by lazy {
            val casts = HashMap<Int, (Any?) -> Any?>()
            this.method.parameterTypes.forEachIndexed { index, clazz ->
                if (this.method.isVarArgs && index == this.method.parameterCount - 1) {
                    val wrapped = wrapClass(clazz.componentType)
                    if (Number::class.java.isAssignableFrom(wrapped)) {
                        casts[index] = { castToNumber(it, wrapped) }
                    }
                    return@forEachIndexed
                }
                val wrapped = wrapClass(clazz)
                if (Number::class.java.isAssignableFrom(wrapped)) {
                    casts[index] = { castToNumber(it, wrapped) }
                }
            }
            casts
        }

        fun invoke(arguments: List<Any?>): Any? {
            val mapped = arguments.mapIndexed { index, any ->
                this.casts[index.coerceAtMost(this.method.parameterCount - 1)]?.invoke(any) ?: any
            }
            return handle.invokeWithArguments(mapped)
        }
    }

    private data class FieldWithHandle(val field: Field, val handle: VarHandle) {
        private val fieldType = wrapClass(field.type)
        private val shouldTryToCast = Number::class.java.isAssignableFrom(fieldType)

        fun set(calling: Any?, value: Any?) {
            val castedValue = if (this.shouldTryToCast) castToNumber(value, this.fieldType) else value
            if (calling == null) this.handle.set(castedValue) else this.handle.set(calling, castedValue)
        }

        fun get(calling: Any?): Any? {
            return if (calling == null) this.handle.get() else this.handle.get(calling)
        }
    }

    class Interceptor(interpreter: Interpreter, val function: ClassInstance) {
        val interpreter = interpreter.branch()

        @RuntimeType
        fun intercept(@AllArguments allArguments: Array<Any?>, @Origin method: Method) {
            val branch = this.interpreter.branch()
            val arguments = ArrayList<ClassInstance>()
            arguments.add(branch.create(StringDef::class, method.name))
            arguments.add(branch.convertValue(allArguments))
            branch.call(this.function, arguments)
        }
    }
}