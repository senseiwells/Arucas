package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.core.Arucas;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.BuiltInException;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.Delegatable;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static me.senseiwells.arucas.utils.ValueTypes.*;

/**
 * Built-in extension for Arucas. Provides many standard functions. <br>
 * Fully Documented.
 *
 * @author senseiwells
 */
public class ArucasBuiltInExtension implements IArucasExtension {
	private final Random random = new Random();

	@Override
	public String getName() {
		return "BuiltInExtension";
	}

	@Override
	public ArucasFunctionMap<BuiltInFunction> getDefinedFunctions() {
		return this.builtInFunctions;
	}

	private final ArucasFunctionMap<BuiltInFunction> builtInFunctions = ArucasFunctionMap.of(
		BuiltInFunction.of("run", 1, this::run),
		BuiltInFunction.of("stop", this::stop),
		BuiltInFunction.of("sleep", 1, this::sleep),
		BuiltInFunction.of("print", 1, this::print),
		BuiltInFunction.arbitrary("print", this::fullPrint),
		BuiltInFunction.of("input", 1, this::input),
		BuiltInFunction.of("debug", 1, this::debug),
		BuiltInFunction.of("experimental", 1, this::experimental),
		BuiltInFunction.of("suppressDeprecated", 1, this::suppressDeprecated),
		BuiltInFunction.of("isMain", this::isMain),
		BuiltInFunction.of("getArucasVersion", this::getArucasVersion),
		BuiltInFunction.of("random", 1, this::random),
		BuiltInFunction.of("getTime", this::getTime),
		BuiltInFunction.of("getNanoTime", this::getNanoTime),
		BuiltInFunction.of("getMilliTime", this::getMilliTime),
		BuiltInFunction.of("getUnixTime", this::getUnixTime),
		BuiltInFunction.of("getDate", this::getDate),
		BuiltInFunction.of("len", 1, this::len),
		BuiltInFunction.of("throwRuntimeError", 1, this::throwRuntimeError, "Use 'throw new Error()'"),
		BuiltInFunction.of("callFunctionWithList", 2, this::callFunctionWithList, "Use 'Function.callWithList()'"),
		BuiltInFunction.of("runFromString", 1, this::runFromString)
	);

	@FunctionDoc(
		name = "run",
		desc = "This is used to run a .arucas file, you can use on script to run other scripts",
		params = {STRING, "path", "as a file path"},
		returns = {ANY, "any value that the file returns"},
		throwMsgs = "Failed to execute script...",
		example = "run('/home/user/script.arucas');"
	)
	private Value run(Arguments arguments) throws CodeError {
		StringValue stringValue = arguments.getNext(StringValue.class);
		String filePath = new File(stringValue.value).getAbsolutePath();
		try {
			Path path = Path.of(filePath);
			Path fileNamePath = path.getFileName();
			String fileName = fileNamePath == null ? filePath : fileNamePath.toString();
			String fileContent = Files.readString(path);
			return Run.run(arguments.getContext(), fileName, fileContent, false);
		}
		catch (IOException | OutOfMemoryError | InvalidPathException e) {
			throw arguments.getError("Failed to execute script '%s'\n%s", filePath, ExceptionUtils.getStackTrace(e));
		}
	}

	@FunctionDoc(
		name = "stop",
		desc = "This is used to stop a script",
		example = "stop();"
	)
	private Value stop(Arguments arguments) throws CodeError {
		throw new ThrowStop();
	}

	@FunctionDoc(
		name = "sleep",
		desc = "This pauses your program for a certain amount of milliseconds",
		params = {NUMBER, "milliseconds", "milliseconds to sleep"},
		example = "sleep(1000);"
	)
	private Value sleep(Arguments arguments) throws CodeError {
		NumberValue numberValue = arguments.getNext(NumberValue.class);
		try {
			Thread.sleep(numberValue.value.longValue());
		}
		catch (InterruptedException e) {
			throw new CodeError(CodeError.ErrorType.INTERRUPTED_ERROR, "", arguments.getPosition());
		}
		return NullValue.NULL;
	}

	@FunctionDoc(
		name = "print",
		desc = "This prints a value to the console",
		params = {ANY, "printValue", "the value to print"},
		example = "print('Hello World');"
	)
	private Value print(Arguments arguments) throws CodeError {
		Context context = arguments.getContext();
		context.getOutput().println(arguments.getNext().getAsString(context));
		return NullValue.NULL;
	}

	@FunctionDoc(
		isVarArgs = true,
		name = "print",
		desc = "This prints a number of values to the console",
		params = {ANY, "printValue...", "the value to print"},
		example = "print('Hello World', 'This is a test', 123);"
	)
	private Value fullPrint(Arguments arguments) throws CodeError {
		Context context = arguments.getContext();
		if (arguments.size() == 0) {
			context.getOutput().println();
			return NullValue.NULL;
		}

		StringBuilder builder = new StringBuilder();
		for (Value value : arguments.getAll()) {
			builder.append(value.getAsString(context));
		}
		context.getOutput().print(builder.toString());
		return NullValue.NULL;
	}

	@FunctionDoc(
		name = "input",
		desc = "This is used to take an input from the user",
		params = {STRING, "prompt", "the prompt to show the user"},
		returns = {STRING, "the input from the user"},
		example = "input('What is your name?');"
	)
	private synchronized Value input(Arguments arguments) throws CodeError {
		StringValue stringValue = arguments.getNext(StringValue.class);
		Context context = arguments.getContext();
		context.getOutput().println(stringValue.value);
		String input = ExceptionUtils.catchAsNull(() -> context.getInput().takeInput().get());
		if (input == null) {
			throw new BuiltInException("Could not take input");
		}
		return StringValue.of(input);
	}

	@FunctionDoc(
		name = "debug",
		desc = "This is used to enable or disable debug mode",
		params = {BOOLEAN, "bool", "true to enable debug mode, false to disable debug mode"},
		example = "debug(true);"
	)
	private Value debug(Arguments arguments) throws CodeError {
		arguments.getContext().setDebug(arguments.getNextGeneric(BooleanValue.class));
		return NullValue.NULL;
	}

	@FunctionDoc(
		name = "experimental",
		desc = "This is used to enable or disable experimental mode",
		params = {BOOLEAN, "bool", "true to enable experimental mode, false to disable experimental mode"},
		example = "experimental(true);"
	)
	private Value experimental(Arguments arguments) throws CodeError {
		arguments.getContext().setExperimental(arguments.getNextGeneric(BooleanValue.class));
		return NullValue.NULL;
	}

	@FunctionDoc(
		name = "suppressDeprecated",
		desc = "This is used to enable or disable suppressing deprecation warnings",
		params = {BOOLEAN, "bool", "true to enable suppressing deprecation warnings, false to disable suppressing deprecation warnings"},
		example = "suppressDeprecated(true);"
	)
	private Value suppressDeprecated(Arguments arguments) throws CodeError {
		arguments.getContext().setSuppressDeprecated(arguments.getNextGeneric(BooleanValue.class));
		return NullValue.NULL;
	}

	@FunctionDoc(
		name = "isMain",
		desc = "This is used to check whether the script is the main script",
		returns = {BOOLEAN, "true if the script is the main script, false if it is not"},
		example = "isMain();"
	)
	private Value isMain(Arguments arguments) {
		return BooleanValue.of(arguments.getContext().isMain());
	}

	@FunctionDoc(
		name = "getArucasVersion",
		desc = "This is used to get the version of Arucas that is currently running",
		returns = {STRING, "the version of Arucas that is currently running"},
		example = "getArucasVersion();"
	)
	private Value getArucasVersion(Arguments arguments) {
		return StringValue.of(Arucas.VERSION);
	}


	@FunctionDoc(
		name = "random",
		desc = "This is used to generate a random integer between 0 and the bound",
		params = {NUMBER, "bound", "the maximum bound (exclusive)"},
		returns = {NUMBER, "the random integer"},
		example = "random(10);"
	)
	private Value random(Arguments arguments) throws CodeError {
		NumberValue numValue = arguments.getNext(NumberValue.class);
		return NumberValue.of(this.random.nextInt(numValue.value.intValue()));
	}

	@FunctionDoc(
		name = "getTime",
		desc = "This is used to get the current time formatted with HH:mm:ss in your local time",
		returns = {STRING, "the current time formatted with HH:mm:ss"},
		example = "getTime();"
	)
	private Value getTime(Arguments arguments) {
		return StringValue.of(DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()));
	}

	@FunctionDoc(
		name = "getNanoTime",
		desc = "This is used to get the current time in nanoseconds",
		returns = {NUMBER, "the current time in nanoseconds"},
		example = "getNanoTime();"
	)
	private Value getNanoTime(Arguments arguments) {
		return NumberValue.of(System.nanoTime());
	}

	@FunctionDoc(
		name = "getMilliTime",
		desc = "This is used to get the current time in milliseconds",
		returns = {NUMBER, "the current time in milliseconds"},
		example = "getMilliTime();"
	)
	private Value getMilliTime(Arguments arguments) {
		return NumberValue.of(System.currentTimeMillis());
	}

	@FunctionDoc(
		name = "getUnixTime",
		desc = "This is used to get the current time in seconds since the Unix epoch",
		returns = {NUMBER, "the current time in seconds since the Unix epoch"},
		example = "getUnixTime();"
	)
	private Value getUnixTime(Arguments arguments) {
		return NumberValue.of(System.currentTimeMillis() / 1000F);
	}

	@FunctionDoc(
		name = "getDate",
		desc = "This is used to get the current date formatted with dd/MM/yyyy in your local time",
		returns = {STRING, "the current date formatted with dd/MM/yyyy"},
		example = "getDate();"
	)
	private Value getDate(Arguments arguments) {
		return StringValue.of(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now()));
	}

	@FunctionDoc(
		name = "len",
		desc = "This is used to get the length of a collection or string",
		params = {STRING, "collection", "the collection or string"},
		throwMsgs = "Cannot pass ... into len()",
		example = "len(\"Hello World\");"
	)
	private Value len(Arguments arguments) throws CodeError {
		Value value = arguments.getNext();
		if (value instanceof StringValue stringValue) {
			return NumberValue.of(stringValue.value.length());
		}
		if (value.isCollection()) {
			return NumberValue.of(value.asCollection(arguments.getContext(), arguments.getPosition()).size());
		}
		if (value instanceof FunctionValue functionValue) {
			// If the function is a member we don't include itself in the length
			if (value instanceof Delegatable) {
				return NumberValue.of(functionValue.getCount() - 1);
			}
			return NumberValue.of(functionValue.getCount());
		}
		throw arguments.getError("Cannot pass %s into len()", value);
	}

	@FunctionDoc(
		deprecated = "You should use the `throw` keyword",
		name = "throwRuntimeError",
		desc = "This is used to throw a runtime error",
		params = {STRING, "message", "the message of the error"},
		throwMsgs = "the error with the message",
		example = "throwRuntimeError('I'm throwing this error');"
	)
	private Value throwRuntimeError(Arguments arguments) throws CodeError {
		StringValue stringValue = arguments.getNext(StringValue.class);
		throw arguments.getError(stringValue.value);
	}

	@FunctionDoc(
		deprecated = "You should use Function class `Function.callWithList(fun() {}, [])`",
		name = "callFunctionWithList",
		desc = "This is used to call a function with a list of arguments",
		params = {
			FUNCTION, "function", "the function",
			LIST, "list", "the list of arguments"
		},
		returns = {ANY, "the return value of the function"},
		example = "callFunctionWithList(fun(n1, n2, n3) { }, [1, 2, 3]);"
	)
	private Value callFunctionWithList(Arguments arguments) throws CodeError {
		FunctionValue functionValue = arguments.getNext(FunctionValue.class);
		ArucasList listValue = arguments.getNextGeneric(ListValue.class);
		return functionValue.call(arguments.getContext(), listValue);
	}

	@FunctionDoc(
		name = "runFromString",
		desc = "This is used to evaluate a string as a script",
		params = {STRING, "string", "the string to evaluate"},
		returns = {ANY, "the return value of the script"},
		example = "runFromString('return 1;');"
	)
	private Value runFromString(Arguments arguments) throws CodeError {
		StringValue stringValue = arguments.getNext(StringValue.class);
		return Run.run(arguments.getContext().createBranch(), "string-run", stringValue.value, arguments.getContext().isMain());
	}
}
