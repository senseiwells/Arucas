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
import me.senseiwells.arucas.values.functions.WrapperMemberFunction;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class WrapperClassDefinition extends AbstractClassDefinition {
	private final Supplier<IArucasWrappedClass> supplier;
	private final Map<String, ArucasMemberHandle> fieldMap;
	private final Map<String, ArucasMemberHandle> staticFieldMap;
	private final ArucasFunctionMap<WrapperMemberFunction> methods;
	private final ArucasFunctionMap<WrapperMemberFunction> constructors;
	protected final ArucasOperatorMap<WrapperMemberFunction> operatorMap;

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

	public void addMethod(WrapperMemberFunction method) {
		this.methods.add(method);
	}

	public void addConstructor(WrapperMemberFunction constructor) {
		this.constructors.add(constructor);
	}

	public void addOperatorMethod(Token.Type tokenType, WrapperMemberFunction method) {
		this.operatorMap.add(tokenType, method);
	}

	public ArucasMemberHandle getMemberHandle(String name) {
		return this.fieldMap.get(name);
	}

	@SuppressWarnings("unused")
	public Collection<String> getFieldNames() {
		return this.fieldMap.keySet();
	}

	@Override
	public ArucasFunctionMap<WrapperMemberFunction> getMethods() {
		return this.methods;
	}

	@Override
	public ArucasFunctionMap<WrapperMemberFunction> getConstructors() {
		return this.constructors;
	}

	@Override
	public Class<?> getDefiningClass() {
		return this.supplier.get().getClass();
	}

	@Override
	public void initialiseStatics(Context context) { }

	@Override
	public WrapperClassValue createNewDefinition(Context context, List<Value> parameters, ISyntax syntaxPosition) throws CodeError {
		return this.createDefinition(this.supplier.get(), context, parameters, syntaxPosition);
	}

	@SuppressWarnings("unused")
	public WrapperClassValue createNewDefinition(IArucasWrappedClass wrappedClass, Context context, List<Value> parameters) throws CodeError {
		if (this.supplier.get().getClass() != wrappedClass.getClass()) {
			throw new RuntimeException("Wrong wrapper class passed in");
		}
		return this.createDefinition(wrappedClass, context, parameters, null);
	}

	private WrapperClassValue createDefinition(IArucasWrappedClass wrappedClass, Context context, List<Value> parameters, ISyntax syntaxPosition) throws CodeError {
		WrapperClassValue thisValue = new WrapperClassValue(this, wrappedClass);

		int parameterCount = parameters.size() + 1;
		if (this.constructors.isEmpty() && parameterCount == 1) {
			return thisValue;
		}

		WrapperMemberFunction constructor = this.constructors.get("", parameterCount);
		if (constructor == null) {
			if (syntaxPosition == null) {
				throw new RuntimeException("No such constructor for %s".formatted(this.getName()));
			}
			throw new RuntimeError("No such constructor for %s".formatted(this.getName()), syntaxPosition, context);
		}

		parameters.add(0, thisValue);
		constructor.call(context, parameters, false);

		return thisValue;
	}

	@Override
	public boolean isAssignable(String name) {
		ArucasMemberHandle handle = this.staticFieldMap.get(name);
		return handle != null && !handle.isAssignable();
	}

	@Override
	public boolean setMember(String name, Value value) {
		if (this.staticFieldMap.containsKey(name)) {
			return this.staticFieldMap.get(name).set(null, value);
		}

		return false;
	}

	@Override
	public Value getMember(String name) {
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
}
