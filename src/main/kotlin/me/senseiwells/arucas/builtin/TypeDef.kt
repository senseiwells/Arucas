package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.ClassDoc
import me.senseiwells.arucas.api.docs.FunctionDoc
import me.senseiwells.arucas.classes.ClassDefinition
import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.BuiltInFunction
import me.senseiwells.arucas.utils.MemberFunction
import me.senseiwells.arucas.utils.Util.Types.BOOLEAN
import me.senseiwells.arucas.utils.Util.Types.OBJECT
import me.senseiwells.arucas.utils.Util.Types.STRING
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
        params = [OBJECT, "value", "the value you want to get the type of"],
        returns = [TYPE, "the type of the value"],
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
        params = [TYPE, "type", "the other type you want to check against"],
        returns = [BOOLEAN, "whether the type is of that type"],
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
        returns = [STRING, "the name of the type"],
        examples = ["String.type.getName();"]
    )
    private fun getName(arguments: Arguments): String {
        val instance = arguments.nextPrimitive(this)
        return instance.name
    }
}