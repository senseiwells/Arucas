package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.wrappers.ArucasMethodHandle;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassValue;
import me.senseiwells.arucas.values.classes.ArucasWrapperClassValue;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class WrapperClassMemberFunction extends ClassMemberFunction {
	private final IArucasWrappedClass classValue;
	private final ArucasMethodHandle methodHandle;
	private final boolean isStatic;
	private final int parameters;
	
	private WrapperClassMemberFunction(ArucasWrapperClassValue thisValue, IArucasWrappedClass classValue, String name, int parameters, boolean isStatic, ArucasMethodHandle methodHandle) {
		super(thisValue, name, createParameters(parameters), ISyntax.empty());
		this.classValue = classValue;
		this.methodHandle = methodHandle;
		this.isStatic = isStatic;
		this.parameters = parameters;
	}

	public WrapperClassMemberFunction(String name, int parameters, boolean isStatic, ArucasMethodHandle methodHandle) {
		this(null, null, name, parameters, isStatic, methodHandle);
	}
	
	private static List<String> createParameters(int count) {
		return Collections.nCopies(count, "");
	}
	
	@Deprecated
	public WrapperClassMemberFunction copy(ArucasClassValue value) {
		throw new UnsupportedOperationException();
	}
	
	public WrapperClassMemberFunction copy(ArucasWrapperClassValue thisValue, IArucasWrappedClass wrappedClass) {
		return new WrapperClassMemberFunction(thisValue, wrappedClass, this.getName(), this.parameters, this.isStatic, this.methodHandle);
	}
	
	@Override
	protected Value<?> callOverride(Context context, List<Value<?>> arguments, boolean returnable) throws CodeError {
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

		if (returnable) {
			return this.methodHandle.call(args, this.thisValue, this.syntaxPosition, context);
		}
		this.methodHandle.call(args, this.thisValue, this.syntaxPosition, context);
		return NullValue.NULL;
	}
	
	@Override
	public String getAsString(Context context) throws CodeError {
		return "<class " + this.thisValue.getName() + "::" + this.getName() + "@" + Integer.toHexString(Objects.hashCode(this)) + ">";
	}

	@Override
	public void setCallingMember(Supplier<Value<?>> supplier) { }
}
