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

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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

	@Override
	public final ArucasFunctionMap<ConstructorFunction> getConstructors() {
		return this.constructors;
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
		// We don't need to get local context because this isn't user defined
		if (this.constructors.isEmpty()) {
			throw new RuntimeError("%s cannot be constructed".formatted(this.getName()), syntaxPosition, context);
		}

		ConstructorFunction constructor = this.constructors.get("", parameters.size());
		if (constructor == null) {
			throw new RuntimeError("No such constructor for %s".formatted(this.getName()), syntaxPosition, context);
		}

		return constructor.call(context, parameters, false);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("/* Native, implemented in Java */\nclass ");
		final String indent = "    ";

		Consumer<List<String>> argumentAdder = argumentList -> {
			Iterator<String> stringIterator = argumentList.iterator();
			while (stringIterator.hasNext()) {
				String argName = stringIterator.next();
				builder.append(argName);
				if (stringIterator.hasNext()) {
					builder.append(", ");
				}
			}
		};

		builder.append(this.getName()).append(" {\n");
		boolean hadVars = false;
		for (String member : this.getStaticMemberVariables().keySet()) {
			builder.append(indent).append("static var ").append(member).append(";\n");
			hadVars = true;
		}

		if (hadVars) {
			builder.append("\n");
		}
		for (ConstructorFunction function : this.getConstructors()) {
			builder.append(indent).append(this.getName()).append("(");
			argumentAdder.accept(function.argumentNames);
			builder.append(") { }\n\n");
		}

		for (MemberFunction function : this.getMethods()) {
			builder.append(indent).append("fun ").append(function.getName()).append("(");
			argumentAdder.accept(function.argumentNames.subList(1, function.argumentNames.size()));
			builder.append(") { }\n\n");
		}

		for (FunctionValue function : this.getStaticMethods()) {
			builder.append(indent).append("static fun ").append(function.getName()).append("(");
			argumentAdder.accept(function.argumentNames);
			builder.append(") { }\n\n");
		}

		String classAsString = builder.toString();
		if (classAsString.endsWith("\n\n")) {
			classAsString = classAsString.substring(0, classAsString.length() - 1);
		}
		classAsString += "}";
		return classAsString;
	}
}
