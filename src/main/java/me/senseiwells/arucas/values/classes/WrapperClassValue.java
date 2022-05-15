package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.ArucasOperatorMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberOperations;
import me.senseiwells.arucas.values.functions.WrapperClassMemberFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WrapperClassValue extends GenericValue<WrapperClassDefinition> implements MemberOperations {
	private final IArucasWrappedClass wrapperClass;
	private final ArucasFunctionMap<WrapperClassMemberFunction> methods;
	private final ArucasOperatorMap<WrapperClassMemberFunction> operatorMap;


	public WrapperClassValue(WrapperClassDefinition arucasClass, IArucasWrappedClass wrapperClass) {
		super(arucasClass);
		this.wrapperClass = wrapperClass;
		this.methods = new ArucasFunctionMap<>();
		this.operatorMap = new ArucasOperatorMap<>();
	}

	public String getName() {
		return this.value.getName();
	}

	protected void addMethod(WrapperClassMemberFunction method) {
		this.methods.add(method);
	}

	public void addOperatorMethod(Token.Type type, WrapperClassMemberFunction method) {
		this.operatorMap.add(type, method);
	}

	public WrapperClassMemberFunction getOperatorMethod(Token.Type type, int parameters) {
		return this.operatorMap.get(type, parameters);
	}

	public <T extends IArucasWrappedClass> T getWrapper(Class<T> clazz) {
		if (!clazz.isInstance(this.wrapperClass)) {
			String wrapperName = ArucasWrapperExtension.getWrapperName(clazz);
			String thisWrapperName = ArucasWrapperExtension.getWrapperName(this.wrapperClass.getClass());
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
		return this.getAllMembers().get(name);
	}

	@Override
	public ArucasFunctionMap<?> getAllMembers() {
		return this.methods;
	}

	@Override
	public Object asJavaValue() {
		return this.wrapperClass.asJavaValue();
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		// If 'toString' is overridden we should use that here
		FunctionValue memberFunction = this.getMember("toString", 1);
		if (memberFunction != null) {
			return memberFunction.call(context, new ArrayList<>()).getAsString(context);
		}

		return "<class " + this.getName() + "@" + Integer.toHexString(this.getHashCode(context)) + ">";
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		// If 'hashCode' is overridden we should use that here
		FunctionValue memberFunction = this.getMember("hashCode", 1);
		if (memberFunction != null) {
			Value value = memberFunction.call(context, new ArrayList<>());
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
			List<Value> parameters = new ArrayList<>();
			parameters.add(other);
			Value value = equalsMethod.call(context, parameters);
			if (!(value instanceof BooleanValue booleanValue)) {
				throw new RuntimeError("operator '==' must return a boolean", equalsMethod.getPosition(), context);
			}
			return booleanValue.value;
		}

		return this == other;
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
