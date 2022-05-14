package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.core.Arucas;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.IArucasCollection;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.IMemberFunction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static me.senseiwells.arucas.utils.ValueTypes.*;

/**
 * Built-in extension for Arucas. Provides many standard functions. <br>
 * Fully Documented.
 * @author senseiwells
 */
public class ArucasBuiltInExtension implements IArucasExtension {
	private final Scanner scanner = new Scanner(System.in);
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
		new BuiltInFunction("run", "path", this::run),
		new BuiltInFunction("stop", this::stop),
		new BuiltInFunction("sleep", "milliseconds", this::sleep),
		new BuiltInFunction("print", "printValue", this::print),
		new BuiltInFunction.Arbitrary("print", this::fullPrint),
		new BuiltInFunction("input", "prompt", this::input),
		new BuiltInFunction("debug", "boolean", this::debug),
		new BuiltInFunction("experimental", "boolean", this::experimental),
		new BuiltInFunction("suppressDeprecated", "boolean", this::suppressDeprecated),
		new BuiltInFunction("isMain", this::isMain),
		new BuiltInFunction("getArucasVersion", this::getArucasVersion),
		new BuiltInFunction("random", "bound", this::random),
		new BuiltInFunction("getTime", this::getTime),
		new BuiltInFunction("getNanoTime", this::getNanoTime),
		new BuiltInFunction("getMilliTime", this::getMilliTime),
		new BuiltInFunction("getUnixTime", this::getUnixTime),
		new BuiltInFunction("getDate", this::getDate),
		new BuiltInFunction("len", "value", this::len),
		new BuiltInFunction("throwRuntimeError", "message", this::throwRuntimeError, "Use 'throw new Error()'"),
		new BuiltInFunction("callFunctionWithList", List.of("function", "argList"), this::callFunctionWithList, "Use 'Function.callWithList()'"),
		new BuiltInFunction("runFromString", "string", this::runFromString)
	);

	@FunctionDoc(
		name = "run",
		desc = "This is used to run a .arucas file, you can use on script to run other scripts",
		params = {STRING, "path", "as a file path"},
		returns = {ANY, "any value that the file returns"},
		throwMsgs = "Failed to execute script...",
		example = "run('/home/user/script.arucas')"
	)
	private Value<?> run(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		String filePath = new File(stringValue.value).getAbsolutePath();
		try {
			Context childContext = context.createChildContext(filePath);
			String fileContent = Files.readString(Path.of(filePath));
			return Run.run(childContext, filePath, fileContent);
		}
		catch (IOException | OutOfMemoryError | InvalidPathException e) {
			throw new RuntimeError("Failed to execute script '%s' \n%s".formatted(filePath, e), function.syntaxPosition, context);
		}
	}

	@FunctionDoc(
		name = "stop",
		desc = "This is used to stop a script",
		example = "stop()"
	)
	private Value<?> stop(Context context, BuiltInFunction function) throws CodeError {
		throw new ThrowStop();
	}

	@FunctionDoc(
		name = "sleep",
		desc = "This pauses your program for a certain amount of milliseconds",
		params = {NUMBER, "milliseconds", "milliseconds to sleep"},
		example = "sleep(1000)"
	)
	private Value<?> sleep(Context context, BuiltInFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		try {
			Thread.sleep(numberValue.value.longValue());
		}
		catch (InterruptedException e) {
			throw new CodeError(CodeError.ErrorType.INTERRUPTED_ERROR, "", function.syntaxPosition);
		}
		return NullValue.NULL;
	}

	@FunctionDoc(
		name = "print",
		desc = "This prints a value to the console",
		params = {ANY, "printValue", "the value to print"},
		example = "print('Hello World')"
	)
	private Value<?> print(Context context, BuiltInFunction function) throws CodeError {
		context.getOutput().println(function.getParameterValue(context, 0).getAsString(context));
		return NullValue.NULL;
	}

	@FunctionDoc(
		isVarArgs = true,
		name = "print",
		desc = "This prints a number of values to the console",
		params = {ANY, "printValue...", "the value to print"},
		example = "print('Hello World', 'This is a test', 123)"
	)
	private Value<?> fullPrint(Context context, BuiltInFunction function) throws CodeError {
		ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);

		if (listValue.value.isEmpty()) {
			context.getOutput().println();
			return NullValue.NULL;
		}

		StringBuilder builder = new StringBuilder();
		for (Value<?> value : listValue.value) {
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
		example = "input('What is your name?')"
	)
	private synchronized Value<?> input(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		context.getOutput().println(stringValue.value);
		return StringValue.of(this.scanner.nextLine());
	}

	@FunctionDoc(
		name = "debug",
		desc = "This is used to enable or disable debug mode",
		params = {BOOLEAN, "bool", "true to enable debug mode, false to disable debug mode"},
		example = "debug(true)"
	)
	private Value<?> debug(Context context, BuiltInFunction function) throws CodeError {
		context.setDebug(function.getParameterValueOfType(context, BooleanValue.class, 0).value);
		return NullValue.NULL;
	}

	@FunctionDoc(
		name = "experimental",
		desc = "This is used to enable or disable experimental mode",
		params = {BOOLEAN, "bool", "true to enable experimental mode, false to disable experimental mode"},
		example = "experimental(true)"
	)
	private Value<?> experimental(Context context, BuiltInFunction function) throws CodeError {
		context.setExperimental(function.getParameterValueOfType(context, BooleanValue.class, 0).value);
		return NullValue.NULL;
	}

	@FunctionDoc(
		name = "suppressDeprecated",
		desc = "This is used to enable or disable suppressing deprecation warnings",
		params = {BOOLEAN, "bool", "true to enable suppressing deprecation warnings, false to disable suppressing deprecation warnings"},
		example = "suppressDeprecated(true)"
	)
	private Value<?> suppressDeprecated(Context context, BuiltInFunction function) throws CodeError {
		context.setSuppressDeprecated(function.getParameterValueOfType(context, BooleanValue.class, 0).value);
		return NullValue.NULL;
	}

	@FunctionDoc(
		name = "isMain",
		desc = "This is used to check whether the script is the main script",
		returns = {BOOLEAN, "true if the script is the main script, false if it is not"},
		example = "isMain()"
	)
	private Value<?> isMain(Context context, BuiltInFunction function) {
		return BooleanValue.of(context.isMain());
	}

	@FunctionDoc(
		name = "getArucasVersion",
		desc = "This is used to get the version of Arucas that is currently running",
		returns = {STRING, "the version of Arucas that is currently running"},
		example = "getArucasVersion()"
	)
	private Value<?> getArucasVersion(Context context, BuiltInFunction function) {
		return StringValue.of(Arucas.VERSION);
	}


	@FunctionDoc(
		name = "random",
		desc = "This is used to generate a random integer between 0 and the bound",
		params = {NUMBER, "bound", "the maximum bound (exclusive)"},
		returns = {NUMBER, "the random integer"},
		example = "random(10)"
	)
	private Value<?> random(Context context, BuiltInFunction function) throws CodeError {
		NumberValue numValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return NumberValue.of(this.random.nextInt(numValue.value.intValue()));
	}

	@FunctionDoc(
		name = "getTime",
		desc = "This is used to get the current time formatted with HH:mm:ss in your local time",
		returns = {STRING, "the current time formatted with HH:mm:ss"},
		example = "getTime()"
	)
	private Value<?> getTime(Context context, BuiltInFunction function) {
		return StringValue.of(DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()));
	}

	@FunctionDoc(
		name = "getNanoTime",
		desc = "This is used to get the current time in nanoseconds",
		returns = {NUMBER, "the current time in nanoseconds"},
		example = "getNanoTime()"
	)
	private Value<?> getNanoTime(Context context, BuiltInFunction function) {
		return NumberValue.of(System.nanoTime());
	}

	@FunctionDoc(
		name = "getMilliTime",
		desc = "This is used to get the current time in milliseconds",
		returns = {NUMBER, "the current time in milliseconds"},
		example = "getMilliTime()"
	)
	private Value<?> getMilliTime(Context context, BuiltInFunction function) {
		return NumberValue.of(System.currentTimeMillis());
	}

	@FunctionDoc(
		name = "getUnixTime",
		desc = "This is used to get the current time in seconds since the Unix epoch",
		returns = {NUMBER, "the current time in seconds since the Unix epoch"},
		example = "getUnixTime()"
	)
	private Value<?> getUnixTime(Context context, BuiltInFunction function) {
		return NumberValue.of(System.currentTimeMillis() / 1000F);
	}

	@FunctionDoc(
		name = "getDate",
		desc = "This is used to get the current date formatted with dd/MM/yyyy in your local time",
		returns = {STRING, "the current date formatted with dd/MM/yyyy"},
		example = "getDate()"
	)
	private Value<?> getDate(Context context, BuiltInFunction function) {
		return StringValue.of(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now()));
	}

	@FunctionDoc(
		name = "len",
		desc = "This is used to get the length of a collection or string",
		params = {STRING, "collection", "the collection or string"},
		throwMsgs = "Cannot pass ... into len()",
		example = "len(\"Hello World\")"
	)
	private Value<?> len(Context context, BuiltInFunction function) throws CodeError {
		Value<?> value = function.getParameterValue(context, 0);
		if (value instanceof StringValue stringValue) {
			return NumberValue.of(stringValue.value.length());
		}
		if (value.value instanceof IArucasCollection collection) {
			return NumberValue.of(collection.size());
		}
		if (value instanceof FunctionValue functionValue) {
			// If the function is a member we don't include itself in the length
			if (value instanceof IMemberFunction) {
				return NumberValue.of(functionValue.getParameterCount() - 1);
			}
			return NumberValue.of(functionValue.getParameterCount());
		}
		throw new RuntimeError("Cannot pass %s into len()".formatted(value), function.syntaxPosition, context);
	}

	@FunctionDoc(
		deprecated = "You should use the `throw` keyword",
		name = "throwRuntimeError",
		desc = "This is used to throw a runtime error",
		params = {STRING, "message", "the message of the error"},
		throwMsgs = "the error with the message",
		example = "throwRuntimeError('I'm throwing this error')"
	)
	@Deprecated
	private Value<?> throwRuntimeError(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		throw new RuntimeError(stringValue.value, function.syntaxPosition, context);
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
		example = "callFunctionWithList(fun(n1, n2, n3) {}, [1, 2, 3])"
	)
	@Deprecated
	private Value<?> callFunctionWithList(Context context, BuiltInFunction function) throws CodeError {
		FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 0);
		ArucasList listValue = function.getParameterValueOfType(context, ListValue.class, 1).value;
		return functionValue.call(context, listValue);
	}

	@FunctionDoc(
		name = "runFromString",
		desc = "This is used to evaluate a string as a script",
		params = {STRING, "string", "the string to evaluate"},
		returns = {ANY, "the return value of the script"},
		example = "runFromString('return 1;')"
	)
	private Value<?> runFromString(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		return Run.run(context.createBranch(), "string-run", stringValue.value);
	}
}
