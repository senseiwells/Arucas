package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.wrappers.ArucasMemberHandle;
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
import java.util.function.Supplier;

public class WrapperArucasClassDefinition extends AbstractClassDefinition {
	private final Supplier<IArucasWrappedClass> supplier;
	private final Map<String, ArucasMemberHandle> fieldMap;
	private final Map<String, ArucasMemberHandle> staticFieldMap;
	private final ArucasFunctionMap<WrapperClassMemberFunction> methods;
	private final ArucasFunctionMap<WrapperClassMemberFunction> constructors;
	private final ArucasOperatorMap<WrapperClassMemberFunction> operatorMap;
	
	public WrapperArucasClassDefinition(String name, Supplier<IArucasWrappedClass> supplier) {
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
	public ArucasFunctionMap<? extends FunctionValue> getMethods() {
		return this.methods;
	}

	@Override
	public void initialiseStatics(Context context) { }

	@Override
	public ArucasClassValue createNewDefinition(Context context, List<Value<?>> parameters, ISyntax syntaxPosition) throws CodeError {
		return this.createNewDefinition(this.supplier.get(), context, parameters, syntaxPosition);
	}
	
	private ArucasClassValue createNewDefinition(IArucasWrappedClass wrappedClass, Context context, List<Value<?>> parameters, ISyntax syntaxPosition) throws CodeError {
		ArucasWrapperClassValue thisValue = new ArucasWrapperClassValue(this, wrappedClass);

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
		if (this.staticFieldMap.containsKey(name)) {
			return this.staticFieldMap.get(name).get(null);
		}
		return this.getStaticMethods().get(name);
	}

	@Override
	public Class<?> getValueClass() {
		return ArucasClassValue.class;
	}
}
