package me.senseiwells.arucas.builtin

import me.senseiwells.arucas.api.ArucasExtension
import me.senseiwells.arucas.api.docs.FunctionDoc
import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.core.Arucas
import me.senseiwells.arucas.exceptions.Propagator
import me.senseiwells.arucas.exceptions.runtimeError
import me.senseiwells.arucas.utils.Arguments
import me.senseiwells.arucas.utils.BuiltInFunction
import me.senseiwells.arucas.utils.Util
import me.senseiwells.arucas.utils.Util.Types.BOOLEAN
import me.senseiwells.arucas.utils.Util.Types.ITERABLE
import me.senseiwells.arucas.utils.Util.Types.NUMBER
import me.senseiwells.arucas.utils.Util.Types.OBJECT
import me.senseiwells.arucas.utils.Util.Types.STRING
import me.senseiwells.arucas.utils.impl.ArucasIterable
import me.senseiwells.arucas.utils.impl.ArucasThread
import java.io.IOException
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class BuiltInExtension: ArucasExtension {
    private companion object {
        val INPUT_LOCK = Any()
    }

    override fun getName() = "BuiltInExtension"

    override fun getBuiltInFunctions(): List<BuiltInFunction> {
        return listOf(
            BuiltInFunction.of("print", 1, this::print),
            BuiltInFunction.arb("print", this::printVarArgs),
            BuiltInFunction.of("printDebug", this::printDebug),
            BuiltInFunction.of("input", 1, this::input),
            BuiltInFunction.of("sleep", 1, this::sleep),
            BuiltInFunction.of("isDebug", this::isDebug),
            BuiltInFunction.of("debug", 1, this::debug),
            BuiltInFunction.of("isExperimental", this::isExperimental),
            BuiltInFunction.of("experimental", 1, this::experimental),
            BuiltInFunction.of("suppressDeprecated", 1, this::suppressDeprecated),
            BuiltInFunction.of("isMain", this::isMain),
            BuiltInFunction.of("getArucasVersion", this::getArucasVersion),

            BuiltInFunction.of("runFromString", 1, this::runFromString, "Use 'eval(code)' instead"),
            BuiltInFunction.of("eval", 1, this::eval),
            BuiltInFunction.of("run", 1, this::run),
            BuiltInFunction.of("stop", this::stop),

            BuiltInFunction.of("len", 1, this::len),
            BuiltInFunction.of("random", 1, this::random),
            BuiltInFunction.of("range", 1, this::range1),
            BuiltInFunction.of("range", 2, this::range2),
            BuiltInFunction.of("range", 3, this::range3),

            BuiltInFunction.of("getTime", this::getTime),
            BuiltInFunction.of("getNanoTime", this::getNanoTime),
            BuiltInFunction.of("getMilliTime", this::getMilliTime),
            BuiltInFunction.of("getUnixTime", this::getUnixTime),
            BuiltInFunction.of("getDate", this::getDate),
        )
    }

    @FunctionDoc(
        name = "print",
        desc = ["This prints a value to the output handler"],
        params = [OBJECT, "printValue", "the value to print"],
        examples = ["print('Hello World');"]
    )
    private fun print(arguments: Arguments) {
        arguments.interpreter.api.getOutput().println(arguments.next().toString(arguments.interpreter))
    }

    @FunctionDoc(
        isVarArgs = true,
        name = "print",
        desc = [
            "This prints a number of values to the console",
            "If there are no arguments then this will print a new line,",
            "other wise it will print the contents without a new line"
        ],
        params = [OBJECT, "printValue...", "the value to print"],
        examples = ["print('Hello World', 'This is a test', 123);"]
    )
    private fun printVarArgs(arguments: Arguments) {
        if (!arguments.hasNext()) {
            arguments.interpreter.api.getOutput().println()
            return
        }
        val output = StringBuilder()
        while (arguments.hasNext()) {
            output.append(arguments.next().toString(arguments.interpreter))
        }
        arguments.interpreter.api.getOutput().println(output.toString())
    }

    @FunctionDoc(
        name = "printDebug",
        desc = [
            "This logs something to the debug output.",
            "It only prints if debug mode is enabled: `debug(true)`"
        ],
        params = [OBJECT, "printValue", "the value to print"],
        examples = [
            """
            debug(true); // Enable debug for testing
            if (true) {
                printDebug("Inside if statement");
            }
            """
        ]
    )
    private fun printDebug(arguments: Arguments) {
        arguments.interpreter.api.getOutput().logln(arguments.next().toString(arguments.interpreter))
    }

    @FunctionDoc(
        name = "input",
        desc = ["This is used to take an input from the user"],
        params = [STRING, "prompt", "the prompt to show the user"],
        returns = [STRING, "the input from the user"],
        examples = ["input('What is your name?');"]
    )
    private fun input(arguments: Arguments): String {
        // This just ensures that we don't try to take
        // multiple inputs from different threads
        synchronized(INPUT_LOCK) {
            val prompt = arguments.nextPrimitive(StringDef::class)
            arguments.interpreter.api.getOutput().println(prompt)
            arguments.interpreter.canInterrupt {
                return arguments.interpreter.api.getInput().takeInput().get()
            }
        }
    }

    @FunctionDoc(
        name = "sleep",
        desc = ["This pauses your program for a certain amount of milliseconds"],
        params = [NUMBER, "milliseconds", "milliseconds to sleep"],
        examples = ["sleep(1000);"]
    )
    private fun sleep(arguments: Arguments) {
        if (Thread.currentThread() !is ArucasThread) {
            runtimeError("'sleep' function can only be called on an Arucas thread")
        }
        val time = arguments.nextPrimitive(NumberDef::class)
        arguments.interpreter.canInterrupt {
            Thread.sleep(time.toLong())
        }
    }

    @FunctionDoc(
        name = "isDebug",
        desc = ["This is used to determine whether the interpreter is in debug mode"],
        examples = ["isDebug();"]
    )
    private fun isDebug(arguments: Arguments): Boolean {
        return arguments.interpreter.properties.isDebug
    }

    @FunctionDoc(
        name = "debug",
        desc = ["This is used to enable or disable debug mode"],
        params = [BOOLEAN, "bool", "true to enable debug mode, false to disable debug mode"],
        examples = ["debug(true);"]
    )
    private fun debug(arguments: Arguments) {
        arguments.interpreter.properties.isDebug = arguments.nextPrimitive(BooleanDef::class)
    }

    @FunctionDoc(
        name = "isExperimental",
        desc = ["This is used to determine whether the interpreter is in experimental mode"],
        examples = ["isExperimental();"]
    )
    private fun isExperimental(arguments: Arguments): Boolean {
        return arguments.interpreter.properties.isExperimental
    }

    @FunctionDoc(
        name = "experimental",
        desc = ["This is used to enable or disable experimental mode"],
        params = [BOOLEAN, "bool", "true to enable experimental mode, false to disable experimental mode"],
        examples = ["experimental(true);"]
    )
    private fun experimental(arguments: Arguments) {
        arguments.interpreter.properties.isExperimental = arguments.nextPrimitive(BooleanDef::class)
    }

    @FunctionDoc(
        name = "suppressDeprecated",
        desc = ["This is used to enable or disable suppressing deprecation warnings"],
        params = [BOOLEAN, "bool", "true to enable, false to disable warnings"],
        examples = ["suppressDeprecated(true);"]
    )
    private fun suppressDeprecated(arguments: Arguments) {
        arguments.interpreter.properties.logDeprecated = arguments.nextPrimitive(BooleanDef::class)
    }

    @FunctionDoc(
        name = "isMain",
        desc = ["This is used to check whether the script is the main script"],
        returns = [BOOLEAN, "true if the script is the main script, false if it is not"],
        examples = ["isMain();"]
    )
    private fun isMain(arguments: Arguments): Boolean {
        return arguments.interpreter.isMain
    }

    @FunctionDoc(
        name = "getArucasVersion",
        desc = ["This is used to get the version of Arucas that is currently running"],
        returns = [STRING, "the version of Arucas that is currently running"],
        examples = ["getArucasVersion();"]
    )
    @Suppress("UNUSED_PARAMETER")
    private fun getArucasVersion(arguments: Arguments): String {
        return Arucas.VERSION
    }

    @FunctionDoc(
        deprecated = ["This should be replaced with 'eval(code)'"],
        name = "runFromString",
        desc = [
            "This is used to evaluate a string as code.",
            "This will not inherit imports that are in the parent script"
        ],
        params = [STRING, "code", "the code to run"],
        examples = ["runFromString('print(\"Hello World\");');"]
    )
    private fun runFromString(arguments: Arguments): ClassInstance {
        return this.eval(arguments)
    }

    @FunctionDoc(
        name = "eval",
        desc = [
            "This is used to evaluate a string as code.",
            "This will not inherit imports that are in the parent script"
        ],
        params = [STRING, "code", "the code to evaluate"],
        returns = [OBJECT, "the result of the evaluation"],
        examples = ["eval('1 + 1');"]
    )
    private fun eval(arguments: Arguments): ClassInstance {
        val code = arguments.nextPrimitive(StringDef::class)
        val child = arguments.interpreter.child(code, "\$eval")
        return child.executeBlocking()
    }

    @FunctionDoc(
        name = "run",
        desc = ["This is used to run a .arucas file, you can use on script to run other scripts"],
        params = [STRING, "path", "as a file path"],
        returns = [OBJECT, "any value that the file returns"],
        examples = ["run('/home/user/script.arucas');"]
    )
    private fun run(arguments: Arguments): Any {
        val pathString = arguments.nextPrimitive(StringDef::class)
        try {
            val path = Path.of(pathString)
            val fileName = path.fileName.toString()
            val content = Files.readString(path)
            val child = arguments.interpreter.child(content, fileName)
            return child.executeBlocking()
        } catch (e: IOException) {
            runtimeError("Failed to read file '$pathString'")
        } catch (e: InvalidPathException) {
            runtimeError("Invalid path '$pathString'")
        }
    }

    @FunctionDoc(
        name = "stop",
        desc = ["This is used to stop a script"],
        examples = ["stop();"]
    )
    @Suppress("UNUSED_PARAMETER")
    private fun stop(arguments: Arguments) {
        throw Propagator.Stop.INSTANCE
    }

    @FunctionDoc(
        name = "len",
        desc = ["This is used to get the length of a collection or string"],
        params = [STRING, "sizable", "the collection or string"],
        examples = ["len(\"Hello World\");"]
    )
    private fun len(arguments: Arguments): Number {
        return when {
            arguments.isNext(CollectionDef::class) -> {
                arguments.nextCollection().callMemberPrimitive(
                    arguments.interpreter, "size", listOf(), NumberDef::class
                )
            }
            arguments.isNext(StringDef::class) -> {
                arguments.nextString().callMemberPrimitive(
                    arguments.interpreter, "length", listOf(), NumberDef::class
                )
            }
            else -> {
                runtimeError("Cannot pass ... into len()")
            }
        }
    }

    @FunctionDoc(
        name = "random",
        desc = ["This is used to generate a random integer between 0 and the bound"],
        params = [NUMBER, "bound", "the maximum bound (exclusive)"],
        returns = [NUMBER, "the random integer"],
        examples = ["random(10);"]
    )
    private fun random(arguments: Arguments): Number {
        val bound = arguments.nextPrimitive(NumberDef::class).toInt()
        if (bound <= 0) {
            runtimeError("Bound must be positive")
        }
        return Random.nextInt(bound)
    }

    @FunctionDoc(
        name = "range",
        desc = ["This is used to generate a range of integers starting from 0, incrementing by 1"],
        params = [NUMBER, "bound", "the maximum bound (exclusive)"],
        returns = [ITERABLE, "an iterable object that returns the range of integers"],
        examples = ["range(10);"]
    )
    private fun range1(arguments: Arguments): ArucasIterable {
        val bound = arguments.nextPrimitive(NumberDef::class)
        return Util.Collection.rangeIterable(arguments.interpreter, bound)
    }

    @FunctionDoc(
        name = "range",
        desc = [
            "This is used to generate a range of numbers starting",
            "from a start value and ending at a bound value incrementing by 1"
        ],
        params = [NUMBER, "start", "the start value", NUMBER, "bound", "the maximum bound (exclusive)"],
        returns = [ITERABLE, "an iterable object that returns the range of integers"],
        examples = ["range(0, 10);"]
    )
    private fun range2(arguments: Arguments): ArucasIterable {
        val start = arguments.nextPrimitive(NumberDef::class)
        val bound = arguments.nextPrimitive(NumberDef::class)
        return Util.Collection.rangeIterable(arguments.interpreter, bound, start)
    }

    @FunctionDoc(
        name = "range",
        desc = [
            "This is used to generate a range of numbers starting from a",
            "start value and ending at a bound value incrementing by a step value"],
        params = [
            NUMBER, "start", "the start value",
            NUMBER, "bound", "the maximum bound (exclusive)",
            NUMBER, "step", "the step value"
        ],
        returns = [ITERABLE, "an iterable object that returns the range of integers"],
        examples = ["range(0, 10, 2);"]
    )
    private fun range3(arguments: Arguments): ArucasIterable {
        val start = arguments.nextPrimitive(NumberDef::class)
        val bound = arguments.nextPrimitive(NumberDef::class)
        val step = arguments.nextPrimitive(NumberDef::class)
        return Util.Collection.rangeIterable(arguments.interpreter, bound, start, step)
    }

    @FunctionDoc(
        name = "getTime",
        desc = ["This is used to get the current time formatted with HH:mm:ss in your local time"],
        returns = [STRING, "the current time formatted with HH:mm:ss"],
        examples = ["getTime();"]
    )
    @Suppress("UNUSED_PARAMETER")
    private fun getTime(arguments: Arguments): String {
        return DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now())
    }

    @FunctionDoc(
        name = "getNanoTime",
        desc = ["This is used to get the current time in nanoseconds"],
        returns = [NUMBER, "the current time in nanoseconds"],
        examples = ["getNanoTime();"]
    )
    @Suppress("UNUSED_PARAMETER")
    private fun getNanoTime(arguments: Arguments): Long {
        return System.nanoTime()
    }

    @FunctionDoc(
        name = "getMilliTime",
        desc = ["This is used to get the current time in milliseconds"],
        returns = [NUMBER, "the current time in milliseconds"],
        examples = ["getMilliTime();"]
    )
    @Suppress("UNUSED_PARAMETER")
    private fun getMilliTime(arguments: Arguments): Long {
        return System.currentTimeMillis()
    }

    @FunctionDoc(
        name = "getUnixTime",
        desc = ["This is used to get the current time in seconds since the Unix epoch"],
        returns = [NUMBER, "the current time in seconds since the Unix epoch"],
        examples = ["getUnixTime();"]
    )
    @Suppress("UNUSED_PARAMETER")
    private fun getUnixTime(arguments: Arguments): Long {
        return System.currentTimeMillis() / 1_000
    }

    @FunctionDoc(
        name = "getDate",
        desc = ["This is used to get the current date formatted with dd/MM/yyyy in your local time"],
        returns = [STRING, "the current date formatted with dd/MM/yyyy"],
        examples = ["getDate();"]
    )
    @Suppress("UNUSED_PARAMETER")
    private fun getDate(arguments: Arguments): String {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now())
    }
}
