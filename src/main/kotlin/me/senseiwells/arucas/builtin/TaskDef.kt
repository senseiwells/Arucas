package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.ClassDoc
import me.senseiwells.arucas.api.docs.ConstructorDoc
import me.senseiwells.arucas.api.docs.FunctionDoc
import me.senseiwells.arucas.classes.ClassInstance
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.BuiltInFunction
import me.senseiwells.arucas.utils.ConstructorFunction
import me.senseiwells.arucas.utils.MemberFunction
import me.senseiwells.arucas.utils.Util.Types.FUNCTION
import me.senseiwells.arucas.utils.Util.Types.FUTURE
import me.senseiwells.arucas.utils.Util.Types.TASK
import me.senseiwells.arucas.utils.impl.ArucasTask
import me.senseiwells.arucas.utils.impl.Task
import java.util.concurrent.Future

@ClassDoc(
    name = TASK,
    desc = [
        "This class is used to create tasks that can be chained and",
        "run asynchronously. Tasks can be executed as many times as needed",
        "and chained tasks will be executed in the order they are created."
    ]
)
class TaskDef(interpreter: Interpreter): CreatableDefinition<Task>(TASK, interpreter) {
    override fun defineConstructors(): List<ConstructorFunction> {
        return listOf(
            ConstructorFunction.of(this::construct)
        )
    }

    @ConstructorDoc(
        desc = ["This creates a new empty task"],
        examples = ["task = new Task();"]
    )
    private fun construct(arguments: Arguments) {
        val instance = arguments.next()
        instance.setPrimitive(this, ArucasTask(arguments.interpreter))
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("then", 1, this::then),
            MemberFunction.of("loopIf", 1, this::loopIf),
            MemberFunction.of("run", this::run),
        )
    }

    @FunctionDoc(
        name = "then",
        desc = [
            "This adds a function to the end of the current task.",
            "If this is the last function in the task then the return",
            "value of the function will be the return value of the task."
        ],
        params = [FUNCTION, "function", "the function to run at the end of the task"],
        returns = [TASK, "the task, this allows for chaining"],
        examples = [
            """
            task = new Task()
                .then(fun() print("hello"))
                .then(fun() print(" "))
                .then(fun() print("world"))
                .then(fun() 10);
            f = task.run(); // prints 'hello world'
            print(f.await()); // prints 10
            """
        ]
    )
    private fun then(arguments: Arguments): ClassInstance {
        val instance = arguments.next()
        instance.asPrimitive(this).addTask(arguments.nextPrimitive(FunctionDef::class))
        return instance
    }

    @FunctionDoc(
        name = "loopIf",
        desc = [
            "This loops the task, essentially just calling 'task.run', the",
            "task will run async from the original task, the loop will continue",
            "if the function provided returns true"
        ],
        params = [FUNCTION, "boolSupplier", "the function to check if the loop should run"],
        returns = [TASK, "the task, this allows for chaining"],
        examples = [
            """
            task = new Task()
                .then(fun() print("hello"))
                .then(fun() print(" "))
                .then(fun() print("world"))
                .loopIf(fun() true); // Always loop
            """
        ]
    )
    private fun loopIf(arguments: Arguments): ClassInstance {
        val instance = arguments.next()
        val task = instance.asPrimitive(this)
        val supplier = arguments.nextPrimitive(FunctionDef::class)
        task.addTask(BuiltInFunction.of("\$lambda", {
            val shouldRun = supplier.invoke(it.interpreter, listOf()).getPrimitive(BooleanDef::class)
            shouldRun ?: runtimeError("'loopIf' check should return type 'Boolean'")
            if (shouldRun) {
                task.run()
            }
        }))
        return instance
    }

    @FunctionDoc(
        name = "run",
        desc = [
            "This runs the task asynchronously and returns a future which can be awaited.",
            "The last function in the task will be used as the return value for the future"
        ],
        returns = [FUTURE, "the future value that can be awaited"],
        examples = [
            """
            task = new Task()
                .then(fun() print("hello"))
                .then(fun() print(" "))
                .then(fun() print("world"))
                .then(fun() 10);
            f = task.run(); // prints 'hello world'
            print(f.await()); // prints 10
            """
        ]
    )
    private fun run(arguments: Arguments): Future<ClassInstance?> {
        return arguments.nextPrimitive(this).run()
    }
}