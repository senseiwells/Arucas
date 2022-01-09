package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.ClassMemberFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberOperations;

import java.util.*;

public class ArucasClassValue extends Value<ArucasClassDefinition> implements MemberOperations {
	private final ArucasFunctionMap<ClassMemberFunction> methods;
	private final Map<Token.Type, ClassMemberFunction> operatorMethods;
	private final Map<String, Value<?>> members;
	
	public ArucasClassValue(ArucasClassDefinition arucasClass) {
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
	
	protected void addMemberVariable(String name, Value<?> value) {
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
	public ArucasFunctionMap<?> getAllMembers() {
		return this.methods;
	}

	@Override
	public ArucasClassValue copy() {
		return this;
	}
	
	@Override
	public int getHashCode(Context context) throws CodeError {
		// TODO: Use the member function hashCode if present
		return this.hashCode();
	}
	
	@Override
	public String getStringValue(Context context) throws CodeError {
		// If 'toString' is overwritten we should return that value here
		FunctionValue memberFunction = this.getMember("toString", 1);
		if (memberFunction != null) {
			return memberFunction.call(context, new ArrayList<>()).getStringValue(context);
		}
		
		return "<class " + this.getName() + "@" + Integer.toHexString(this.hashCode()) + ">";
	}
	
	@Override
	public boolean isEquals(Context context, Value<?> other) throws CodeError {
//		if (other instanceof ArucasClassValue otherClass && this.getName().equals(otherClass.getName())) {
//			return this.members.equals(otherClass.members);
//		}
//		return false;
		
		// TODO: Use the member function equals if present
		return this == other;
	}
}
