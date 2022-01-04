package me.senseiwells.arucas.api;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.ConstructorFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ArucasClassExtension extends AbstractClassDefinition {
	private final Set<ConstructorFunction> constructors;
	private final ArucasFunctionMap<MemberFunction> methods;

	public ArucasClassExtension(String name) {
		super(name);
		this.constructors = this.getDefinedConstructors();
		this.methods = this.getDefinedMethods();
		this.getStaticMemberVariables().putAll(this.getDefinedStaticVariables());
		for (FunctionValue value : this.getDefinedStaticMethods()) {
			this.getStaticMethods().add(value);
		}
	}

	@Override
	public final void initialiseStatics(Context context) { }

	@Override
	public final ArucasFunctionMap<MemberFunction> getMethods() {
		return this.methods;
	}

	/**
	 * No members are assignable by default
	 */
	@Override
	public boolean isAssignable(String name) {
		return false;
	}

	/**
	 * This lets you define constructors for a Class
	 */
	public Set<ConstructorFunction> getDefinedConstructors() {
		return Set.of();
	}

	/**
	 * This lets you define methods for a Class
	 */
	public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
		return new ArucasFunctionMap<>();
	}

	/**
	 * This lets you define static variables for a Class
	 */
	public Map<String, Value<?>> getDefinedStaticVariables() {
		return Map.of();
	}

	/**
	 * This lets you define static methods for a Class
	 */
	public List<BuiltInFunction> getDefinedStaticMethods() {
		return List.of();
	}

	/**
	 * This returns the new value that was returned inside the constructor
	 */
	@Override
	public Value<?> createNewDefinition(Context context, List<Value<?>> parameters, ISyntax syntaxPosition) throws CodeError {
		if (this.constructors.isEmpty()) {
			throw new RuntimeError("%s cannot be constructed".formatted(this.getName()), syntaxPosition, context);
		}

		int parameterCount = parameters.size();
		for (ConstructorFunction constructor : this.constructors) {
			if (parameterCount != constructor.getParameterCount()) {
				continue;
			}
			return constructor.call(context, parameters, false);
		}
		throw new RuntimeError("No such constructor for %s".formatted(this.getName()), syntaxPosition, context);
	}
}
