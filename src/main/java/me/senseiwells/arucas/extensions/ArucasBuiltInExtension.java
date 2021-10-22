package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ErrorRuntime;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
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
			new BuiltInFunction("run", "path", (context, function) -> {
				StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
				String filePath = stringValue.value;
				try {
					Context childContext = context.createChildContext(filePath);
					
					String fileContent = Files.readString(Path.of(filePath));
					Run.run(childContext, filePath, fileContent);
				}
				catch (IOException | InvalidPathException e) {
					throw new ErrorRuntime("Failed to execute script '" + filePath + "' \n" + e, function.startPos, function.endPos, context);
				}
				return new NullValue();
			}),
			
			new BuiltInFunction("stop", (context, function) -> {
				throw new ThrowStop();
			}),
			
			new BuiltInFunction("debug", "boolean", (context, function) -> {
				context.setDebug(function.getParameterValueOfType(context, BooleanValue.class, 0).value);
				return new NullValue();
			}),
			
			new BuiltInFunction("print", "printValue", (context, function) -> {
				System.out.println(function.getParameterValue(context, 0));
				return new NullValue();
			}),
			
			new BuiltInFunction("sleep", "milliseconds", (context, function) -> {
				NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
				try {
					Thread.sleep(numberValue.value.longValue());
				}
				catch (InterruptedException e) {
					throw new ErrorRuntime(
						"An error occurred while trying to call 'sleep()'",
						function.startPos,
						function.endPos,
						context
					);
				}
				return new NullValue();
			}),
			
            /*
            // Implement when (I/O) is added to the language
			new BuiltInFunction("schedule", List.of("milliseconds", "function"), (context, function) -> {
				NumberValue numberValue = function.getParameterValueOfType(context, NumberValue.class, 0);
				FunctionValue functionValue = function.getParameterValueOfType(context, FunctionValue.class, 1);
				
				Context branchContext = context.createBranch();
				Thread thread = new Thread(() -> {
					try {
						Thread.sleep(numberValue.value.longValue());
						functionValue.call(branchContext, List.of());
					}
					catch (InterruptedException | ThrowValue e) {
						System.out.println("WARN: An error was caught in schedule() call, check that you are passing in a valid function");
					}
					catch (CodeError e) {
						System.out.println(e.toString(context));
					}
				}, "Schedule Thread");
				thread.setDaemon(true);
				thread.start();
				return new NullValue();
			}),
			*/
   
			new BuiltInFunction("random", "bound", (context, function) -> {
				NumberValue numValue = function.getParameterValueOfType(context, NumberValue.class, 0);
				return new NumberValue(Math.random() * numValue.value);
			}),
			
			new BuiltInFunction("round", "number", (context, function) -> {
				NumberValue numValue = function.getParameterValueOfType(context, NumberValue.class, 0);
				return new NumberValue(Math.round(numValue.value));
			}),
			
			new BuiltInFunction("roundUp", "number", (context, function) -> {
				NumberValue numValue = function.getParameterValueOfType(context, NumberValue.class, 0);
				return new NumberValue(Math.ceil(numValue.value));
			}),
			
			new BuiltInFunction("roundDown", "number", (context, function) -> {
				NumberValue numValue = function.getParameterValueOfType(context, NumberValue.class, 0);
				return new NumberValue(Math.floor(numValue.value));
			}),
			
			new BuiltInFunction("modulus", List.of("number1", "number2"), (context, function) -> {
				NumberValue numberValue1 = function.getParameterValueOfType(context, NumberValue.class, 0);
				NumberValue numberValue2 = function.getParameterValueOfType(context, NumberValue.class, 1);
				return new NumberValue(numberValue1.value % numberValue2.value);
			}),
			
			new BuiltInFunction("len", "value", (context, function) -> {
				Value<?> value = function.getParameterValue(context, 0);
				if (value instanceof ListValue) {
					ListValue listValue = function.getParameterValueOfType(context, ListValue.class, 0);
					return new NumberValue(listValue.value.size());
				}
				if (value instanceof StringValue) {
					StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
					return new NumberValue(stringValue.value.length());
				}
				throw new ErrorRuntime("Cannot pass " + value.toString() + " into len()", function.startPos, function.endPos, context);
			}),
			
			new BuiltInFunction("stringToList", "string", (context, function) -> {
				StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
				List<Value<?>> stringList = new ArrayList<>();
				for (char c : stringValue.value.toCharArray()) {
					stringList.add(new StringValue(String.valueOf(c)));
				}
				return new ListValue(stringList);
			}),
			
			new BuiltInFunction("stringOf", "value", (context, function) -> {
				Value<?> value = function.getParameterValue(context, 0);
				return new StringValue(value.toString());
			}),
			
			new BuiltInFunction("numberOf", "value", (context, function) -> {
				Value<?> stringValue = function.getParameterValue(context, 0);
				try {
					return new NumberValue(Double.parseDouble(stringValue.toString()));
				}
				catch (NumberFormatException e) {
					// If you throw error then you cannot check whether a string can be converted to a num
					return new NullValue();
				}
			}),
			
			new BuiltInFunction("getTime", (context, function) -> new StringValue(DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()))),
			
			new BuiltInFunction("isString", "value", (context, function) -> function.isType(context, StringValue.class)),
			new BuiltInFunction("isNumber", "value", (context, function) -> function.isType(context, NumberValue.class)),
			new BuiltInFunction("isBoolean", "value", (context, function) -> function.isType(context, BooleanValue.class)),
			new BuiltInFunction("isFunction", "value", (context, function) -> function.isType(context, FunctionValue.class)),
			new BuiltInFunction("isList", "value", (context, function) -> function.isType(context, ListValue.class))
		);
	}
}
