package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassValue;

import java.lang.invoke.MethodHandle;
import java.util.Collections;
import java.util.List;

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
	
	public WrapperClassMemberFunction copy(ArucasClassValue value) {
		// TODO: Figure out a better solution to get the parameters
		//       If we can get direct access to the parameters buffer
		//       we could skip some branches and context lookups. This
		//       would make wrapper classes much faster than other types
		//       of calls. The only problem is that we need to make
		//       wrapper classes a subclass of `ArucasClassValue` for
		//       them to be completely unique.
		//
		// TODO: To fix this we need to make a more robust system for
		//       class creation.
		//
		return new WrapperClassMemberFunction(this.classValue, this.getName(), this.parameters, this.isStatic, this.methodHandle);
	}
	
	@Override
	protected Value<?> callOverride(Context context, List<Value<?>> arguments, boolean returnable) throws CodeError {
		Object[] args = new Object[1 + this.parameters];
		if (this.isStatic) {
			args[0] = context;
			for (int i = 0; i < this.parameters; i++) {
				args[i + 1] = arguments.get(i);
			}
		}
		else {
			args[0] = this.classValue;
			args[1] = context;
			for (int i = 0; i < this.parameters - 1; i++) {
				args[i + 2] = arguments.get(i);
			}
		}
		
		try {
			return (Value<?>)this.methodHandle.invokeWithArguments(args);
		}
		catch (ClassCastException e) {
			// TODO: Generate a more descriptive error message for the invalid parameter value
			throw new RuntimeError(e.getMessage(), this.syntaxPosition, context);
		}
		catch (Throwable t) {
			throw new RuntimeError(t.getMessage(), this.syntaxPosition, context);
		}
	}
	
	@Override
	public String getStringValue(Context context) throws CodeError {
		return "<class %s::%s@%x>".formatted(this.thisValue.getName(), this.getName(), this.hashCode());
	}
}
