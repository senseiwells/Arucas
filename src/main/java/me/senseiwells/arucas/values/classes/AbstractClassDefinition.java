package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberOperations;

import java.util.*;

public abstract class AbstractClassDefinition implements MemberOperations {
	private final String name;
	private final List<FunctionValue> staticMethods;
	private final Map<String, Value<?>> staticMemberVariables;

	public AbstractClassDefinition(String name) {
		this.name = name;
		this.staticMethods = new ArrayList<>();
		this.staticMemberVariables = new HashMap<>();
	}

	public final String getName() {
		return this.name;
	}

	public abstract Collection<? extends FunctionValue> getMethods();

	public final Map<String, Value<?>> getStaticMemberVariables() {
		return this.staticMemberVariables;
	}

	public final List<FunctionValue> getStaticMethods() {
		return this.staticMethods;
	}

	public final void addStaticMethod(FunctionValue method) {
		this.staticMethods.add(method);
	}

	public abstract Class<?> getValueClass();

	/**
	 * This gets called when the class is initialised
	 */
	public abstract void initialiseStatics(Context context) throws ThrowValue, CodeError;

	/**
	 * This gets called when a new instance of this object is created
	 */
	public abstract Value<?> createNewDefinition(Context context, List<Value<?>> parameters, ISyntax syntaxPosition) throws CodeError, ThrowValue;

	/**
	 * These methods are for Runtime, when accessing and assigning static members
	 */

	@Override
	public boolean isAssignable(String name) {
		// Only static member variables are modifiable
		return this.staticMemberVariables.get(name) != null;
	}

	@Override
	public final boolean hasMember(String name) {
		return MemberOperations.super.hasMember(name);
	}

	@Override
	public final boolean hasMember(String name, int parameters) {
		return MemberOperations.super.hasMember(name, parameters);
	}

	@Override
	public final FunctionValue getDelegate(String name) {
		return MemberOperations.super.getDelegate(name);
	}

	@Override
	public final boolean setMember(String name, Value<?> value) {
		if (!this.isAssignable(name)) {
			return false;
		}

		this.staticMemberVariables.put(name, value);
		return true;
	}

	@Override
	public final Value<?> getMember(String name) {
		Value<?> member = this.staticMemberVariables.get(name);
		if (member == null) {
			return this.getDelegate(name);
		}

		return member;
	}

	@Override
	public final FunctionValue getMember(String name, int parameters) {
		return MemberOperations.super.getMember(name, parameters);
	}

	@Override
	public final Iterable<FunctionValue> getAllMembers() {
		return this.staticMethods;
	}
}
