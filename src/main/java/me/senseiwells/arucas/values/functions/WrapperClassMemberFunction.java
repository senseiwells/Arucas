package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasMethodHandle;
import me.senseiwells.arucas.values.classes.WrapperClassValue;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class WrapperClassMemberFunction extends UserDefinedFunction implements IMemberFunction {
	private final IArucasWrappedClass classValue;
	private final ArucasMethodHandle methodHandle;
	private final boolean isStatic;
	private final int parameters;
	private final WrapperClassValue thisValue;

	private WrapperClassMemberFunction(WrapperClassValue thisValue, IArucasWrappedClass classValue, String name, int parameters, boolean isStatic, ArucasMethodHandle methodHandle) {
		super(name, createParameters(parameters), ISyntax.empty());
		this.classValue = classValue;
		this.methodHandle = methodHandle;
		this.isStatic = isStatic;
		this.parameters = parameters;
		this.thisValue = thisValue;
	}

	public WrapperClassMemberFunction(String name, int parameters, boolean isStatic, ArucasMethodHandle methodHandle) {
		this(null, null, name, parameters, isStatic, methodHandle);
	}

	private static List<String> createParameters(int count) {
		return Collections.nCopies(count, "");
	}

	public WrapperClassMemberFunction copy(WrapperClassValue thisValue, IArucasWrappedClass wrappedClass) {
		return new WrapperClassMemberFunction(thisValue, wrappedClass, this.getName(), this.parameters, this.isStatic, this.methodHandle);
	}

	@Override
	public Value call(Context context, List<Value> arguments, boolean returnable) throws CodeError {
		context.pushFunctionScope(this.getPosition());

		Object[] args = new Object[1 + this.parameters];
		int iModifier = 0;
		if (!this.isStatic) {
			args[0] = this.classValue;
			iModifier = 1;
		}
		args[iModifier] = context;
		int parameters = this.parameters - iModifier;
		iModifier++;
		for (int i = 0; i < parameters; i++) {
			args[i + iModifier] = arguments.get(i);
		}

		Value returnValue;
		if (returnable) {
			returnValue = this.methodHandle.call(args, this.thisValue, this.getPosition(), context);
		}
		else {
			this.methodHandle.call(args, this.thisValue, this.getPosition(), context);
			returnValue = NullValue.NULL;
		}

		context.popScope();
		return returnValue;
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return "<class " + this.thisValue.getName() + "::" + this.getName() + "@" + Integer.toHexString(Objects.hashCode(this)) + ">";
	}

	@Override
	public FunctionValue setThisAndGet(Value thisValue) {
		return this;
	}
}
