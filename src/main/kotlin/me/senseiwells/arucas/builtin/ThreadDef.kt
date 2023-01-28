package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.docs.ClassDoc
import me.senseiwells.arucas.api.docs.FunctionDoc
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.classes.CreatableDefinition
import me.senseiwells.arucas.core.Interpreter
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.BuiltInFunction
import me.senseiwells.arucas.utils.MemberFunction
import me.senseiwells.arucas.utils.Util.Types.BOOLEAN
import me.senseiwells.arucas.utils.Util.Types.FUNCTION
import me.senseiwells.arucas.utils.Util.Types.NUMBER
import me.senseiwells.arucas.utils.Util.Types.STRING
import me.senseiwells.arucas.utils.Util.Types.THREAD
import me.senseiwells.arucas.utils.impl.ArucasThread

@ClassDoc(
    name = THREAD,
    desc = [
        "This class allows to to create threads for async executions.",
        "This class cannot be instantiated or extended. To create a new",
        "thread use the static method 'Thread.runThreaded()'"
    ]
)
class ThreadDef(interpreter: Interpreter): CreatableDefinition<ArucasThread>(THREAD, interpreter) {
    override fun canExtend() = false

    override fun defineStaticMethods(): List<BuiltInFunction> {
        return listOf(
            BuiltInFunction.of("getCurrentThread", this::getCurrentThread),
            BuiltInFunction.of("runThreaded", 1, this::runThreaded1),
            BuiltInFunction.of("runThreaded", 2, this::runThreaded2),
            BuiltInFunction.of("freeze", this::freeze)
        )
    }

    @FunctionDoc(
        isStatic = true,
        name = "getCurrentThread",
        desc = [
            "This gets the current thread that the code is running on,",
            "this may throw an error if the thread is not safe to get,",
            "which happens when running outside of Arucas Threads"
        ],
        returns = [THREAD, "the current thread"],
        examples = ["Thread.getCurrentThread();"]
    )
    @Suppress("UNUSED_PARAMETER")
    private fun getCurrentThread(arguments: Arguments): ClassInstance {
        val current = Thread.currentThread()
        if (current is ArucasThread) {
            return this.create(current)
        }
        runtimeError("Thread is not safe to get")
    }

    @FunctionDoc(
        isStatic = true,
        name = "runThreaded",
        desc = [
            "This starts a new thread and runs a function on it, the thread will",
            "terminate when it finishes executing the function, threads will stop automatically",
            "when the program stops, you are also able to stop threads by using the Thread object"
        ],
        params = [FUNCTION, "function", "the function you want to run on a new thread"],
        returns = [THREAD, "the new thread"],
        examples = [
            """
            Thread.runThreaded(fun() {
                print("Running asynchronously!");
            });
            """
        ]
    )
    private fun runThreaded1(arguments: Arguments): ClassInstance {
        val function = arguments.nextFunction()
        return this.create(arguments.interpreter.runFunctionOnThread(function))
    }

    @FunctionDoc(
        isStatic = true,
        name = "runThreaded",
        desc = ["This starts a new thread with a specific name and runs a function on it"],
        params = [
            STRING, "name", "the name of the thread",
            FUNCTION, "function", "the function you want to run on a new thread"
        ],
        returns = [THREAD, "the new thread"],
        examples = [
            """
            Thread.runThreaded("MyThread", fun() {
                print("Running asynchronously on MyThread");
            });
            """
        ]
    )
    private fun runThreaded2(arguments: Arguments): ClassInstance {
        val name = arguments.nextPrimitive(StringDef::class)
        val function = arguments.nextFunction()
        return this.create(arguments.interpreter.runFunctionOnThread(function, name))
    }

    @FunctionDoc(
        isStatic = true,
        name = "freeze",
        desc = [
            "This freezes the current thread, stops anything else from executing on the thread.",
            "This may fail if you try to freeze a non Arucas Thread in which case an error will be thrown"
        ],
        examples = ["Thread.freeze();"]
    )
    @Suppress("UNUSED_PARAMETER")
    private fun freeze(arguments: Arguments) {
        val current = Thread.currentThread()
        if (current !is ArucasThread) {
            runtimeError("Thread is not safe to freeze")
        }
        current.freeze()
    }

    override fun defineMethods(): List<MemberFunction> {
        return listOf(
            MemberFunction.of("isAlive", this::isAlive),
            MemberFunction.of("getAge", this::getAge),
            MemberFunction.of("getName", this::getName),
            MemberFunction.of("stop", this::stop),
            MemberFunction.of("thaw", this::thaw),
            MemberFunction.of("freeze", this::freezeInstance)
        )
    }

    @FunctionDoc(
        name = "isAlive",
        desc = ["This checks if the thread is alive (still running)"],
        returns = [BOOLEAN, "true if the thread is alive, false if not"],
        examples = ["Thread.getCurrentThread().isAlive();"]
    )
    private fun isAlive(arguments: Arguments): Boolean {
        return arguments.nextPrimitive(this).isAlive
    }

    @FunctionDoc(
        name = "getAge",
        desc = ["This gets the age of the thread in milliseconds"],
        returns = [NUMBER, "the age of the thread"],
        examples = ["Thread.getCurrentThread().getAge();"]
    )
    private fun getAge(arguments: Arguments): Long {
        return System.currentTimeMillis() - arguments.nextPrimitive(this).startTime!!
    }

    @FunctionDoc(
        name = "getName",
        desc = ["This gets the name of the thread"],
        returns = [STRING, "the name of the thread"],
        examples = ["Thread.getCurrentThread().getName();"]
    )
    private fun getName(arguments: Arguments): String {
        return arguments.nextPrimitive(this).name
    }

    @FunctionDoc(
        name = "stop",
        desc = [
            "This stops the thread from executing, anything that was running will be instantly stopped.",
            "This method will fail if the thread is not alive"
        ],
        examples = ["Thread.getCurrentThread().stop();"]
    )
    private fun stop(arguments: Arguments) {
        arguments.nextPrimitive(this).stopThread()
    }

    @FunctionDoc(
        name = "freeze",
        desc = [
            "This serves the same purpose as 'Thread.freeze()' however this works on the current",
            "thread instance, unlike 'Thread.freeze()' this cannot throw an error."
        ],
        examples = ["Thread.getCurrentThread().freeze()"]
    )
    private fun freezeInstance(arguments: Arguments) {
        val thread = arguments.nextPrimitive(this)
        if (Thread.currentThread() === thread) {
            thread.freeze()
        } else {
            thread.isFrozen = true
        }
    }

    @FunctionDoc(
        name = "thaw",
        desc = [
            "This will thaw the thread from its frozen state, if the thread is not frozen then an",
            "error will be thrown"
        ],
        examples = ["Thread.getCurrentThread().thaw();"]
    )
    private fun thaw(arguments: Arguments) {
        val thread = arguments.nextPrimitive(this)
        if (!thread.isFrozen) {
            runtimeError("Thread is not frozen")
        }
        thread.thaw()
    }
}