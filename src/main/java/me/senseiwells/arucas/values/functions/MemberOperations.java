package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public interface MemberOperations {
	/**
	 * Returns if this object contains the specified member
	 */
	default boolean hasMember(String name) {
		return this.getMember(name) != null;
	}

	/**
	 * Returns if this object contains the member with the specified parameters
	 */
	default boolean hasMember(String name, int parameters) {
		return this.getMember(name, parameters) != null;
	}

	/**
	 * Returns if the specified member is allowed to change value
	 */
	boolean isAssignable(String name);

	/**
	 * Change the value of a member inside this object
	 */
	boolean setMember(Context context, ISyntax position, String name, Value value) throws RuntimeError;

	/**
	 * Returns a member of this object
	 */
	Value getMember(String name);

	/**
	 * Returns a member of this object with the specified amount of parameters
	 */
	default FunctionValue getMember(String name, int parameters) {
		return this.getAllMembers().get(name, parameters);
	}

	/**
	 * Returns a delegate value of a function
	 */
	default FunctionValue getDelegate(String name) {
		return this.getAllMembers().get(name);
	}

	/**
	 * Returns all the member functions of that class
	 */
	ArucasFunctionMap<?> getAllMembers();
}
