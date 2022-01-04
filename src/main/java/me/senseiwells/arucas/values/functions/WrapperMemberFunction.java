//package me.senseiwells.arucas.values.functions;
//
//import me.senseiwells.arucas.api.ISyntax;
//import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
//import me.senseiwells.arucas.throwables.CodeError;
//import me.senseiwells.arucas.throwables.RuntimeError;
//import me.senseiwells.arucas.throwables.ThrowValue;
//import me.senseiwells.arucas.utils.Context;
//import me.senseiwells.arucas.values.Value;
//import me.senseiwells.arucas.values.classes.ArucasClassValue;
//
//import java.lang.invoke.MethodHandle;
//import java.util.ArrayList;
//import java.util.List;
//
//public class WrapperMemberFunction extends ClassMemberFunction {
//	private final IArucasWrappedClass wrapperValue;
//	private final MethodHandle methodHandle;
//
//	public WrapperMemberFunction(IArucasWrappedClass value, MethodHandle methodHandle, String name, int parameters) {
//		super(name, createParameters(parameters), ISyntax.empty());
//		this.wrapperValue = value;
//		this.methodHandle = methodHandle;
//	}
//
//	private static List<String> createParameters(int count) {
//		List<String> list = new ArrayList<>(count);
//		for (int i = 0; i < count; i++) {
//			list.add(Integer.toString(i));
//		}
//
//		return list;
//	}
//
//	public WrapperMemberFunction copy(ArucasClassValue value) {
//		// TODO: Allow for correct member values
//		return this;
//	}
//
//	@Override
//	public Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError, ThrowValue {
//		// This value is always added as the first parameter
//		Object[] args = new Object[2 + arguments.size()];
//		args[0] = this.wrapperValue;
//		args[1] = context;
//		for (int i = 0; i < arguments.size(); i++) {
//			args[i + 2] = arguments.get(i);
//		}
//
//		try {
//			throw new ThrowValue.Return((Value<?>)methodHandle.invokeWithArguments(args));
//		}
//		catch (CodeError | ThrowValue t) {
//			throw t;
//		}
//		catch (Throwable t) {
//			throw new RuntimeError(t.getMessage(), this.syntaxPosition, context);
//		}
//	}
//
//	@Override
//	public String getStringValue(Context context) throws CodeError {
//		return "<class %s::%s@%x>".formatted(this.thisValue.getName(), this.getName(), this.hashCode());
//	}
//}
