package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.wrappers.ArucasMemberHandle;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
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
	private final Map<Token.Type, WrapperClassMemberFunction> operatorMethods;
	
	public WrapperArucasClassDefinition(String name, Supplier<IArucasWrappedClass> supplier) {
		super(name);
		this.supplier = supplier;
		this.fieldMap = new HashMap<>();
		this.staticFieldMap = new HashMap<>();
		this.methods = new ArucasFunctionMap<>();
		this.constructors = new ArucasFunctionMap<>();
		this.operatorMethods = new HashMap<>();
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
		this.operatorMethods.put(tokenType, method);
	}

	@Override
	public ArucasFunctionMap<? extends FunctionValue> getMethods() {
		return this.methods;
	}

	@Override
	public void initialiseStatics(Context context) { }

	@Override
	public ArucasClassValue createNewDefinition(Context context, List<Value<?>> parameters, ISyntax syntaxPosition) throws CodeError {
		IArucasWrappedClass wrappedClass = this.supplier.get();
		ArucasWrapperClassValue thisValue = new ArucasWrapperClassValue(this, wrappedClass);
		
		for (WrapperClassMemberFunction function : this.methods) {
			thisValue.addMethod(function.copy(wrappedClass));
		}

		for (Map.Entry<Token.Type, WrapperClassMemberFunction> entry : this.operatorMethods.entrySet()) {
			thisValue.addOperatorMethods(entry.getKey(), entry.getValue().copy(wrappedClass));
		}
		
		for (ArucasMemberHandle member : this.fieldMap.values()) {
			thisValue.addField(member);
		}
		
		int parameterCount = parameters.size() + 1;
		if (this.constructors.isEmpty() && parameterCount == 1) {
			return thisValue;
		}

		WrapperClassMemberFunction constructor = this.constructors.get(this.getName(), parameterCount);
		if (constructor == null) {
			throw new RuntimeError("No such constructor for %s".formatted(this.getName()), syntaxPosition, context);
		}
		
		constructor.copy(wrappedClass).call(context, parameters, false);
		return thisValue;
	}

	@Override
	public boolean isAssignable(String name) {
		ArucasMemberHandle handle = this.staticFieldMap.get(name);
		return handle != null && !handle.isFinal();
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
