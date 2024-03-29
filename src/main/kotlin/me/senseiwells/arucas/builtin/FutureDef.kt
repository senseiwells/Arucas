package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.annotations.ClassDoc
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.functions.builtin.Arguments
import me.senseiwells.arucas.functions.builtin.BuiltInFunction
import me.senseiwells.arucas.functions.builtin.MemberFunction
import me.senseiwells.arucas.interpreter.Interpreter
import me.senseiwells.arucas.utils.misc.Types.FUTURE
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

@ClassDoc(
    name = FUTURE,
    desc = [
        "This class is used to represent values that are in the future.",
        "More precisely values that are being evaluated on another thread,",
        "this allows you to access those values once they've been processed."
    ]
)
class FutureDef(interpreter: Interpreter): CreatableDefinition<Future<*>>(FUTURE, interpreter) {
    override fun canExtend() = false

    override fun defineStaticMethods(): List<BuiltInFunction> {
        return listOf(
            BuiltInFunction.of("completed", 1, this::completed)
        )
    }

    @FunctionDoc(
        isStatic = true,
        name = "completed",
        desc = ["This returns a future that with a complete value."],
        params = [ParameterDoc(ObjectDef::class, "value", ["The value to complete the future with."])],
        returns = ReturnDoc(FutureDef::class, ["The future that has been completed with the value."]),
        examples = ["future = Future.completed(true);"]
    )
    private fun completed(arguments: Arguments): ClassInstance {
        return this.create(CompletableFuture.completedFuture(arguments.next()))
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("await", this::await),
            MemberFunction.of("cancel", this::cancel),
            MemberFunction.of("isComplete", this::isComplete),
            MemberFunction.of("isCancelled", this::isCancelled)
        )
    }

    @FunctionDoc(
        name = "await",
        desc = [
            "This blocks the current thread until the future has",
            "been completed and then returns the value of the future"
        ],
        returns = ReturnDoc(ObjectDef::class, ["The value of the future."]),
        examples = ["future.await();"]
    )
    private fun await(arguments: Arguments): Any? {
        arguments.interpreter.canInterrupt {
            return arguments.nextPrimitive(this).get()
        }
    }

    @FunctionDoc(
        name = "cancel",
        desc = [
            "This cancels the future and prevents it from completing"
        ],
        examples = ["future.cancel();"]
    )
    private fun cancel(arguments: Arguments) {
        arguments.nextPrimitive(this).cancel(true)
    }

    @FunctionDoc(
        name = "isComplete",
        desc = ["This returns whether the future has been completed"],
        returns = ReturnDoc(BooleanDef::class, ["Whether the future has been completed."]),
        examples = ["future.isComplete();"]
    )
    private fun isComplete(arguments: Arguments): Boolean {
        return arguments.nextPrimitive(this).isDone
    }

    @FunctionDoc(
        name = "isCancelled",
        desc = ["This returns whether the future has been cancelled"],
        returns = ReturnDoc(BooleanDef::class, ["Whether the future has been cancelled."]),
        examples = ["future.isCancelled();"]
    )
    private fun isCancelled(arguments: Arguments): Boolean {
        return arguments.nextPrimitive(this).isCancelled
    }
}