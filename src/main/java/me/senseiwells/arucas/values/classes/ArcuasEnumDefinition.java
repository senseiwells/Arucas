package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.nodes.ListNode;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.BuiltInFunction;
import me.senseiwells.arucas.values.functions.ClassMemberFunction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArcuasEnumDefinition extends ArucasClassDefinition {
	private final Map<String, ListNode> enumInitializerMap;
	private final List<EnumValue> enumValues;

	public ArcuasEnumDefinition(String name) {
		super(name);
		this.enumInitializerMap = new LinkedHashMap<>();
		this.enumValues = new ArrayList<>();
		this.addStaticMethod(new BuiltInFunction("values", this::values));
		this.addStaticMethod(new BuiltInFunction("fromString", "string", this::fromString));
	}

	public void addEnum(String enumName, ListNode node) {
		this.enumInitializerMap.put(enumName, node);
	}

	public Value<?> fromString(Context context, BuiltInFunction function) throws CodeError {
		StringValue stringValue = function.getFirstParameter(context, StringValue.class);
		return this.getEnumValue(stringValue.value);
	}

	public Value<?> values(Context context, BuiltInFunction function) {
		ArucasList list = new ArucasList();
		list.addAll(this.enumValues);
		return new ListValue(list);
	}

	public Value<?> getEnumValue(String name) {
		for (EnumValue enumValue : this.enumValues) {
			if (enumValue.getEnumName().equals(name)) {
				return enumValue;
			}
		}
		return NullValue.NULL;
	}

	private EnumValue createEnumValue(String enumName, Context ctx, List<Value<?>> parameters, ISyntax syntaxPosition) throws CodeError, ThrowValue {
		Context context = this.getLocalContext(ctx);

		EnumValue thisValue = new EnumValue(this, enumName, this.enumValues.size());

		this.addClassProperties(thisValue, context);

		int parameterCount = parameters.size() + 1;
		if (this.constructors.isEmpty() && parameterCount == 1) {
			return thisValue;
		}

		ClassMemberFunction constructor = this.constructors.get(this.getName(), parameterCount);
		if (constructor == null) {
			throw new RuntimeError("No such constructor for %s".formatted(this.getName()), syntaxPosition, context);
		}

		constructor.copy(thisValue).call(context, parameters, false);

		return thisValue;
	}

	@Override
	protected void initialiseStatics(Context context) throws CodeError, ThrowValue {
		Map<String, Value<?>> staticMap = this.getStaticMemberVariables();
		for (Map.Entry<String, ListNode> entry : this.enumInitializerMap.entrySet()) {
			String name = entry.getKey();
			ListNode node = entry.getValue();

			if (staticMap.containsKey(name)) {
				throw new RuntimeError("Cannot create enum '%s' because '%s' is already defined".formatted(name, name), node.syntaxPosition, context);
			}
			ListValue list = node.visit(context);
			EnumValue enumValue = this.createEnumValue(name, context, list.value, node.syntaxPosition);

			this.enumValues.add(enumValue);
			this.getStaticMemberVariables().put(name, enumValue);
		}
		super.initialiseStatics(context);
	}

	@Override
	public ArucasClassValue createNewDefinition(Context context, List<Value<?>> parameters, ISyntax syntaxPosition) throws CodeError {
		throw new RuntimeError("Enums cannot be constructed", syntaxPosition, context);
	}
}
