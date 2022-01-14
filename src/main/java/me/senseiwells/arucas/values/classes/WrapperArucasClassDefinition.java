package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.wrappers.IArucasWrappedClass;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.WrapperClassMemberFunction;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class WrapperArucasClassDefinition extends AbstractClassDefinition {
	private final Supplier<IArucasWrappedClass> supplier;
	private final Map<String, FieldBoolean> fieldMap;
	private final Map<String, FieldBoolean> staticFieldMap;
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

	public void addField(Field field, boolean isFinal) {
		this.fieldMap.put(field.getName(), new FieldBoolean(field, isFinal));
	}

	public void addStaticField(Field field, boolean isFinal) {
		this.staticFieldMap.put(field.getName(), new FieldBoolean(field, isFinal));
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

	public FieldBoolean getField(String name) {
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
		IArucasWrappedClass wrappedClass = this.supplier.get();
		ArucasWrapperClassValue thisValue = new ArucasWrapperClassValue(this, wrappedClass);
		
		for (WrapperClassMemberFunction function : this.methods) {
			thisValue.addMethod(function.copy(wrappedClass));
		}

		for (Map.Entry<Token.Type, WrapperClassMemberFunction> entry : this.operatorMethods.entrySet()) {
			thisValue.addOperatorMethods(entry.getKey(), entry.getValue().copy(wrappedClass));
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
		FieldBoolean fieldBoolean = this.staticFieldMap.get(name);
		return fieldBoolean != null && !fieldBoolean.isFinal;
	}

	@Override
	public boolean setMember(String name, Value<?> value) {
		if (this.staticFieldMap.containsKey(name)) {
			FieldBoolean fieldBoolean = this.staticFieldMap.get(name);
			if (fieldBoolean.isFinal) {
				return false;
			}
			try {
				fieldBoolean.field.set(null, value);
				return true;
			}
			catch (IllegalAccessException e) {
				this.staticFieldMap.remove(name);
			}
		}
		return false;
	}

	@Override
	public Value<?> getMember(String name) {
		if (this.staticFieldMap.containsKey(name)) {
			try {
				return (Value<?>) this.staticFieldMap.get(name).field.get(null);
			}
			catch (IllegalAccessException e) {
				this.staticFieldMap.remove(name);
			}
		}
		return this.getStaticMethods().get(name);
	}

	@Override
	public Class<?> getValueClass() {
		return ArucasClassValue.class;
	}

	public static record FieldBoolean(Field field, boolean isFinal) { }
}
