package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.annotations.ClassDoc
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc
import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.core.Type
import me.senseiwells.arucas.core.Type.*
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.functions.builtin.MemberFunction
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.Trace
import me.senseiwells.arucas.utils.Util.Types.OBJECT

@ClassDoc(
    name = OBJECT,
    desc = [
        "This is the base class for every other class in Arucas.",
        "This class cannot be instantiated from, you can extend it",
        "however every class already extends this class by default"
    ]
)
class ObjectDef(interpreter: Interpreter): PrimitiveDefinition<Any>(OBJECT, interpreter) {
    override fun superclassOf(name: String): ClassDefinition {
        return this
    }

    override fun cacheSuperclasses() {
        // We have no super classes
    }

    override fun call(instance: ClassInstance, interpreter: Interpreter, args: List<ClassInstance>): ClassInstance {
        runtimeError("Cannot call '${instance.definition.name}'")
    }

    override fun memberFunctionAccess(instance: ClassInstance, name: String, args: MutableList<ClassInstance>, trace: Trace, origin: ClassDefinition): ClassInstance {
        this.methods.value.get(name, args.size + 1)?.let {
            args.add(0, instance)
            return it
        }

        instance.getInstanceField(name)?.let {
            val field = it.instance
            if (field.definition.inheritsFrom(FunctionDef::class)) {
                return field
            }
        }

        val error = if (args.isEmpty()) "" else " with ${args.size} parameter${if (args.size == 1) "" else "s"}"
        runtimeError("Method '<${origin.name}>.$name'$error is not defined", trace)
    }

    override fun hasMemberFunction(name: String): Boolean {
        return this.methods.value.has(name)
    }

    override fun hasMemberFunction(name: String, parameters: Int): Boolean {
        return this.methods.value.has(name, parameters)
    }

    override fun bracketAccess(instance: ClassInstance, interpreter: Interpreter, index: ClassInstance, trace: LocatableTrace): ClassInstance {
        runtimeError("Cannot access '${instance.definition.name}' with index", trace)
    }

    override fun bracketAssign(instance: ClassInstance, interpreter: Interpreter, index: ClassInstance, assignee: ClassInstance, trace: LocatableTrace): ClassInstance {
        runtimeError("Cannot assign to '${instance.definition.name}' with index", trace)
    }

    override fun copy(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace) = instance

    override fun not(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace) = this.cannotApply(instance, NOT, trace)

    override fun plus(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace) = this.cannotApply(instance, PLUS, trace)

    override fun minus(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace) = this.cannotApply(instance, MINUS, trace)

    override fun plus(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace) = this.cannotApply(instance, PLUS, other, trace)

    override fun minus(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace) = this.cannotApply(instance, MINUS, other, trace)

    override fun multiply(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace) = this.cannotApply(instance, MULTIPLY, other, trace)

    override fun divide(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace) = this.cannotApply(instance, DIVIDE, other, trace)

    override fun power(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace) = this.cannotApply(instance, POWER, other, trace)

    override fun and(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace) = this.cannotApply(instance, AND, other, trace)

    override fun or(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace) = this.cannotApply(instance, OR, other, trace)

    override fun bitAnd(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace) = this.cannotApply(instance, BIT_AND, other, trace)

    override fun bitOr(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace) = this.cannotApply(instance, BIT_OR, other, trace)

    override fun xor(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace) = this.cannotApply(instance, XOR, other, trace)

    override fun shiftLeft(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace) = this.cannotApply(instance, SHIFT_LEFT, other, trace)

    override fun shiftRight(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace) = this.cannotApply(instance, SHIFT_RIGHT, other, trace)

    override fun compare(instance: ClassInstance, interpreter: Interpreter, type: Type, other: ClassInstance, trace: LocatableTrace) = this.cannotApply(instance, type, other, trace)

    override fun compare(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace): Int {
        val lessThan = instance.binary(interpreter, LESS_THAN, { other }, trace).getPrimitive(BooleanDef::class)
            ?: runtimeError("Expected comparison '<' between '${instance.definition.name}' and '${other.definition.name}' to return a boolean", trace)
        if (lessThan) {
            return -1
        }
        val moreThan = instance.binary(interpreter, MORE_THAN, { other }, trace).getPrimitive(BooleanDef::class)
            ?: runtimeError("Expected comparison '>' between '${instance.definition.name}' and '${other.definition.name}' to return a boolean", trace)
        if (moreThan) {
            return 1
        }
        return 0
    }

    override fun notEquals(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace) = !instance.equals(interpreter, other, trace)

    override fun equals(instance: ClassInstance, interpreter: Interpreter, other: ClassInstance, trace: LocatableTrace) = instance === other

    override fun hashCode(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace) = System.identityHashCode(instance)

    override fun toString(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace) = "${instance.definition.name}@${Integer.toHexString(instance.hashCode(interpreter))}"

    private fun cannotApply(instance: ClassInstance, type: Type, trace: Trace): Nothing {
        runtimeError("Cannot apply operator '$type' to ${instance.definition.name}", trace)
    }

    private fun cannotApply(instance: ClassInstance, type: Type, other: ClassInstance, trace: Trace): Nothing {
        runtimeError("Cannot apply operator '$type' to ${instance.definition.name} and ${other.definition.name}", trace)
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("toString", this::toString),
            MemberFunction.of("hashCode", this::hashCode),
            MemberFunction.of("uniqueHash", this::uniqueHash),
            MemberFunction.of("copy", this::copy),
            MemberFunction.of("instanceOf", 1, this::instanceOf)
        )
    }

    @FunctionDoc(
        name = "toString",
        desc = ["This returns the string representation of the value"],
        returns = ReturnDoc(StringDef::class, ["The string representation of the value."]),
        examples = ["[10, 11, 12].toString(); // [10, 11, 12]"]
    )
    private fun toString(arguments: Arguments): String {
        return arguments.next().toString(arguments.interpreter)
    }

    @FunctionDoc(
        name = "hashCode",
        desc = [
            "This returns the hash code of the value, mainly used for maps and sets",
            "the hash code of an object must remain consistent for objects to be able",
            "to be used as keys in a map or set. If two objects are equal, they must",
            "have the same hash code"
        ],
        returns = ReturnDoc(NumberDef::class, ["The hash code of the value."]),
        examples = ["[10, 11, 12].hashCode(); // -1859087"]
    )
    private fun hashCode(arguments: Arguments): Int {
        return arguments.next().hashCode(arguments.interpreter)
    }

    @FunctionDoc(
        name = "uniqueHash",
        desc = ["This returns the unique hash of the value, this is different for every instance of a value"],
        returns = ReturnDoc(NumberDef::class, ["The unique hash of the value."]),
        examples = ["'thing'.uniqueHash();"]
    )
    private fun uniqueHash(arguments: Arguments): Int {
        return System.identityHashCode(arguments.next())
    }

    @FunctionDoc(
        name = "copy",
        desc = [
            "This returns a copy of the value if implemented.",
            "Some objects that are immutable, such as Strings and Numbers",
            "will not be copied, and will return the same instance.",
            "Any object that has not implemented the copy method will also",
            "return the same instance"
        ],
        returns = ReturnDoc(ObjectDef::class, ["A copy of the value."]),
        examples = ["[10, 11, 12].copy(); // [10, 11, 12]"]
    )
    private fun copy(arguments: Arguments): ClassInstance {
        return arguments.next().copy(arguments.interpreter)
    }

    @FunctionDoc(
        name = "instanceOf",
        desc = ["This returns true if the value is an instance of the given type"],
        params = [ParameterDoc(TypeDef::class, "type", ["The type to check against."])],
        returns = ReturnDoc(BooleanDef::class, ["True if the value is an instance of the given type."]),
        examples = ["[10, 11, 12].instanceOf(List.type); // true"]
    )
    private fun instanceOf(arguments: Arguments): Boolean {
        val instance = arguments.next()
        val type = arguments.nextPrimitive(TypeDef::class)
        return instance.isOf(type)
    }
}