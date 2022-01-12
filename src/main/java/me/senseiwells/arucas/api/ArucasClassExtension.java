package me.senseiwells.arucas.api;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.ConstructorFunction;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;
import java.util.Map;

public abstract class ArucasClassExtension extends AbstractClassDefinition {
	private final ArucasFunctionMap<ConstructorFunction> constructors;
	private final ArucasFunctionMap<MemberFunction> methods;

	public ArucasClassExtension(String name) {
		super(name);
		this.constructors = this.getDefinedConstructors();
		this.methods = this.getDefinedMethods();
		this.getStaticMemberVariables().putAll(this.getDefinedStaticVariables());
		this.getStaticMethods().addAll(this.getDefinedStaticMethods());
	}

	@Override
	public final void initialiseStatics(Context context) { }

	@Override
	public final ArucasFunctionMap<MemberFunction> getMethods() {
		return this.methods;
	}

	/**
	 * No members are assignable by default.
	 */
	@Override
	public boolean isAssignable(String name) {
		return false;
	}

	/**
	 * This lets you define constructors for a Class.
	 */
	public ArucasFunctionMap<ConstructorFunction> getDefinedConstructors() {
		return ArucasFunctionMap.of();
	}

	/**
	 * This lets you define methods for a Class.
	 */
	public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
		return ArucasFunctionMap.of();
	}

	/**
	 * This lets you define static variables for a Class.
	 */
	public Map<String, Value<?>> getDefinedStaticVariables() {
		return Map.of();
	}

	/**
	 * This lets you define static methods for a Class.
	 */
	public ArucasFunctionMap<BuiltInFunction> getDefinedStaticMethods() {
		return ArucasFunctionMap.of();
	}

	/**
	 * This returns the new value that was returned inside the constructor.
	 */
	@Override
	public Value<?> createNewDefinition(Context context, List<Value<?>> parameters, ISyntax syntaxPosition) throws CodeError {
		if (this.constructors.isEmpty()) {
			throw new RuntimeError("%s cannot be constructed".formatted(this.getName()), syntaxPosition, context);
		}
		
		ConstructorFunction constructor = this.constructors.get(this.getName(), parameters.size());
		if (constructor == null) {
			throw new RuntimeError("No such constructor for %s".formatted(this.getName()), syntaxPosition, context);
		}
		
		return constructor.call(context, parameters, false);
	}
}
