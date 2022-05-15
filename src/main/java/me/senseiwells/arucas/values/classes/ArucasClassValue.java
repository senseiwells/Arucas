package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.Functions;
import me.senseiwells.arucas.utils.ValueRef;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberOperations;
import me.senseiwells.arucas.values.functions.UserDefinedClassFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ArucasClassValue extends GenericValue<ArucasClassDefinition> implements MemberOperations {
	private final Map<String, Value> members;

	public ArucasClassValue(ArucasClassDefinition arucasClass) {
		super(arucasClass);
		this.members = new HashMap<>();
	}

	public String getName() {
		return this.value.getName();
	}

	public void addMemberVariable(String name, Value value) {
		this.members.put(name, value);
	}

	public boolean hasOperatorMethod(Token.Type type, int parameters) {
		return this.value.operatorMap.hasOperator(type, parameters);
	}

	public UserDefinedClassFunction getOperatorMethod(Token.Type type, int parameters) {
		return this.value.operatorMap.get(type, parameters);
	}

	@Override
	public boolean isAssignable(String name) {
		// Only member variables are modifiable
		return this.members.get(name) != null;
	}

	@Override
	public boolean setMember(String name, Value value) {
		if (!this.isAssignable(name)) {
			return false;
		}

		this.members.put(name, value);
		return true;
	}

	@Override
	public Value getMember(String name) {
		return this.members.get(name);
	}

	@Override
	public final ArucasFunctionMap<?> getAllMembers() {
		return this.value.getMethods();
	}

	@Override
	public ArucasClassValue copy(Context context) {
		return this;
	}

	@Override
	public Object asJavaValue() {
		return this;
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
	public String getAsString(Context context) throws CodeError {
		// If 'toString' is overridden we should use that here
		FunctionValue memberFunction = this.getMember("toString", 1);
		if (memberFunction != null) {
			return memberFunction.call(context, ArucasList.arrayListOf(this)).getAsString(context);
		}

		return "<class " + this.getName() + "@" + Integer.toHexString(this.getHashCode(context)) + ">";
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

		UserDefinedClassFunction function = this.value.getMethods().get(name);
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
	public final TypeValue getType(Context context, ISyntax syntaxPosition) {
		return this.value.getType();
	}
}
