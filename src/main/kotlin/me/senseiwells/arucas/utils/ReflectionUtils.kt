package me.senseiwells.arucas.utils

import me.senseiwells.arucas.api.ArucasObfuscator
import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.exceptions.RuntimeError
import me.senseiwells.arucas.exceptions.runtimeError
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.VarHandle
import java.lang.invoke.WrongMethodTypeException
import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object ReflectionUtils {
    private val methodCache = HashMap<MethodId, MethodHandle>()
    private val constructorCache = HashMap<ConstructorId, MethodHandle>()
    private val fieldCache = HashMap<FieldId, VarHandle>()

    fun callMethod(callingClass: Class<*>, callingObject: Any?, name: String, arguments: List<ClassInstance>, obfuscator: ArucasObfuscator): Any? {
        val (args, types) = this.argumentsToJava(arguments)
        val id = MethodId(callingClass, name, types)
        var methodHandle = this.methodCache[id]
        if (methodHandle == null) {
            val method = this.getMethod(callingClass, callingObject, obfuscator.obfuscateMethodName(callingClass, name), types)
            if (method == null) {
                val typeNames = types.joinToString(prefix = "(", postfix = ")") { obfuscator.deobfuscateClass(it) }
                runtimeError("No such method '$name' with type parameters $typeNames exists for '${obfuscator.deobfuscateClass(callingClass)}'")
            }
            methodHandle = this.getMethodHandle(method)
            this.methodCache[id] = methodHandle
        }
        if (callingObject != null) {
            args.add(0, callingObject)
        }
        return this.invokeHandle(methodHandle, args)
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
            methodHandle = this.getMethodHandle(constructor)
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
        this.setVarHandle(handle, callingObject, setObject)
    }

    fun getClass(name: String, obfuscator: ArucasObfuscator): Class<*> {
        val obfuscatedName = obfuscator.obfuscateClassName(name)
        try {
            return Class.forName(obfuscatedName)
        } catch (e: Exception) {
            runtimeError("Failed to get Java class", e)
        }
    }

    private fun getVarHandle(clazz: Class<*>, name: String, obfuscator: ArucasObfuscator): VarHandle {
        val id = FieldId(clazz, name)
        var varHandle: VarHandle? = this.fieldCache[id]
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
                argument::class.java
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
            if (i > given.lastIndex) {
                return true
            }
            val givenClass = given[i]
            if (givenClass != Void.TYPE && required[i].isAssignableFrom(givenClass)) {
                return false
            }
        }
        val varArgType = if (required.isNotEmpty()) required.last().componentType else null
        if (varArgType != null && required.lastIndex != given.lastIndex) {
            for (i in required.size..given.lastIndex) {
                val givenClass = given[i]
                if (givenClass != Void.TYPE && varArgType.isAssignableFrom(givenClass)) {
                    return false
                }
            }
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
        mutMethod = callingClass.getMethod(mutMethod.name, *mutMethod.parameterTypes)
        if (mutMethod.canAccess(callingObject)) {
            return mutMethod
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

    private fun getMethodHandle(method: Method): MethodHandle {
        return MethodHandles.publicLookup().unreflect(method)
    }

    private fun getMethodHandle(constructor: Constructor<*>): MethodHandle {
        val lookup = MethodHandles.publicLookup()
        return lookup.unreflectConstructor(constructor)
    }

    private fun getVarHandle(field: Field): VarHandle {
        val lookup = MethodHandles.publicLookup()
        return lookup.unreflectVarHandle(field)
    }

    private fun invokeHandle(methodHandle: MethodHandle, args: List<Any?>): Any? {
        try {
            return methodHandle.invokeWithArguments(args)
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

    private fun setVarHandle(varHandle: VarHandle, calling: Any?, value: Any?) {
        try {
            if (calling == null) varHandle.set(value) else varHandle.set(calling, value)
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

    private fun getVarHandle(varHandle: VarHandle, calling: Any?): Any? {
        try {
            return if (calling == null) varHandle.get() else varHandle.get(calling)
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
}