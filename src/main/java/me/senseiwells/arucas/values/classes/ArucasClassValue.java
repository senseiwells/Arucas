package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.ArucasOperatorMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.TypeValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.ClassMemberFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberOperations;

import java.util.*;

public class ArucasClassValue extends Value<AbstractClassDefinition> implements MemberOperations {
	private final ArucasFunctionMap<FunctionValue> methods;
	private final Map<String, Value<?>> members;
	private final ArucasOperatorMap<ClassMemberFunction> operatorMap;

	public ArucasClassValue(AbstractClassDefinition arucasClass) {
		super(arucasClass);
		this.methods = new ArucasFunctionMap<>();
		this.members = new HashMap<>();
		this.operatorMap = new ArucasOperatorMap<>();
	}

	public String getName() {
		return this.value.getName();
	}

	protected void addMethod(FunctionValue method) {
		this.methods.add(method);
	}

	public void addMemberVariable(String name, Value<?> value) {
		this.members.put(name, value);
	}

	public void addOperatorMethod(Token.Type type, ClassMemberFunction method) {
		this.operatorMap.add(type, method);
	}

	public boolean hasOperatorMethod(Token.Type type, int parameters) {
		return this.operatorMap.hasOperator(type, parameters);
	}

	public ClassMemberFunction getOperatorMethod(Token.Type type, int parameters) {
		return this.operatorMap.get(type, parameters);
	}

	@Override
	public boolean isAssignable(String name) {
		// Only member variables are modifiable
		return this.members.get(name) != null;
	}

	@Override
	public boolean setMember(String name, Value<?> value) {
		if (!this.isAssignable(name)) {
			return false;
		}

		this.members.put(name, value);
		return true;
	}

	@Override
	public Value<?> getMember(String name) {
		Value<?> member = this.members.get(name);
		if (member != null) {
			return member;
		}

		return this.methods.get(name);
	}

	@Override
	public final ArucasFunctionMap<?> getAllMembers() {
		return this.methods;
	}

	@Override
	public ArucasClassValue copy(Context context) {
		return this;
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		// If 'hashCode' is overridden we should use that here
		FunctionValue memberFunction = this.getMember("hashCode", 1);
		if (memberFunction != null) {
			Value<?> value = memberFunction.call(context, new ArrayList<>());
			if (!(value instanceof NumberValue numberValue)) {
				throw new RuntimeError("hashCode() must return a number", memberFunction.syntaxPosition, context);
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
			return memberFunction.call(context, new ArrayList<>()).getAsString(context);
		}

		return "<class " + this.getName() + "@" + Integer.toHexString(this.getHashCode(context)) + ">";
	}

	@Override
	public boolean isEquals(Context context, Value<?> other) throws CodeError {
		// If 'equals' is overridden we should use that here
		FunctionValue equalsMethod = this.getOperatorMethod(Token.Type.EQUALS, 2);
		/*if (equalsMethod == null) {
			equalsMethod = this.getMember("equals", 2);
		}*/
		if (equalsMethod != null) {
			List<Value<?>> parameters = new ArrayList<>();
			parameters.add(other);
			Value<?> value = equalsMethod.call(context, parameters);
			if (!(value instanceof BooleanValue booleanValue)) {
				throw new RuntimeError("operator '==' must return a boolean", equalsMethod.syntaxPosition, context);
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
	public final TypeValue getType(Context context, ISyntax syntaxPosition) {
		return this.value.getType();
	}
}
