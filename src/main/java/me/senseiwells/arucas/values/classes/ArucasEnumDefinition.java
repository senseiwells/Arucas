package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.nodes.ListNode;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.UserDefinedClassFunction;

import java.util.*;

public class ArucasEnumDefinition extends ArucasClassDefinition {
	private final Map<String, EnumValue> enums;
	private Map<String, ListNode> enumInitializerMap;

	public ArucasEnumDefinition(String name) {
		super(name);
		this.enums = new LinkedHashMap<>();
		this.enumInitializerMap = new LinkedHashMap<>();

		this.addStaticMethod(BuiltInFunction.of("values", this::values));
		this.addStaticMethod(BuiltInFunction.of("fromString", 1, this::fromString));
	}

	public void addEnum(String enumName, ListNode node) {
		if (this.enumInitializerMap != null) {
			this.enumInitializerMap.put(enumName, node);
		}
	}

	public boolean hasEnum(String enumName) {
		return this.enumInitializerMap == null ? this.enums.containsKey(enumName) : this.enumInitializerMap.containsKey(enumName);
	}

	public Value fromString(Arguments arguments) throws CodeError {
		StringValue stringValue = arguments.getNextString();
		EnumValue enumValue = this.getEnumValue(stringValue.value);
		return enumValue == null ? NullValue.NULL : enumValue;
	}

	public Value values(Arguments arguments) {
		ArucasList list = new ArucasList();
		list.addAll(this.enums.values());
		return new ListValue(list);
	}

	@SuppressWarnings("unused")
	public Collection<String> names() {
		return this.enums.keySet();
	}

	public EnumValue getEnumValue(String name) {
		return this.enums.get(name);
	}

	private EnumValue createEnumValue(String enumName, Context ctx, List<Value> parameters, ISyntax syntaxPosition) throws CodeError, ThrowValue {
		Context context = this.getLocalContext(ctx);

		EnumValue thisValue = new EnumValue(this, enumName, this.enums.size());

		this.addClassProperties(thisValue, context);

		int parameterCount = parameters.size() + 1;
		if (this.constructors.isEmpty() && parameterCount == 1) {
			return thisValue;
		}

		UserDefinedClassFunction constructor = this.constructors.get(this.getName(), parameterCount);
		if (constructor == null) {
			throw new RuntimeError("No such constructor for %s".formatted(this.getName()), syntaxPosition, context);
		}

		parameters.add(0, thisValue);
		constructor.call(context, parameters, false);

		return thisValue;
	}

	@Override
	protected void initialiseStatics(Context context) throws CodeError, ThrowValue {
		Map<String, Value> staticMap = this.getStaticMemberVariables();
		for (Map.Entry<String, ListNode> entry : this.enumInitializerMap.entrySet()) {
			String name = entry.getKey();
			ListNode node = entry.getValue();

			if (staticMap.containsKey(name)) {
				throw new RuntimeError("Cannot create enum '%s' because '%s' is already defined".formatted(name, name), node.syntaxPosition, context);
			}
			ListValue list = node.visit(context);
			EnumValue enumValue = this.createEnumValue(name, context, list.value, node.syntaxPosition);

			this.enums.put(name, enumValue);
			this.getStaticMemberVariables().put(name, enumValue);
		}
		this.enumInitializerMap = null;
		super.initialiseStatics(context);
	}

	@Override
	public boolean isAssignable(String name) {
		return !this.enums.containsKey(name) && super.isAssignable(name);
	}

	@Override
	public ArucasClassValue createNewDefinition(Context context, List<Value> parameters, ISyntax syntaxPosition) throws CodeError {
		throw new RuntimeError("Enums cannot be constructed", syntaxPosition, context);
	}
}
