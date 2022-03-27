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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

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
		new BuiltInFunction("getArucasVersion", ((context, function) -> StringValue.of(Arucas.VERSION))),
		new BuiltInFunction("random", "bound", this::random),
		new BuiltInFunction("getTime", (context, function) -> StringValue.of(DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()))),
		new BuiltInFunction("getNanoTime", (context, function) -> NumberValue.of(System.nanoTime())),
		new BuiltInFunction("getMilliTime", (context, function) -> NumberValue.of(System.currentTimeMillis())),
		new BuiltInFunction("getUnixTime", (context, function) -> NumberValue.of(System.currentTimeMillis() / 1000.0F)),
		new BuiltInFunction("len", "value", this::len),
		new BuiltInFunction("throwRuntimeError", "message", this::throwRuntimeError, "Use 'throw new Error()'"),
		new BuiltInFunction("callFunctionWithList", List.of("function", "argList"), this::callFunctionWithList, "Use 'Function.callDelegateWithList()'"),
		new BuiltInFunction("runFromString", "string", this::runFromString)
	);

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

	private Value<?> stop(Context context, BuiltInFunction function) throws CodeError {
		throw new ThrowStop();
	}

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

	private Value<?> print(Context context, BuiltInFunction function) throws CodeError {
		context.getOutput().println(function.getParameterValue(context, 0).getAsString(context));
		return NullValue.NULL;
	}

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

	private synchronized Value<?> input(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		context.getOutput().println(stringValue.value);
		return StringValue.of(this.scanner.nextLine());
	}

	private Value<?> debug(Context context, BuiltInFunction function) throws CodeError {
		context.setDebug(function.getParameterValueOfType(context, BooleanValue.class, 0).value);
		return NullValue.NULL;
	}

	private Value<?> experimental(Context context, BuiltInFunction function) throws CodeError {
		context.setExperimental(function.getParameterValueOfType(context, BooleanValue.class, 0).value);
		return NullValue.NULL;
	}

	private Value<?> suppressDeprecated(Context context, BuiltInFunction function) throws CodeError {
		context.setSuppressDeprecated(function.getParameterValueOfType(context, BooleanValue.class, 0).value);
		return NullValue.NULL;
	}

	private Value<?> isMain(Context context, BuiltInFunction function) {
		return BooleanValue.of(context.isMain());
	}

	private Value<?> random(Context context, BuiltInFunction function) throws CodeError {
		NumberValue numValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return NumberValue.of(this.random.nextInt(numValue.value.intValue()));
	}

	private Value<?> len(Context context, BuiltInFunction function) throws CodeError {
		Value<?> value = function.getParameterValue(context, 0);
		if (value instanceof StringValue stringValue) {
			return NumberValue.of(stringValue.value.length());
		}
		if (value.value instanceof IArucasCollection collection) {
			return NumberValue.of(collection.size());
		}
		throw new RuntimeError("Cannot pass %s into len()".formatted(value), function.syntaxPosition, context);
	}

	private Value<?> throwRuntimeError(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		throw new RuntimeError(stringValue.value, function.syntaxPosition, context);
	}

	private Value<?> callFunctionWithList(Context context, BuiltInFunction function) throws CodeError {
		FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 0);
		ArucasList listValue = function.getParameterValueOfType(context, ListValue.class, 1).value;
		return functionValue.call(context, listValue);
	}

	private Value<?> runFromString(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		return Run.run(context.createBranch(), "string-run", stringValue.value);
	}
}
