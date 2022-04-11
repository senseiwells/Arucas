package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.extensions.util.JavaValue;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ReflectionUtils;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasMethodHandle;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class JavaFunction extends FunctionValue {
	private final Object callingObject;
	private final MethodHandle handle;

	private JavaFunction(String functionName, Object callingObject, MethodHandle handle, ISyntax syntaxPosition, int arguments) {
		super("$" + functionName, syntaxPosition, Collections.nCopies(arguments, ""), null);
		this.callingObject = callingObject;
		this.handle = handle;
	}

	public static JavaFunction of(Method method, Object callingObject, ISyntax syntaxPosition) {
		if (method != null) {
			MethodHandle handle = ReflectionUtils.getMethodHandle(method);
			return handle == null ? null : new JavaFunction(method.getName(), callingObject, handle, syntaxPosition, method.getParameterCount());
		}
		return null;
	}

	@Override
	protected void populateArguments(Context context, List<Value<?>> arguments, List<String> argumentNames) { }

	@Override
	protected Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError, ThrowValue {
		boolean isStatic = this.callingObject == null;
		Object[] parameters = new Object[this.getParameterCount() + (isStatic ? 0 : 1)];
		for (int i = 0; i < arguments.size(); i++) {
			parameters[i + (isStatic ? 0 : 1)] = arguments.get(i).asJavaValue();
		}
		if (!isStatic) {
			parameters[0] = this.callingObject;
		}

		return ArucasMethodHandle.invokeMethodHandle(() -> {
			return JavaValue.of(this.handle.invokeWithArguments(parameters));
		}, this.syntaxPosition, context);
	}
}
