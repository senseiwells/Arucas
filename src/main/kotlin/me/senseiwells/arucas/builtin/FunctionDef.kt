package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.annotations.ClassDoc
import me.senseiwells.arucas.api.docs.annotations.ConstructorDoc
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.compiler.InternalTrace
import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.functions.ArucasFunction
import me.senseiwells.arucas.functions.builtin.Arguments
import me.senseiwells.arucas.functions.builtin.BuiltInFunction
import me.senseiwells.arucas.functions.builtin.ConstructorFunction
import me.senseiwells.arucas.interpreter.Interpreter
import me.senseiwells.arucas.utils.misc.Types.FUNCTION

@ClassDoc(
    name = FUNCTION,
    desc = [
        "This class is used for functions, and this is the only type that can be called.",
        "You are able to extend this class and implement an 'invoke' method to create",
        "your own function types, this class cannot be instantiated directly"
    ]
)
class FunctionDef(interpreter: Interpreter): CreatableDefinition<ArucasFunction>(FUNCTION, interpreter) {
    override fun call(instance: ClassInstance, interpreter: Interpreter, args: List<ClassInstance>): ClassInstance {
        return instance.asPrimitive(this)(interpreter, args)
    }

    override fun toString(instance: ClassInstance, interpreter: Interpreter, trace: LocatableTrace): String {
        val primitive = instance.asPrimitive(this)
        return "${primitive.name}::${primitive.count}"
    }

    override fun canConstructDirectly() = false

    override fun defineConstructors(): List<ConstructorFunction> {
        return listOf(
            ConstructorFunction.of(this::construct)
        )
    }

    @ConstructorDoc(
        desc = ["This creates a function, this cannot be called directly, only from child classes"],
        examples = [
            """
            class ChildFunction: Function {
                ChildFunction(): super();
            }
            """
        ]
    )
    private fun construct(arguments: Arguments) {
        val instance = arguments.next()
        val function = BuiltInFunction.arb(instance.definition.name, {
            instance.callMember(it.interpreter, "invoke", it.arguments, InternalTrace("invoke function"))
        })
        instance.setPrimitive(this, function)
    }
}