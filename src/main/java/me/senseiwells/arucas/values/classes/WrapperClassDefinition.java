package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.ArucasOperatorMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.WrapperClassMemberFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class WrapperClassDefinition extends AbstractClassDefinition {
	private final Supplier<IArucasWrappedClass> supplier;
	private final Map<String, ArucasMemberHandle> fieldMap;
	private final Map<String, ArucasMemberHandle> staticFieldMap;
	private final ArucasFunctionMap<WrapperClassMemberFunction> methods;
	private final ArucasFunctionMap<WrapperClassMemberFunction> constructors;
	private final ArucasOperatorMap<WrapperClassMemberFunction> operatorMap;

	public WrapperClassDefinition(String name, Supplier<IArucasWrappedClass> supplier) {
		super(name);
		this.supplier = supplier;
		this.fieldMap = new HashMap<>();
		this.staticFieldMap = new HashMap<>();
		this.methods = new ArucasFunctionMap<>();
		this.constructors = new ArucasFunctionMap<>();
		this.operatorMap = new ArucasOperatorMap<>();
	}

	public void addField(ArucasMemberHandle field) {
		this.fieldMap.put(field.getName(), field);
	}

	public void addStaticField(ArucasMemberHandle field) {
		this.staticFieldMap.put(field.getName(), field);
	}

	public void addMethod(WrapperClassMemberFunction method) {
		this.methods.add(method);
	}

	public void addConstructor(WrapperClassMemberFunction constructor) {
		this.constructors.add(constructor);
	}

	public void addOperatorMethod(Token.Type tokenType, WrapperClassMemberFunction method) {
		this.operatorMap.add(tokenType, method);
	}

	public ArucasMemberHandle getMemberHandle(String name) {
		return this.fieldMap.get(name);
	}

	@Override
	public ArucasFunctionMap<WrapperClassMemberFunction> getMethods() {
		return this.methods;
	}

	@Override
	public ArucasFunctionMap<WrapperClassMemberFunction> getConstructors() {
		return this.constructors;
	}

	@Override
	public void initialiseStatics(Context context) {
	}

	@Override
	public WrapperClassValue createNewDefinition(Context context, List<Value<?>> parameters, ISyntax syntaxPosition) throws CodeError {
		return this.createDefinition(this.supplier.get(), context, parameters, syntaxPosition);
	}

	@SuppressWarnings("unused")
	public WrapperClassValue createNewDefinition(IArucasWrappedClass wrappedClass, Context context, List<Value<?>> parameters) throws CodeError {
		if (this.supplier.get().getClass() != wrappedClass.getClass()) {
			throw new RuntimeException("Wrong wrapper class passed in");
		}
		return this.createDefinition(wrappedClass, context, parameters, null);
	}

	private WrapperClassValue createDefinition(IArucasWrappedClass wrappedClass, Context context, List<Value<?>> parameters, ISyntax syntaxPosition) throws CodeError {
		WrapperClassValue thisValue = new WrapperClassValue(this, wrappedClass);

		for (WrapperClassMemberFunction function : this.methods) {
			thisValue.addMethod(function.copy(thisValue, wrappedClass));
		}

		this.operatorMap.forEach((type, function) -> thisValue.addOperatorMethod(type, function.copy(thisValue, wrappedClass)));

		int parameterCount = parameters.size() + 1;
		if (this.constructors.isEmpty() && parameterCount == 1) {
			return thisValue;
		}

		WrapperClassMemberFunction constructor = this.constructors.get("", parameterCount);
		if (constructor == null) {
			if (syntaxPosition == null) {
				throw new RuntimeException("No such constructor for %s".formatted(this.getName()));
			}
			throw new RuntimeError("No such constructor for %s".formatted(this.getName()), syntaxPosition, context);
		}

		constructor.copy(thisValue, wrappedClass).call(context, parameters, false);
		return thisValue;
	}

	@Override
	public boolean isAssignable(String name) {
		ArucasMemberHandle handle = this.staticFieldMap.get(name);
		return handle != null && !handle.isAssignable();
	}

	@Override
	public boolean setMember(String name, Value<?> value) {
		if (this.staticFieldMap.containsKey(name)) {
			return this.staticFieldMap.get(name).set(null, value);
		}

		return false;
	}

	@Override
	public Value<?> getMember(String name) {
		if (name.equals("type")) {
			return this.getType();
		}
		if (this.staticFieldMap.containsKey(name)) {
			return this.staticFieldMap.get(name).get(null);
		}
		return this.getStaticMethods().get(name);
	}

	@Override
	public Class<WrapperClassValue> getValueClass() {
		return WrapperClassValue.class;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("/* Native, implemented in Java */\nclass ");
		final String indent = "    ";

		Consumer<Integer> argumentAdder = integer -> {
			integer = Math.max(integer, 0);

			for (int i = 0; i < integer; i++) {
				String argName = "arg" + (i + 1);
				builder.append(argName);
				if (i < integer - 1) {
					builder.append(", ");
				}
			}
		};

		builder.append(this.getName()).append(" {\n");
		boolean hadVars = false;
		for (String member : this.staticFieldMap.keySet()) {
			builder.append(indent).append("static var ").append(member).append(";\n");
			hadVars = true;
		}
		if (hadVars) {
			builder.append("\n");
		}

		hadVars = false;
		for (String member : this.fieldMap.keySet()) {
			builder.append(indent).append("var ").append(member).append(";\n");
			hadVars = true;
		}
		if (hadVars) {
			builder.append("\n");
		}

		for (FunctionValue function : this.getConstructors()) {
			builder.append(indent).append(this.getName()).append("(");
			argumentAdder.accept(function.getParameterCount() - 1);
			builder.append(") { }\n\n");
		}

		for (FunctionValue function : this.getMethods()) {
			builder.append(indent).append("fun ").append(function.getName()).append("(");
			argumentAdder.accept(function.getParameterCount() - 1);
			builder.append(") { }\n\n");
		}

		this.operatorMap.forEach((type, function) -> {
			builder.append(indent).append("operator ").append(type.toString()).append(" (");
			argumentAdder.accept(function.getParameterCount() - 1);
			builder.append(") { }\n\n");
		});

		for (FunctionValue function : this.getStaticMethods()) {
			builder.append(indent).append("static fun ").append(function.getName()).append("(");
			argumentAdder.accept(function.getParameterCount());
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
