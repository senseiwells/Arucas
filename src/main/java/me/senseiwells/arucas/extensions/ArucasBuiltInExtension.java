package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasExtension;
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

	/**
	 * Name: <code>run(path)</code> <br>
	 * Description: This is used to run a <code>.arucas</code> file, you can use on script to run other scripts <br>
	 * Parameter - String: as a file path <br>
	 * Returns - Value: any value that the file returns <br>
	 * Throws: Error: <code>"Failed to execute script..."</code> if the file fails to execute <br>
	 * Example: <code>run("/home/user/script.arucas");</code> 
	 */
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

	/**
	 * Name: <code>stop()</code> <br>
	 * Description: This is used to stop a script <br>
	 * Example: <code>stop();</code> 
	 */
	private Value<?> stop(Context context, BuiltInFunction function) throws CodeError {
		throw new ThrowStop();
	}

	/**
	 * Name: <code>sleep(milliseconds)</code> <br>
	 * Description: This pauses your program for a certain amount of milliseconds <br>
	 * Parameter - Number: milliseconds to sleep <br>
	 * Example: <code>sleep(1000);</code> 
	 */
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

	/**
	 * Name: <code>print(printValue)</code> <br>
	 * Description: This prints a value to the console <br>
	 * Parameter - Value: the value to print <br>
	 * Example: <code>print("Hello World");</code> 
	 */
	private Value<?> print(Context context, BuiltInFunction function) throws CodeError {
		context.getOutput().println(function.getParameterValue(context, 0).getAsString(context));
		return NullValue.NULL;
	}

	/**
	 * Name: <code>fullPrint(printValue)</code> <br>
	 * Description: This prints a number of values to the console <br>
	 * Parameters - Arbitrary: any number of values to print <br>
	 * Example: <code>print("Hello World", "This is a test", 123);</code> 
	 */
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

	/**
	 * Name: <code>input(prompt)</code> <br>
	 * Description: This is used to take an input from the user <br>
	 * Parameter - String: the prompt to show the user <br>
	 * Returns - String: the input from the user <br>
	 * Example: <code>input("What is your name?");</code> 
	 */
	private synchronized Value<?> input(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		context.getOutput().println(stringValue.value);
		return StringValue.of(this.scanner.nextLine());
	}

	/**
	 * Name: <code>debug(bool)</code> <br>
	 * Description: This is used to enable or disable debug mode <br>
	 * Parameter - Boolean: true to enable debug mode, false to disable debug mode <br>
	 * Example: <code>debug(true);</code> 
	 */
	private Value<?> debug(Context context, BuiltInFunction function) throws CodeError {
		context.setDebug(function.getParameterValueOfType(context, BooleanValue.class, 0).value);
		return NullValue.NULL;
	}

	/**
	 * Name: <code>experimental(bool)</code> <br>
	 * Description: This is used to enable or disable experimental mode <br>
	 * Parameter - Boolean: true to enable experimental mode, false to disable experimental mode <br>
	 * Example: <code>experimental(true);</code> 
	 */
	private Value<?> experimental(Context context, BuiltInFunction function) throws CodeError {
		context.setExperimental(function.getParameterValueOfType(context, BooleanValue.class, 0).value);
		return NullValue.NULL;
	}

	/**
	 * Name: <code>suppressDeprecated(bool)</code> <br>
	 * Description: This is used to enable or disable suppressing deprecation warnings <br>
	 * Parameter - Boolean: true to enable suppressing deprecation warnings, false to disable suppressing deprecation warnings <br>
	 * Example: <code>suppressDeprecated(true);</code> 
	 */
	private Value<?> suppressDeprecated(Context context, BuiltInFunction function) throws CodeError {
		context.setSuppressDeprecated(function.getParameterValueOfType(context, BooleanValue.class, 0).value);
		return NullValue.NULL;
	}

	/**
	 * Name: <code>isMain()</code> <br>
	 * Description: This is used to check whether the script is the main script <br>
	 * Returns - Boolean: true if the script is the main script, false if it is not <br>
	 * Example: <code>isMain();</code> 
	 */
	private Value<?> isMain(Context context, BuiltInFunction function) {
		return BooleanValue.of(context.isMain());
	}

	/**
	 * Name: <code>getArucasVersion()</code> <br>
	 * Description: This is used to get the version of Arucas that is currently running <br>
	 * Returns - String: the version of Arucas that is currently running <br>
	 * Example: <code>getArucasVersion();</code> 
	 */
	private Value<?> getArucasVersion(Context context, BuiltInFunction function) {
		return StringValue.of(Arucas.VERSION);
	}

	/**
	 * Name: <code>random(bound)</code> <br>
	 * Description: This is used to generate a random integer between 0 and the bound <br>
	 * Parameter - Number: the maximum bound (exclusive) <br>
	 * Returns - Number: the random integer <br>
	 * Example: <code>random(10);</code> 
	 */
	private Value<?> random(Context context, BuiltInFunction function) throws CodeError {
		NumberValue numValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return NumberValue.of(this.random.nextInt(numValue.value.intValue()));
	}

	/**
	 * Name: <code>getTime()</code> <br>
	 * Description: This is used to get the current time formatted with HH:mm:ss in your local time <br>
	 * Returns - String: the current time formatted with HH:mm:ss <br>
	 * Example: <code>getTime();</code> 
	 */
	private Value<?> getTime(Context context, BuiltInFunction function) {
		return StringValue.of(DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()));
	}

	/**
	 * Name: <code>getNanoTime()</code> <br>
	 * Description: This is used to get the current time in nanoseconds <br>
	 * Returns - Number: the current time in nanoseconds <br>
	 * Example: <code>getNanoTime();</code> 
	 */
	private Value<?> getNanoTime(Context context, BuiltInFunction function) {
		return NumberValue.of(System.nanoTime());
	}

	/**
	 * Name: <code>getMilliTime()</code> <br>
	 * Description: This is used to get the current time in milliseconds <br>
	 * Returns - Number: the current time in milliseconds <br>
	 * Example: <code>getMilliTime();</code> 
	 */
	private Value<?> getMilliTime(Context context, BuiltInFunction function) {
		return NumberValue.of(System.currentTimeMillis());
	}

	/**
	 * Name: <code>getUnixTime()</code> <br>
	 * Description: This is used to get the current time in seconds since the Unix epoch <br>
	 * Returns - Number: the current time in seconds since the Unix epoch <br>
	 * Example: <code>getUnixTime();</code> 
	 */
	private Value<?> getUnixTime(Context context, BuiltInFunction function) {
		return NumberValue.of(System.currentTimeMillis() / 1000F);
	}

	/**
	 * Name: <code>getDate()</code> <br>
	 * Description: This is used to get the current date formatted with dd/MM/yyyy in your local time <br>
	 * Returns - String: the current date formatted with dd/MM/yyyy <br>
	 * Example: <code>getDate();</code> 
	 */
	private Value<?> getDate(Context context, BuiltInFunction function) {
		return StringValue.of(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now()));
	}

	/**
	 * Name: <code>len(collection)</code> <br>
	 * Description: This is used to get the length of a collection or string <br>
	 * Parameter - String/Collection/Function: the collection or string <br>
	 * Throws - Error: <code>"Cannot pass ... into len()"</code> if the parameter is not a collection or string <br>
	 * Example: <code>len("Hello World");</code> 
	 */
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

	/**
	 * Deprecated: You should use the <code>throw</code> keyword <br>
	 * Name: <code>throwRuntimeError(message)</code> <br>
	 * Description: This is used to throw a runtime error <br>
	 * Parameter - String: the message of the error <br>
	 * Throws - Error: the error with the message <br>
	 * Example: <code>throwRuntimeError("I'm throwing this error");</code> 
	 */
	@Deprecated
	private Value<?> throwRuntimeError(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		throw new RuntimeError(stringValue.value, function.syntaxPosition, context);
	}

	/**
	 * Deprecated: You should use Function class <code>Function.callWithList(fun() {}, [])</code> <br>
	 * Name: <code>callFunctionWithList(function, list)</code> <br>
	 * Description: This is used to call a function with a list of arguments <br>
	 * Parameters - Function, List: the function and the list of arguments <br>
	 * Returns - Value: the return value of the function <br>
	 * Example: <code>callFunctionWithList(fun(n1, n2, n3) {}, [1, 2, 3]);</code>
	 */
	@Deprecated
	private Value<?> callFunctionWithList(Context context, BuiltInFunction function) throws CodeError {
		FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 0);
		ArucasList listValue = function.getParameterValueOfType(context, ListValue.class, 1).value;
		return functionValue.call(context, listValue);
	}

	/**
	 * Name: <code>runFromString(string)</code> <br>
	 * Description: This is used to evaluate a string as a script <br>
	 * Parameter - String: the string to evaluate <br>
	 * Returns - Value: the return value of the script <br>
	 * Example: <code>runFromString("return 1;");</code>
	 */
	private Value<?> runFromString(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		return Run.run(context.createBranch(), "string-run", stringValue.value);
	}
}
