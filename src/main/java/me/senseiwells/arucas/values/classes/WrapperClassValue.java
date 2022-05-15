package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.*;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberOperations;
import me.senseiwells.arucas.values.functions.WrapperMemberFunction;

import java.util.List;
import java.util.Objects;

public class WrapperClassValue extends GenericValue<WrapperClassDefinition> implements MemberOperations {
	private final IArucasWrappedClass wrapperClass;

	public WrapperClassValue(WrapperClassDefinition arucasClass, IArucasWrappedClass wrapperClass) {
		super(arucasClass);
		this.wrapperClass = wrapperClass;
	}

	public String getName() {
		return this.value.getName();
	}

	public WrapperMemberFunction getOperatorMethod(Token.Type type, int parameters) {
		return this.value.operatorMap.get(type, parameters);
	}

	public IArucasWrappedClass getWrapper() {
		return this.wrapperClass;
	}

	public <T extends IArucasWrappedClass> T getWrapper(Class<T> clazz) {
		if (!clazz.isInstance(this.wrapperClass)) {
			String wrapperName = ArucasWrapperCreator.getWrapperName(clazz);
			String thisWrapperName = ArucasWrapperCreator.getWrapperName(this.wrapperClass.getClass());
			throw new RuntimeException("Expected %s found %s".formatted(wrapperName, thisWrapperName));
		}
		@SuppressWarnings("unchecked")
		T wrappedClass = (T) this.wrapperClass;
		return wrappedClass;
	}

	private ArucasMemberHandle getHandle(String name) {
		return this.value.getMemberHandle(name);
	}

	@Override
	public boolean isAssignable(String name) {
		ArucasMemberHandle handle = this.getHandle(name);
		return handle != null && handle.isAssignable();
	}

	@Override
	public boolean setMember(String name, Value value) {
		ArucasMemberHandle handle = this.getHandle(name);
		if (handle != null) {
			return handle.set(this.wrapperClass, value);
		}

		return false;
	}

	@Override
	public Value getMember(String name) {
		ArucasMemberHandle handle = this.getHandle(name);
		if (handle != null) {
			return handle.get(this.wrapperClass);
		}
		return null;
	}

	@Override
	public ArucasFunctionMap<?> getAllMembers() {
		return this.value.getMethods();
	}

	@Override
	public Object asJavaValue() {
		return this.wrapperClass.asJavaValue();
	}

	@Override
	public Value onUnaryOperation(Context context, Token.Type type, ISyntax syntaxPosition) throws CodeError {
		FunctionValue function = this.getOperatorMethod(type, 1);
		if (function != null) {
			return function.call(context, ArucasList.arrayListOf(this));
		}
		return super.onUnaryOperation(context, type, syntaxPosition);
	}

	@Override
	protected Value onBinaryOperation(Context context, Value other, Token.Type type, ISyntax syntaxPosition) throws CodeError {
		FunctionValue function = this.getOperatorMethod(type, 2);
		if (function != null) {
			return function.call(context, ArucasList.arrayListOf(this, other));
		}
		return super.onBinaryOperation(context, other, type, syntaxPosition);
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		// If 'toString' is overridden we should use that here
		FunctionValue memberFunction = this.getMember("toString", 1);
		if (memberFunction != null) {
			return memberFunction.call(context, ArucasList.arrayListOf(this)).getAsString(context);
		}

		return "<class " + this.getName() + "@" + Integer.toHexString(this.getHashCode(context)) + ">";
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		// If 'hashCode' is overridden we should use that here
		FunctionValue memberFunction = this.getMember("hashCode", 1);
		if (memberFunction != null) {
			Value value = memberFunction.call(context, ArucasList.arrayListOf(this));
			if (!(value instanceof NumberValue numberValue)) {
				throw new RuntimeError("hashCode() must return a number", memberFunction.getPosition(), context);
			}
			return numberValue.value.intValue();
		}

		return Objects.hashCode(this);
	}

	@Override
	public boolean isEquals(Context context, Value other) throws CodeError {
		FunctionValue equalsMethod = this.getOperatorMethod(Token.Type.EQUALS, 2);
		if (equalsMethod != null) {
			List<Value> parameters = ArucasList.arrayListOf(this, other);
			Value value = equalsMethod.call(context, parameters);
			if (!(value instanceof BooleanValue booleanValue)) {
				throw new RuntimeError("operator '==' must return a boolean", equalsMethod.getPosition(), context);
			}
			return booleanValue.value;
		}

		return this == other;
	}

	@Override
	public FunctionValue onMemberCall(Context context, String name, List<Value> arguments, ValueRef reference, ISyntax position) {
		// Get the class method
		FunctionValue function = this.getMember(name, arguments.size() + 1);
		if (function == null) {
			// If null get built in member
			function = context.getMemberFunction(this.getClass(), name, arguments.size() + 1);

			// We check fields as a last resort
			if (function == null && this.getMember(name) instanceof FunctionValue delegate) {
				return delegate;
			}
		}

		arguments.add(0, this);
		return function;
	}

	@Override
	public Value onMemberAccess(Context context, String name, ISyntax position) {
		Value value = this.getMember(name);
		if (value != null) {
			return value;
		}

		WrapperMemberFunction function = this.value.getMethods().get(name);
		if (function != null) {
			return function.getDelegate(this);
		}

		return super.onMemberAccess(context, name, position);
	}

	@Override
	public Value onMemberAssign(Context context, String name, Functions.Uni<Context, Value> valueGetter, ISyntax position) throws CodeError {
		if (!this.hasMember(name)) {
			throw new RuntimeError(
				"The member '%s' does not exist for the class '%s'".formatted(name, this.getTypeName()),
				position, context
			);
		}
		Value value = valueGetter.apply(context);
		if (!this.setMember(name, value)) {
			throw new RuntimeError(
				"The member '%s' cannot be set for the class '%s'".formatted(name, this.getTypeName()),
				position, context
			);
		}
		return value;
	}

	@Override
	public String getTypeName() {
		return this.getName();
	}

	@Override
	public TypeValue getType(Context context, ISyntax syntaxPosition) throws CodeError {
		return this.value.getType();
	}

	@Override
	public GenericValue<WrapperClassDefinition> copy(Context context) throws CodeError {
		return this;
	}
}
