package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.annotations.*
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.functions.builtin.Arguments
import me.senseiwells.arucas.functions.builtin.ConstructorFunction
import me.senseiwells.arucas.functions.builtin.MemberFunction
import me.senseiwells.arucas.interpreter.Interpreter
import me.senseiwells.arucas.utils.impl.ArucasTask
import me.senseiwells.arucas.utils.impl.Task
import me.senseiwells.arucas.utils.misc.Types.TASK
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
            MemberFunction.of("waitThen", 2, this::waitThen),
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
        params = [ParameterDoc(FunctionDef::class, "function", ["The function to run at the end of the task."])],
        returns = ReturnDoc(TaskDef::class, ["The task, this allows for chaining."]),
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
        instance.asPrimitive(this).addTask(0, arguments.nextPrimitive(FunctionDef::class))
        return instance
    }

    private fun waitThen(arguments: Arguments): ClassInstance {
        val instance = arguments.next()
        val delay = arguments.nextPrimitive(NumberDef::class).toInt()
        instance.asPrimitive(this).addTask(delay, arguments.nextPrimitive(FunctionDef::class))
        return instance
    }

    @FunctionDoc(
        name = "run",
        desc = [
            "This runs the task asynchronously and returns a future which can be awaited.",
            "The last function in the task will be used as the return value for the future"
        ],
        returns = ReturnDoc(FutureDef::class, ["The future value that can be awaited."]),
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