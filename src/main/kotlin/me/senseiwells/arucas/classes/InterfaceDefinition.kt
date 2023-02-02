package me.senseiwells.arucas.classes

import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.CallTrace
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.Trace

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

    override fun inheritsFrom(definition: ClassDefinition): Boolean {
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