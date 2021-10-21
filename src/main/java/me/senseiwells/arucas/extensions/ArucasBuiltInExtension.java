package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.ContextBuilder;
import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ErrorRuntime;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArucasBuiltInExtension implements IArucasExtension {
	
	@Override
	public String getName() {
		return "BuiltInExtension";
	}
	
	@Override
	public Set<BuiltInFunction> getDefinedFunctions() {
		return Set.of(
			new BuiltInFunction("run", "path", function -> {
				StringValue stringValue = function.getValueForType(StringValue.class, 0, null);
				String filePath = stringValue.value;
				try {
					String fileContent = Files.readString(Path.of(filePath));
					Run.run(function.context, filePath, fileContent);
				}
				catch (IOException | InvalidPathException e) {
					throw new ErrorRuntime("Failed to execute script '" + filePath + "' \n" + e, function.startPos, function.endPos, function.context);
				}
				return new NullValue();
			}),
			
			new BuiltInFunction("stop", function -> {
				throw new ThrowStop();
			}),
			
			new BuiltInFunction("debug", "boolean", function -> {
				function.context.setDebug(function.getValueForType(BooleanValue.class, 0, null).value);
				return new NullValue();
			}),
			
			new BuiltInFunction("print", "printValue", function -> {
				System.out.println(function.getValueFromTable(function.argumentNames.get(0)));
				return new NullValue();
			}),
			
			new BuiltInFunction("sleep", "milliseconds", function -> {
				NumberValue numberValue = function.getValueForType(NumberValue.class, 0, null);
				try {
					Thread.sleep(numberValue.value.longValue());
				}
				catch (InterruptedException e) {
					throw new CodeError(
						CodeError.ErrorType.RUNTIME_ERROR,
						"An error occurred while trying to call 'sleep()'",
						function.startPos,
						function.endPos
					);
				}
				return new NullValue();
			}),
			
			new BuiltInFunction("schedule", List.of("milliseconds", "function"), function -> {
				NumberValue numberValue = function.getValueForType(NumberValue.class, 0, null);
				FunctionValue functionValue = function.getValueForType(FunctionValue.class, 1, null);
				Thread thread = new Thread(() -> {
					try {
						Thread.sleep(numberValue.value.longValue());
						functionValue.call(function.context, null);
					}
					catch (InterruptedException | CodeError | ThrowValue e) {
						if (!(e instanceof ThrowStop))
							System.out.println("WARN: An error was caught in schedule() call, check that you are passing in a valid function");
					}
				}, "Schedule Thread");
				thread.setDaemon(true);
				thread.start();
				return new NullValue();
			}),
			
			new BuiltInFunction("random", "bound", function -> {
				NumberValue numValue = function.getValueForType(NumberValue.class, 0, null);
				return new NumberValue(Math.random() * numValue.value);
			}),
			
			new BuiltInFunction("round", "number", function -> {
				NumberValue numValue = function.getValueForType(NumberValue.class, 0, null);
				return new NumberValue(Math.round(numValue.value));
			}),
			
			new BuiltInFunction("roundUp", "number", function -> {
				NumberValue numValue = function.getValueForType(NumberValue.class, 0, null);
				return new NumberValue(Math.ceil(numValue.value));
			}),
			
			new BuiltInFunction("roundDown", "number", function -> {
				NumberValue numValue = function.getValueForType(NumberValue.class, 0, null);
				return new NumberValue(Math.floor(numValue.value));
			}),
			
			new BuiltInFunction("modulus", List.of("number1", "number2"), function -> {
				NumberValue numberValue1 = function.getValueForType(NumberValue.class, 0, null);
				NumberValue numberValue2 = function.getValueForType(NumberValue.class, 1, null);
				return new NumberValue(numberValue1.value % numberValue2.value);
			}),
			
			new BuiltInFunction("len", "value", function -> {
				Value<?> value = function.getValueFromTable(function.argumentNames.get(0));
				if (value instanceof ListValue) {
					ListValue listValue = function.getValueForType(ListValue.class, 0, null);
					return new NumberValue(listValue.value.size());
				}
				if (value instanceof StringValue) {
					StringValue stringValue = function.getValueForType(StringValue.class, 0, null);
					return new NumberValue(stringValue.value.length());
				}
				throw new ErrorRuntime("Cannot pass " + value.toString() + " into len()", function.startPos, function.endPos, function.context);
			}),
			
			new BuiltInFunction("stringToList", "string", function -> {
				StringValue stringValue = function.getValueForType(StringValue.class, 0, null);
				List<Value<?>> stringList = new ArrayList<>();
				for (char c : stringValue.value.toCharArray()) {
					stringList.add(new StringValue(String.valueOf(c)));
				}
				return new ListValue(stringList);
			}),
			
			new BuiltInFunction("stringOf", "value", function -> {
				Value<?> value = function.getValueFromTable(function.argumentNames.get(0));
				return new StringValue(value.toString());
			}),
			
			new BuiltInFunction("numberOf", "value", function -> {
				Value<?> stringValue = function.getValueFromTable(function.argumentNames.get(0));
				try {
					return new NumberValue(Double.parseDouble(stringValue.toString()));
				}
				catch (NumberFormatException e) {
					// If you throw error then you cannot check whether a string can be converted to a num
					return new NullValue();
				}
			}),
			
			new BuiltInFunction("getTime", (function) -> new StringValue(DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()))),
			
			new BuiltInFunction("isString", "value", function -> function.isType(StringValue.class)),
			new BuiltInFunction("isNumber", "value", function -> function.isType(NumberValue.class)),
			new BuiltInFunction("isBoolean", "value", function -> function.isType(BooleanValue.class)),
			new BuiltInFunction("isFunction", "value", function -> function.isType(FunctionValue.class)),
			new BuiltInFunction("isList", "value", function -> function.isType(ListValue.class))
		);
	}
}
