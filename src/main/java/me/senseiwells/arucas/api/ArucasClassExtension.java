package me.senseiwells.arucas.api;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.ConstructorDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.api.docs.MemberDoc;
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

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

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

	/**
	 * This generates the Java implemented Arucas functions
	 * into Arucas code, including all the methods and fields,
	 * that are available, and also providing documentation for
	 * the classes, fields and functions.
	 */
	@Override
	public String toString() {
		Class<?> thisClass = this.getClass();

		StringBuilder builder = new StringBuilder();
		final String indent = "    ";

		ClassDoc classDoc = thisClass.getAnnotation(ClassDoc.class);
		if (classDoc != null) {
			builder.append("/* Native, implemented in Java\n");
			for (String desc : classDoc.desc()) {
				builder.append(" * ").append(desc);
			}
			builder.append("\n * Import with 'import ").append(classDoc.name());
			builder.append(" from ").append(classDoc.importPath()).append("'");
			builder.append("\n */\n");
		}
		else {
			builder.append("/* Native, implemented in Java */\n");
		}

		Map<Integer, ConstructorDoc> constructorDocMap = new HashMap<>();
		Map<String, FunctionDoc> functionDocMap = new HashMap<>();
		Map<String, MemberDoc> memberDocMap = new HashMap<>();
		for (Method method : thisClass.getDeclaredMethods()) {
			ConstructorDoc constructorDoc = method.getAnnotation(ConstructorDoc.class);
			if (constructorDoc != null) {
				constructorDocMap.put(constructorDoc.params().length, constructorDoc);
				continue;
			}
			FunctionDoc functionDoc = method.getAnnotation(FunctionDoc.class);
			if (functionDoc != null) {
				String functionId = (functionDoc.isStatic() ? "$" : "") + functionDoc.name();
				functionId += functionDoc.isVarArgs() ? "-1" : functionDoc.params().length / 3;
				functionDocMap.put(functionId, functionDoc);
				continue;
			}
			MemberDoc[] memberDocs = method.getAnnotationsByType(MemberDoc.class);
			for (MemberDoc memberDoc : memberDocs) {
				String memberId = (memberDoc.isStatic() ? "$" : "") + memberDoc.name();
				memberDocMap.put(memberId, memberDoc);
			}
		}

		builder.append("class ");

		builder.append(this.getName()).append(" {\n");
		boolean hadVars = false;
		for (String member : this.getStaticMemberVariables().keySet()) {
			MemberDoc doc = memberDocMap.get("$" + member);
			if (doc != null) {
				builder.append(indent).append("/*\n");
				builder.append(indent).append(" * ").append(doc.desc()).append("\n");
				builder.append(indent).append(" * The type of the field is ").append(doc.type()).append("\n");
				builder.append(indent).append(" * This field is ").append(doc.assignable() ? "" : "not ").append("assignable\n");
				builder.append(indent).append(" */\n");
			}
			builder.append(indent).append("static var ").append(member).append(";\n");
			hadVars = true;
		}

		if (hadVars) {
			builder.append("\n");
		}

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

		for (ConstructorFunction function : this.getConstructors()) {
			ConstructorDoc doc = constructorDocMap.get(function.getParameterCount());
			String[] params;
			List<String> parameterNames = null;
			if (doc != null && (params = doc.params()).length % 3 == 0) {
				parameterNames = new ArrayList<>();
				builder.append(indent).append("/*\n");
				for (String desc : doc.desc()) {
					builder.append(indent).append(" * ").append(desc).append("\n");
				}
				for (int i = 0; i < params.length; i++) {
					int index = i % 3;
					switch (index) {
						case 0 -> builder.append(indent).append(" * ").append("Parameter - ").append(params[i]).append(": ");
						case 1 -> parameterNames.add(params[i]);
						case 2 -> builder.append(params[i]).append("\n");
					}
				}
				builder.append("\n").append(indent).append(" */\n");
			}
			builder.append(indent).append(this.getName()).append("(");
			if (parameterNames == null) {
				parameterNames = function.argumentNames;
			}
			argumentAdder.accept(parameterNames);
			builder.append(") { }\n\n");
		}

		Function<FunctionDoc, List<String>> addFunctionDoc = (doc) -> {
			String[] params;
			List<String> parameterNames = null;
			if (doc != null && (params = doc.params()).length % 3 == 0) {
				parameterNames = new ArrayList<>();
				builder.append(indent).append("/*\n");

				boolean deprecated = false;
				for (String deprecate : doc.deprecated()) {
					builder.append(indent).append(" * ");
					if (!deprecated) {
						builder.append("Deprecated: ");
						deprecated = true;
					}
					builder.append(deprecate).append("\n");
				}

				for (String desc : doc.desc()) {
					builder.append(indent).append(" * ").append(desc).append("\n");
				}

				for (int i = 0; i < params.length; i += 3) {
					builder.append(indent).append(" * ").append("Parameter - ");
					builder.append(params[i]).append(" (").append(params[i + 1]).append("): ");
					builder.append(params[i + 2]).append("\n");
					parameterNames.add(params[i + 1]);
				}

				String[] returns = doc.returns();
				if (returns.length == 2) {
					builder.append(indent).append(" * ").append("Returns - ").append(returns[0]);
					builder.append(": ").append(returns[1]).append("\n");
				}

				boolean hasThrown = false;
				for (String throwMessage : doc.throwMsgs()) {
					if (!hasThrown) {
						builder.append(indent).append(" * ").append("Throws - Error: ").append(throwMessage);
						hasThrown = true;
						continue;
					}
					builder.append(", ").append(throwMessage);
				}
				if (hasThrown) {
					builder.append("\n");
				}

				builder.append(indent).append(" */\n");
			}
			return parameterNames;
		};

		for (MemberFunction function : this.getMethods()) {
			FunctionDoc doc = functionDocMap.get(function.getName() + (function.getParameterCount() == -1 ? "-1" : function.getParameterCount() - 1));
			List<String> parameterNames = addFunctionDoc.apply(doc);
			builder.append(indent).append("fun ").append(function.getName()).append("(");
			if (parameterNames == null) {
				parameterNames = function.argumentNames.subList(1, function.argumentNames.size());
			}
			argumentAdder.accept(parameterNames);
			builder.append(") { }\n\n");
		}

		for (FunctionValue function : this.getStaticMethods()) {
			FunctionDoc doc = functionDocMap.get("$" + function.getName() + function.getParameterCount());
			List<String> parameterNames = addFunctionDoc.apply(doc);
			if (parameterNames == null) {
				parameterNames = function.argumentNames;
			}
			builder.append(indent).append("static fun ").append(function.getName()).append("(");
			argumentAdder.accept(parameterNames);
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
