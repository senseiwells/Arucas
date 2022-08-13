package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.ClassDoc
import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.core.Type
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.Util
import me.senseiwells.arucas.utils.Util.Types.BOOLEAN

@ClassDoc(
    name = BOOLEAN,
    desc = [
        "This is the boolean type, representing either true or false.",
        "This class cannot be instantiated, or extended"
    ]
)
class BooleanDef(interpreter: Interpreter): PrimitiveDefinition<Boolean>(BOOLEAN, interpreter) {
    val TRUE = this.create(true)
    val FALSE = this.create(false)

    fun from(boolean: Boolean) = if (boolean) TRUE else FALSE

    override fun canExtend() = false

    override fun binary(instance: ClassInstance, interpreter: Interpreter, type: Type, other: () -> ClassInstance, trace: LocatableTrace): ClassInstance {
        return when (type) {
            Type.AND -> if (!instance.asPrimitive(this)) FALSE else this.from(this.and(instance, interpreter, other(), trace) as Boolean)
            Type.OR -> if (instance.asPrimitive(this)) TRUE else this.from(this.and(instance, interpreter, other(), trace) as Boolean)
            else -> super.binary(instance, interpreter, type, other, trace)
        }
    }

    override fun not(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): Boolean {
        return !instance.asPrimitive(this)
    }

    override fun and(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val otherBoolean = other.getPrimitive(this) ?: return super.and(instance, interpreter, other, trace)
        return instance.asPrimitive(this) && otherBoolean
    }

    override fun or(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val otherBoolean = other.getPrimitive(this) ?: return super.or(instance, interpreter, other, trace)
        return instance.asPrimitive(this) || otherBoolean
    }

    override fun bitAnd(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val otherBoolean = other.getPrimitive(this) ?: return super.bitAnd(instance, interpreter, other, trace)
        return instance.asPrimitive(this) and otherBoolean
    }

    override fun bitOr(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val otherBoolean = other.getPrimitive(this) ?: return super.bitOr(instance, interpreter, other, trace)
        return instance.asPrimitive(this) or otherBoolean
    }

    override fun xor(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Any? {
        val otherBoolean = other.getPrimitive(this) ?: return super.xor(instance, interpreter, other, trace)
        return instance.asPrimitive(this) xor otherBoolean
    }

    override fun toString(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): String {
        return if (instance === TRUE) "true" else "false"
    }
}