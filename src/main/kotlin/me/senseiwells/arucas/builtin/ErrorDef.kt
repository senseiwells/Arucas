package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.annotations.*
import me.senseiwells.arucas.classes.PrimitiveDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.RuntimeError
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.ConstructorFunction
import me.senseiwells.arucas.utils.MemberFunction
import me.senseiwells.arucas.utils.Util.Types.ERROR

@ClassDoc(
    name = ERROR,
    desc = [
        "This class is used for errors, and this is the only type that can be thrown.",
        "You are able to extend this class to create your own error types"
    ]
)
class ErrorDef(interpreter: Interpreter): PrimitiveDefinition<RuntimeError.Arucas>(ERROR, interpreter) {
    fun create(details: String, value: ClassInstance): ClassInstance {
        val instance = ClassInstance(this)
        val error = RuntimeError.Arucas(instance, value, details)
        instance.setPrimitive(this, error)
        return instance
    }

    override fun defineConstructors(): List<ConstructorFunction> {
        return listOf(
            ConstructorFunction.of(this::construct),
            ConstructorFunction.of(1, this::construct1),
            ConstructorFunction.of(2, this::construct2)
        )
    }

    @ConstructorDoc(
        desc = ["This creates a new Error value with no message"],
        examples = ["new Error();"]
    )
    private fun construct(arguments: Arguments) {
        val instance = arguments.next()
        val error = RuntimeError.Arucas(instance, arguments.interpreter.getNull(), "")
        instance.setPrimitive(this, error)
    }

    @ConstructorDoc(
        desc = ["This creates a new Error value with the given details as a message"],
        params = [ParameterDoc(StringDef::class, "details", ["The details of the error."])],
        examples = ["new Error('This is an error');"]
    )
    private fun construct1(arguments: Arguments) {
        val instance = arguments.next()
        val details = arguments.nextPrimitive(StringDef::class)
        val error = RuntimeError.Arucas(instance, arguments.interpreter.getNull(), details)
        instance.setPrimitive(this, error)
    }

    @ConstructorDoc(
        desc = ["This creates a new Error value with the given details as a message and the given value"],
        params = [ParameterDoc(StringDef::class, "details", ["The details of the error."]), ParameterDoc(ObjectDef::class, "value", ["The value that is related to the error."])],
        examples = ["new Error('This is an error', [1, 2, 3]);"]
    )
    private fun construct2(arguments: Arguments) {
        val instance = arguments.next()
        val details = arguments.nextPrimitive(StringDef::class)
        val value = arguments.next()
        val error = RuntimeError.Arucas(instance, value, details)
        instance.setPrimitive(this, error)
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("getDetails", this::getDetails),
            MemberFunction.of("getValue", this::getValue),
            MemberFunction.of("getStackTraceString", this::getStackTraceString)
        )
    }

    @FunctionDoc(
        name = "getDetails",
        desc = ["This returns the raw message of the error"],
        returns = ReturnDoc(StringDef::class, ["The details of the error."]),
        examples = ["error.getDetails();"]
    )
    private fun getDetails(arguments: Arguments): String {
        return arguments.nextPrimitive(this).message
    }

    @FunctionDoc(
        name = "getValue",
        desc = ["This returns the value that is related to the error"],
        returns = ReturnDoc(ObjectDef::class, ["The value that is related to the error."]),
        examples = ["error.getValue();"]
    )
    private fun getValue(arguments: Arguments): ClassInstance {
        return arguments.nextPrimitive(this).value
    }

    @FunctionDoc(
        name = "getStackTraceString",
        desc = ["This prints the stack trace of this error"],
        returns = ReturnDoc(StringDef::class, ["The stack trace converted to a string."]),
        examples = ["error.getStackTraceString();"]
    )
    private fun getStackTraceString(arguments: Arguments): String {
        return arguments.nextPrimitive(this).format(arguments.interpreter)
    }
}