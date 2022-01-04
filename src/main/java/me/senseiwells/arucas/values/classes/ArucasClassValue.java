package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.StackTable;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.ClassMemberFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberOperations;

import java.util.*;

public class ArucasClassValue extends Value<ArucasClassDefinition> implements MemberOperations {
	private final List<ClassMemberFunction> methods;
	private final Map<Token.Type, ClassMemberFunction> operatorMethods;
	private final StackTable members;
	
	public ArucasClassValue(ArucasClassDefinition arucasClass) {
		super(arucasClass);
		this.methods = new ArrayList<>();
		this.operatorMethods = new HashMap<>();
		this.members = new StackTable();
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
		this.members.setLocal(name, value);
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

		this.members.set(name, value);
		return true;
	}
	
	@Override
	public Value<?> getMember(String name) {
		Value<?> member = this.members.get(name);
		if (member != null) {
			return member;
		}

		return this.getDelegate(name, this.methods);
	}

	@Override
	public Iterable<? extends FunctionValue> getAllMembers() {
		return this.methods;
	}

	@Override
	public ArucasClassValue copy() {
		// You should not be able to
		return this;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ArucasClassValue otherClass && this.getName().equals(otherClass.getName())) {
			return this.members.equals(otherClass.members);
		}
		return false;
	}

	@Override
	public String getStringValue(Context context) throws CodeError {
		// If 'toString' is overwritten we should return that value here
		FunctionValue memberFunction = this.getMember("toString", 1);
		if (memberFunction != null) {
			return memberFunction.call(context, new ArrayList<>()).getStringValue(context);
		}
		return "<class %s@%x>".formatted(this.getName(), this.hashCode());
	}
}
