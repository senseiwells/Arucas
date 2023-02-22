package me.senseiwells.arucas.extensions

import me.senseiwells.arucas.api.docs.annotations.ClassDoc
import me.senseiwells.arucas.builtin.FunctionDef
import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.functions.builtin.BuiltInFunction
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.ReflectionUtils
import me.senseiwells.arucas.utils.Trace
import me.senseiwells.arucas.utils.Util.Types.JAVA_CLASS

@ClassDoc(
    name = JAVA_CLASS,
    desc = [
        "This class 'acts' as a Java class. You are able to call this class which",
        "will invoke the Java class' constructor, and access and assign the static",
        "fields of the class. This class cannot be instantiated or extended."
    ]
)
class JavaClassDef(interpreter: Interpreter): CreatableDefinition<Class<*>>(JAVA_CLASS, interpreter) {
    override fun canExtend() = false

    override fun call(instance: ClassInstance, interpreter: Interpreter, args: List<ClassInstance>): ClassInstance {
        val value = ReflectionUtils.callConstructor(instance.asPrimitive(this), args, interpreter.api.getObfuscator())
        return this.getPrimitiveDef(JavaDef::class).createNullable(value)
    }

    override fun memberFunctionAccess(instance: ClassInstance, name: String, args: MutableList<ClassInstance>, trace: Trace, origin: ClassDefinition): ClassInstance {
        if (!this.hasMemberFunction(name, args.size)) {
            return this.interpreter.create(FunctionDef::class, BuiltInFunction.java(instance.asPrimitive(this), null, name))
        }
        return super.memberFunctionAccess(instance, name, args, trace, origin)
    }

    override fun memberAccess(instance: ClassInstance, interpreter: Interpreter, name: String, trace: LocatableTrace): ClassInstance {
        return this.getPrimitiveDef(JavaDef::class).createNullable(
            ReflectionUtils.getField(instance.asPrimitive(this), null, name, interpreter.api.getObfuscator())
        )
    }

    override fun memberAssign(instance: ClassInstance, name: String, assignee: ClassInstance, trace: Trace): ClassInstance {
        ReflectionUtils.setField(instance.asPrimitive(this), null, assignee, name, this.interpreter.api.getObfuscator())
        return assignee
    }

    override fun toString(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): String {
        val className = interpreter.api.getObfuscator().deobfuscateClass(instance.asPrimitive(this))
        return "JavaClass{name=$className}"
    }
}