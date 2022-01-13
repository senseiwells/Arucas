package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NumberValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.ClassMemberFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberOperations;

import java.util.*;

public class ArucasClassValue extends Value<AbstractClassDefinition> implements MemberOperations {
	private final ArucasFunctionMap<ClassMemberFunction> methods;
	private final Map<Token.Type, ClassMemberFunction> operatorMethods;
	private final Map<String, Value<?>> members;
	
	public ArucasClassValue(AbstractClassDefinition arucasClass) {
		super(arucasClass);
		this.methods = new ArucasFunctionMap<>();
		this.operatorMethods = new HashMap<>();
		this.members = new HashMap<>();
	}
	
	public String getName() {
		return this.value.getName();
	}
	
	protected void addMethod(ClassMemberFunction method) {
		this.methods.add(method);
	}

	protected void addOperatorMethods(Token.Type type, ClassMemberFunction method) {
		this.operatorMethods.put(type, method);
	}
	
	public void addMemberVariable(String name, Value<?> value) {
		this.members.put(name, value);
	}

	public boolean hasOperatorMethod(Token.Type type) {
		return this.operatorMethods.containsKey(type);
	}

	public ClassMemberFunction getOperatorMethod(Token.Type type) {
		return this.operatorMethods.get(type);
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
		FunctionValue memberFunction = this.getMember("equals", 2);
		if (memberFunction != null) {
			List<Value<?>> parameters = new ArrayList<>();
			parameters.add(other);
			Value<?> value = memberFunction.call(context, parameters);
			if (!(value instanceof BooleanValue booleanValue)) {
				throw new RuntimeError("equals() must return a boolean", memberFunction.syntaxPosition, context);
			}
			return booleanValue.value;
		}

		return this == other;
	}
}
