package me.senseiwells.arucas.extensions;

import me.senseiwells.arucas.api.IArucasExtension;
import me.senseiwells.arucas.core.Run;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowStop;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.AbstractBuiltInFunction;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ArucasBuiltInExtension implements IArucasExtension {
	
	@Override
	public String getName() {
		return "BuiltInExtension";
	}
	
	@Override
	public Set<? extends AbstractBuiltInFunction<?>> getDefinedFunctions() {
		return this.builtInFunctions;
	}

	private final Set<? extends AbstractBuiltInFunction<?>> builtInFunctions = Set.of(
		new BuiltInFunction("run", "path", (context, function) -> {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			String filePath = stringValue.value;
			try {
				Context childContext = context.createChildContext(filePath);

				String fileContent = Files.readString(Path.of(filePath));
				return Run.run(childContext, filePath, fileContent);
			}
			catch (IOException | InvalidPathException e) {
				throw new RuntimeError("Failed to execute script '%s' \n%s".formatted(filePath, e), function.startPos, function.endPos, context);
			}
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
				throw new CodeError(
						CodeError.ErrorType.INTERRUPTED_ERROR,
						"",
						function.startPos,
						function.endPos
				);
			}
			return new NullValue();
		}),

		new BuiltInFunction("random", "bound", (context, function) -> {
			NumberValue numValue = function.getParameterValueOfType(context, NumberValue.class, 0);
			return new NumberValue(new Random().nextInt(numValue.value.intValue()));
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
			if (value instanceof MapValue) {
				MapValue mapValue = function.getParameterValueOfType(context, MapValue.class, 0);
				return new NumberValue(mapValue.value.size());
			}
			throw new RuntimeError("Cannot pass %s into len()".formatted(value), function.startPos, function.endPos, context);
		}),

		new BuiltInFunction("throwRuntimeError", "message", (context, function) -> {
			StringValue stringValue = function.getParameterValueOfType(context, StringValue.class, 0);
			throw new RuntimeError(stringValue.value, function.startPos, function.endPos, context);
		}),

		new BuiltInFunction("getTime", (context, function) -> new StringValue(DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalTime.now()))),

		new MemberFunction("isString", (context, function) -> function.isType(context, StringValue.class)),
		new MemberFunction("isNumber", (context, function) -> function.isType(context, NumberValue.class)),
		new MemberFunction("isBoolean", (context, function) -> function.isType(context, BooleanValue.class)),
		new MemberFunction("isFunction", (context, function) -> function.isType(context, FunctionValue.class)),
		new MemberFunction("isList", (context, function) -> function.isType(context, ListValue.class)),
		new MemberFunction("isMap", ((context, function) -> function.isType(context, MapValue.class))),

		new MemberFunction("toString", (context, function) -> {
			Value<?> value = function.getParameterValue(context, 0);
			return new StringValue(value.toString());
		})
	);
}
