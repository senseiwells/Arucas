package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasMethodHandle;
import me.senseiwells.arucas.values.classes.WrapperClassDefinition;
import me.senseiwells.arucas.values.classes.WrapperClassValue;

import java.util.List;
import java.util.Objects;

public class WrapperMemberFunction extends FunctionValue implements IMemberFunction {
	private final WrapperClassDefinition definition;
	private final ArucasMethodHandle methodHandle;
	private final boolean isStatic;

	private WrapperMemberFunction(WrapperClassDefinition definition, String name, int parameters, ArucasMethodHandle methodHandle, boolean isStatic) {
		super(name, ISyntax.emptyOf("Wrapper/" + name), parameters, null);
		this.definition = definition;
		this.methodHandle = methodHandle;
		this.isStatic = isStatic;
	}

	public static WrapperMemberFunction of(WrapperClassDefinition definition, String name, int parameters, ArucasMethodHandle handle, boolean isStatic) {
		return new WrapperMemberFunction(definition, name, parameters, handle, isStatic);
	}

	@Override
	protected Value execute(Context context, List<Value> arguments) throws CodeError {
		if (this.isStatic) {
			Object[] args = new Object[1 + this.getCount()];
			args[0] = context;
			for (int i = 0; i < this.getCount(); i++) {
				args[i + 1] = arguments.get(i);
			}
			return this.methodHandle.call(args, null, this.getPosition(), context);
		}

		if (arguments.isEmpty() || !(arguments.get(0) instanceof WrapperClassValue wrapperValue)) {
			throw new RuntimeException("'this' was not passed into the function");
		}

		Object[] args = new Object[1 + this.getCount()];
		args[0] = wrapperValue.getWrapper();
		args[1] = context;
		for (int i = 0; i < this.getCount() - 1; i++) {
			args[i + 2] = arguments.get(i);
		}

		return this.methodHandle.call(args, wrapperValue, this.getPosition(), context);
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return "<class " + this.definition.getName() + "::" + this.getName() + "@" + Integer.toHexString(Objects.hashCode(this)) + ">";
	}

	@Override
	public FunctionValue getDelegate(Value thisValue) {
		if (thisValue instanceof WrapperClassValue wrapperValue) {
			return new Delegate(wrapperValue, this);
		}
		return null;
	}

	private static class Delegate extends WrapperMemberFunction {
		private final WrapperClassValue wrapperValue;

		private Delegate(WrapperClassValue wrapperValue, WrapperMemberFunction function) {
			super(function.definition, function.getName(), function.getCount(), function.methodHandle, function.isStatic);
			this.wrapperValue = wrapperValue;
		}

		@Override
		protected Value execute(Context context, List<Value> arguments) throws CodeError {
			arguments.add(0, this.wrapperValue);
			return super.execute(context, arguments);
		}
	}
}
