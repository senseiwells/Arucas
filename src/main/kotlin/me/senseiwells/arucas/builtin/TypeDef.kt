package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.annotations.ClassDoc
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc
import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.functions.builtin.BuiltInFunction
import me.senseiwells.arucas.functions.builtin.MemberFunction
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.Util.Types.TYPE

@ClassDoc(name = TYPE, desc = ["This class lets you get the type of another class"])
class TypeDef(interpreter: Interpreter): CreatableDefinition<ClassDefinition>(TYPE, interpreter) {
    override fun canExtend(): Boolean {
        return false
    }

    override fun defineStaticMethods(): List<BuiltInFunction> {
        return listOf(
            BuiltInFunction.of("of", 1, this::of)
        )
    }

    @FunctionDoc(
        isStatic = true,
        name = "of",
        desc = ["This gets the specific type of a value"],
        params = [ParameterDoc(ObjectDef::class, "value", ["The value you want to get the type of."])],
        returns = ReturnDoc(TypeDef::class, ["The type of the value."]),
        examples = ["Type.of(0);"]
    )
    private fun of(arguments: Arguments): ClassInstance {
        return arguments.next().definition.getTypeInstance()
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("inheritsFrom", 1, this::inheritsFrom),
            MemberFunction.of("getName", this::getName),
        )
    }

    @FunctionDoc(
        name = "inheritsFrom",
        desc = ["This checks whether a type is a subtype of another type"],
        params = [ParameterDoc(TypeDef::class, "type", ["The other type you want to check against."])],
        returns = ReturnDoc(BooleanDef::class, ["Whether the type is of that type."]),
        examples = ["String.type.inheritsFrom(Number.type);"]
    )
    private fun inheritsFrom(arguments: Arguments): Boolean {
        val instance = arguments.nextPrimitive(this)
        val otherType = arguments.nextPrimitive(this)
        return instance.inheritsFrom(otherType)
    }

    @FunctionDoc(
        name = "getName",
        desc = ["This gets the name of the type"],
        returns = ReturnDoc(StringDef::class, ["The name of the type."]),
        examples = ["String.type.getName();"]
    )
    private fun getName(arguments: Arguments): String {
        val instance = arguments.nextPrimitive(this)
        return instance.name
    }
}