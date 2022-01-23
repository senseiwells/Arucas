package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassValue;

import java.lang.invoke.MethodHandle;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class WrapperClassMemberFunction extends ClassMemberFunction {
	private final IArucasWrappedClass classValue;
	private final MethodHandle methodHandle;
	private final boolean isStatic;
	private final int parameters;
	
	public WrapperClassMemberFunction(IArucasWrappedClass classValue, String name, int parameters, boolean isStatic, MethodHandle methodHandle) {
		super(null, name, createParameters(parameters), ISyntax.empty());
		this.classValue = classValue;
		this.methodHandle = methodHandle;
		this.isStatic = isStatic;
		this.parameters = parameters;
	}
	
	public WrapperClassMemberFunction(String name, int parameters, boolean isStatic, MethodHandle methodHandle) {
		this(null, name, parameters, isStatic, methodHandle);
	}
	
	private static List<String> createParameters(int count) {
		return Collections.nCopies(count, "");
	}
	
	@Deprecated
	public WrapperClassMemberFunction copy(ArucasClassValue value) {
		throw new UnsupportedOperationException();
	}
	
	public WrapperClassMemberFunction copy(IArucasWrappedClass wrappedClass) {
		return new WrapperClassMemberFunction(wrappedClass, this.getName(), this.parameters, this.isStatic, this.methodHandle);
	}
	
	@Override
	protected Value<?> callOverride(Context context, List<Value<?>> arguments, boolean returnable) throws CodeError {
		Object[] args = new Object[1 + this.parameters];
		int iModifier = 0;
		if (!this.isStatic) {
			args[0] = this.classValue;
			iModifier = 1;
		}
		int parameters = this.parameters - iModifier;
		iModifier++;
		for (int i = 0; i < parameters; i++) {
			args[i + iModifier] = arguments.get(i);
		}
		
		try {
			if (returnable) {
				return (Value<?>) this.methodHandle.invokeWithArguments(args);
			}
			this.methodHandle.invokeWithArguments(args);
			return NullValue.NULL;
		}
		catch (ClassCastException e) {
			throw new RuntimeError(this.formatCastException(e.getMessage()), this.syntaxPosition, context);
		}
		catch (Throwable t) {
			throw new RuntimeError(t.getMessage().strip(), this.syntaxPosition, context);
		}
	}

	private String formatCastException(String message) {
		String[] matches = Pattern.compile("[a-zA-Z]+(?=Value(?!\\.))")
			.matcher(message).results().map(MatchResult::group)
			.toArray(String[]::new);
		if (matches.length != 2) {
			return message;
		}
		return "Invalid parameter types: Expected: %s, Found: %s".formatted(matches[1], matches[0]);
	}
	
	@Override
	public String getAsString(Context context) throws CodeError {
		return "<class " + this.thisValue.getName() + "::" + this.getName() + "@" + Integer.toHexString(Objects.hashCode(this)) + ">";
	}
}
