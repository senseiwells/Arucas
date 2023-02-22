package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.annotations.ClassDoc
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.functions.builtin.MemberFunction
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.Util.Types.ENUM
import me.senseiwells.arucas.utils.impl.ArucasEnum

@ClassDoc(
    name = ENUM,
    desc = [
        "This class is the super class of all enums in Arucas.",
        "Enums cannot be instantiated or extended"
    ]
)
class EnumDef(interpreter: Interpreter): PrimitiveDefinition<ArucasEnum>(ENUM, interpreter) {
    override fun canExtend() = false

    override fun toString(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): String {
        return "<enum ${instance.definition.name} - ${instance.asPrimitive(this).name}>"
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("getName", this::getName),
            MemberFunction.of("ordinal", this::ordinal)
        )
    }

    @FunctionDoc(
        name = "getName",
        desc = ["This allows you to get the name of an enum value"],
        returns = ReturnDoc(StringDef::class, ["The name of the enum value."]),
        examples = ["enum.getName();"]
    )
    private fun getName(arguments: Arguments): String {
        return arguments.nextPrimitive(EnumDef::class).name
    }

    @FunctionDoc(
        name = "ordinal",
        desc = ["This allows you to get the ordinal of the enum value"],
        returns = ReturnDoc(NumberDef::class, ["The ordinal of the enum value."]),
        examples = ["enum.ordinal();"]
    )
    private fun ordinal(arguments: Arguments): Int {
        return arguments.nextPrimitive(EnumDef::class).ordinal
    }
}