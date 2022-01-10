package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.utils.*;
import me.senseiwells.arucas.utils.impl.ArucasValueListCustom;
import me.senseiwells.arucas.utils.impl.ArucasValueThread;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
		new BuiltInFunction("input", "prompt", this::input),
		new BuiltInFunction("debug", "boolean", this::debug),
		new BuiltInFunction("suppressDeprecated", "boolean", this::suppressDeprecated),
		new BuiltInFunction("random", "bound", this::random),
		new BuiltInFunction("getTime", (context, function) -> StringValue.of(DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()))),
		new BuiltInFunction("getDirectory", (context, function) -> StringValue.of(System.getProperty("user.dir"))),
		new BuiltInFunction("len", "value", this::len),
		new BuiltInFunction("runThreaded", List.of("function", "parameters"), this::runThreaded$2, "Used 'Thread.runThreaded(name, function)'"),
		new BuiltInFunction("readFile", "path", this::readFile),
		new BuiltInFunction("writeFile", List.of("path", "string"), this::writeFile),
		new BuiltInFunction("createDirectory", "path", this::createDirectory),
		new BuiltInFunction("doesFileExist", "path", this::doesFileExist),
		new BuiltInFunction("getFileList", "path", this::getFileList),
		new BuiltInFunction("throwRuntimeError", "message", this::throwRuntimeError),
		new BuiltInFunction("callFunctionWithList", List.of("function", "argList"), this::callFunctionWithList),
		new BuiltInFunction("runFromString", "string", this::runFromString),

		// Math functions
		new BuiltInFunction("sin", "value", this::sin, "Use 'Math.sin(num)'"),
		new BuiltInFunction("cos", "value", this::cos, "Use 'Math.cos(num)'"),
		new BuiltInFunction("tan", "value", this::tan, "Use 'Math.tan(num)'"),
		new BuiltInFunction("arcsin", "value", this::arcsin, "Use 'Math.arcsin(num)'"),
		new BuiltInFunction("arccos", "value", this::arccos, "Use 'Math.arccos(num)'"),
		new BuiltInFunction("arctan", "value", this::arctan, "Use 'Math.arctan(num)'"),
		new BuiltInFunction("cosec", "value", this::cosec, "Use 'Math.cosec(num)'"),
		new BuiltInFunction("sec", "value", this::sec, "Use 'Math.sec(num)'"),
		new BuiltInFunction("cot", "value", this::cot, "Use 'Math.cot(num)'")
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
		context.getOutput().println(function.getParameterValue(context, 0).getStringValue(context));
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

	private Value<?> suppressDeprecated(Context context, BuiltInFunction function) throws CodeError {
		context.setSuppressDeprecated(function.getParameterValueOfType(context, BooleanValue.class, 0).value);
		return NullValue.NULL;
	}

	private Value<?> random(Context context, BuiltInFunction function) throws CodeError {
		NumberValue numValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return NumberValue.of(this.random.nextInt(numValue.value.intValue()));
	}

	private Value<?> len(Context context, BuiltInFunction function) throws CodeError {
		Value<?> value = function.getParameterValue(context, 0);
		if (value instanceof ListValue listValue) {
			return NumberValue.of(listValue.value.size());
		}
		if (value instanceof StringValue stringValue) {
			return NumberValue.of(stringValue.value.length());
		}
		if (value instanceof MapValue mapValue) {
			return NumberValue.of(mapValue.value.size());
		}
		throw new RuntimeError("Cannot pass %s into len()".formatted(value), function.syntaxPosition, context);
	}

	@Deprecated
	private Value<?> runThreaded$2(Context context, BuiltInFunction function) throws CodeError {
		FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 0);
		ArucasValueListCustom list = function.getParameterValueOfType(context, ListValue.class, 1).value;
		ArucasValueThread thread = context.getThreadHandler().runAsyncFunctionInContext(context.createBranch(), (branchContext) -> functionValue.call(branchContext, list));
		return ThreadValue.of(thread);
	}

	private Value<?> readFile(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);

		try {
			String fileContent = Files.readString(Path.of(stringValue.value));
			return StringValue.of(fileContent);
		}
		catch (OutOfMemoryError e) {
			throw new RuntimeError("Out of Memory - The file you are trying to read is too large", function.syntaxPosition, context);
		}
		catch (InvalidPathException e) {
			throw new RuntimeError(e.getMessage(), function.syntaxPosition, context);
		}
		catch (IOException e) {
			throw new RuntimeError(
				"There was an error reading the file: \"%s\"\n%s".formatted(stringValue.value, ExceptionUtils.getStackTrace(e)),
				function.syntaxPosition,
				context
			);
		}
	}

	private Value<?> writeFile(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		StringValue writeValue = function.getParameterValueOfType(context, StringValue.class, 1);
		String filePath = stringValue.value;

		try (PrintWriter printWriter = new PrintWriter(filePath)) {
			printWriter.println(writeValue.value);
			return NullValue.NULL;
		}
		catch (FileNotFoundException e) {
			throw new RuntimeError(
				"There was an error writing the file: \"%s\"\n%s".formatted(stringValue.value, ExceptionUtils.getStackTrace(e)),
				function.syntaxPosition,
				context
			);
		}
	}

	private Value<?> createDirectory(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		try {
			return BooleanValue.of(Path.of(stringValue.value).toFile().mkdirs());
		}
		catch (InvalidPathException e) {
			throw new RuntimeError(e.getMessage(), function.syntaxPosition, context);
		}
	}

	private Value<?> doesFileExist(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		try {
			return BooleanValue.of(Path.of(stringValue.value).toFile().exists());
		}
		catch (InvalidPathException e) {
			throw new RuntimeError(e.getMessage(), function.syntaxPosition, context);
		}
	}

	private Value<?> getFileList(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		try {
			File[] files = Path.of(stringValue.value).toFile().listFiles();
			if (files == null) {
				throw new RuntimeError("Could not find any files", function.syntaxPosition, context);
			}
			ArucasValueListCustom fileList = new ArucasValueListCustom();
			for (File file : files) {
				fileList.add(StringValue.of(file.getName()));
			}
			return new ListValue(fileList);
		}
		catch (InvalidPathException e) {
			throw new RuntimeError(e.getMessage(), function.syntaxPosition, context);
		}
	}

	private Value<?> throwRuntimeError(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		throw new RuntimeError(stringValue.value, function.syntaxPosition, context);
	}

	private Value<?> callFunctionWithList(Context context, BuiltInFunction function) throws CodeError {
		FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 0);
		ArucasValueListCustom listValue = function.getParameterValueOfType(context, ListValue.class, 1).value;
		return functionValue.call(context, listValue);
	}

	private Value<?> runFromString(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
		return Run.run(context, "string-run", stringValue.value);
	}

	@Deprecated
	private Value<?> sin(Context context, BuiltInFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return NumberValue.of(Math.sin(numberValue.value));
	}

	@Deprecated
	private Value<?> cos(Context context, BuiltInFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return NumberValue.of(Math.cos(numberValue.value));
	}

	@Deprecated
	private Value<?> tan(Context context, BuiltInFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return NumberValue.of(Math.tan(numberValue.value));
	}

	@Deprecated
	private Value<?> arcsin(Context context, BuiltInFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return NumberValue.of(Math.asin(numberValue.value));
	}

	@Deprecated
	private Value<?> arccos(Context context, BuiltInFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return NumberValue.of(Math.acos(numberValue.value));
	}

	@Deprecated
	private Value<?> arctan(Context context, BuiltInFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return NumberValue.of(Math.atan(numberValue.value));
	}

	@Deprecated
	private Value<?> cosec(Context context, BuiltInFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return NumberValue.of(1 / Math.sin(numberValue.value));
	}

	@Deprecated
	private Value<?> sec(Context context, BuiltInFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return NumberValue.of(1 / Math.cos(numberValue.value));
	}

	@Deprecated
	private Value<?> cot(Context context, BuiltInFunction function) throws CodeError {
		NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
		return NumberValue.of(1 / Math.tan(numberValue.value));
	}
}
