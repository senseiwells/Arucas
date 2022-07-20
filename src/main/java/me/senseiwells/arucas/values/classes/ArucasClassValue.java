package me.senseiwells.arucas.values.classes;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.*;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.IArucasCollection;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberOperations;
import me.senseiwells.arucas.values.functions.UserDefinedClassFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ArucasClassValue extends GenericValue<ArucasClassDefinition> implements MemberOperations {
	private final Map<String, TypedValue> members;

	public ArucasClassValue(ArucasClassDefinition arucasClass) {
		super(arucasClass);
		this.members = new HashMap<>();
	}

	public String getName() {
		return this.value.getName();
	}

	public void addMemberVariable(String name, TypedValue value) {
		this.members.put(name, value);
	}

	public UserDefinedClassFunction getOperatorMethod(Token.Type type, int parameters) {
		return this.value.operatorMap.get(type, parameters);
	}

	@Override
	public boolean isAssignable(String name) {
		// Only member variables are modifiable
		return this.members.get(name) != null;
	}

	@Override
	public boolean setMember(Context context, ISyntax position, String name, Value value) throws RuntimeError {
		if (!this.isAssignable(name)) {
			return false;
		}

		TypedValue typedValue = this.members.get(name);
		if (typedValue == null) {
			return false;
		}

		if (typedValue.definitions != null && !typedValue.definitions.contains(value.getDefinition(context))) {
			throw new RuntimeError("Member field <%s>.%s got type '%s' but expected '%s'".formatted(
				this.getName(), name, value.getTypeName(),
				TypedValue.typesAsString(typedValue.definitions)
			), position, context);
		}

		typedValue.value = value;
		return true;
	}

	@Override
	public Value getMember(String name) {
		TypedValue typedValue = this.members.get(name);
		return typedValue == null ? null : typedValue.value;
	}

	@Override
	public final ArucasFunctionMap<?> getAllMembers() {
		return this.value.getMethods();
	}

	@Override
	public ArucasClassValue copy(Context context) {
		return this;
	}

	@Override
	public Value castAs(Context context, AbstractClassDefinition definition, ISyntax position) throws CodeError {
		UserDefinedClassFunction castMethod = this.value.getCastMethod(definition);
		if (castMethod != null) {
			return castMethod.call(context, ArucasList.arrayListOf(this));
		}
		return super.castAs(context, definition, position);
	}

	@Override
	public Object asJavaValue() {
		return this;
	}

	@Override
	public boolean isCollection() {
		return this.hasMember("toList", 1);
	}

	@Override
	public IArucasCollection asCollection(Context context, ISyntax syntaxPosition) throws CodeError {
		FunctionValue memberFunction = this.getMember("toList", 1);
		if (memberFunction != null) {
			Value value = memberFunction.call(context, ArucasList.arrayListOf(this));
			if (!(value instanceof ListValue listValue)) {
				throw new RuntimeError("toList() must return a list", memberFunction.getPosition(), context);
			}
			return listValue.value;
		}

		return super.asCollection(context, syntaxPosition);
	}

	@Override
	public Value onUnaryOperation(Context context, Token.Type type, ISyntax syntaxPosition) throws CodeError {
		FunctionValue function = this.getOperatorMethod(type, 1);
		if (function != null) {
			return function.call(context, ArucasList.arrayListOf(this));
		}

		return super.onUnaryOperation(context, type, syntaxPosition);
	}

	@Override
	protected Value onBinaryOperation(Context context, Value other, Token.Type type, ISyntax syntaxPosition) throws CodeError {
		FunctionValue function = this.getOperatorMethod(type, 2);
		if (function != null) {
			return function.call(context, ArucasList.arrayListOf(this, other));
		}
		return super.onBinaryOperation(context, other, type, syntaxPosition);
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		// If 'hashCode' is overridden we should use that here
		FunctionValue memberFunction = this.getMember("hashCode", 1);
		if (memberFunction != null) {
			Value value = memberFunction.call(context, ArucasList.arrayListOf(this));
			if (!(value instanceof NumberValue numberValue)) {
				throw new RuntimeError("hashCode() must return a number", memberFunction.getPosition(), context);
			}
			return numberValue.value.intValue();
		}

		return Objects.hashCode(this);
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		// If 'toString' is overridden we should use that here
		FunctionValue memberFunction = this.getMember("toString", 1);
		if (memberFunction != null) {
			return memberFunction.call(context, ArucasList.arrayListOf(this)).getAsString(context);
		}

		return "<class " + this.getName() + "@" + Integer.toHexString(this.getHashCode(context)) + ">";
	}

	@Override
	public boolean isEquals(Context context, Value other) throws CodeError {
		FunctionValue equalsMethod = this.getOperatorMethod(Token.Type.EQUALS, 2);
		if (equalsMethod != null) {
			List<Value> parameters = ArucasList.arrayListOf(this, other);
			Value value = equalsMethod.call(context, parameters);
			if (!(value instanceof BooleanValue booleanValue)) {
				throw new RuntimeError("operator '==' must return a boolean", equalsMethod.getPosition(), context);
			}
			return booleanValue.value;
		}

		return this == other;
	}

	@Override
	public boolean isNotEquals(Context context, Value other) throws CodeError {
		return super.isNotEquals(context, other);
	}

	@Override
	public FunctionValue onMemberCall(Context context, String name, List<Value> arguments, ValueRef reference, ISyntax position) throws CodeError {
		// Get the class method
		int size = arguments.size() + 1;
		FunctionValue function = this.getMember(name, size);
		if (function == null) {
			// If null get built in member
			function = context.getMemberFunction(this.getClass(), name, size);

			// We check fields as a last resort
			if (function == null && this.getMember(name) instanceof FunctionValue delegate) {
				return delegate;
			}

			if (function == null) {
				// If still not then we check delegates
				for (AbstractClassDefinition definition : this.value.castAsMap.keySet()) {
					function = definition.getMember(name, size);
					if (function != null) {
						arguments.add(0, this.castAs(context, definition, position));
						return function;
					}
					function = context.getMemberFunction(definition.getValueClass(), name, size);
					if (function != null) {
						arguments.add(0, this.castAs(context, definition, position));
						return function;
					}
					// We check fields as a last resort
					if (definition.hasMemberField(name)) {
						return this.castAs(context, definition, position).onMemberCall(context, name, arguments, reference, position);
					}
				}
			}
		}

		arguments.add(0, this);
		return function;
	}

	@Override
	public Value onMemberAccess(Context context, String name, ISyntax position) throws CodeError {
		Value value = this.getMember(name);
		if (value != null) {
			return value;
		}

		UserDefinedClassFunction function = this.value.getMethods().get(name);
		if (function != null) {
			return function.getDelegate(this);
		}

		for (AbstractClassDefinition definition : this.value.castAsMap.keySet()) {
			if (definition.hasMemberField(name) || definition.getMethods().get(name) != null) {
				return this.value.castAsMap.get(definition).call(context, ArucasList.arrayListOf(this)).onMemberAccess(context, name, position);
			}
		}

		return super.onMemberAccess(context, name, position);
	}

	@Override
	public Value onMemberAssign(Context context, String name, Functions.UniFunction<Context, Value> valueGetter, ISyntax position) throws CodeError {
		if (!this.hasMember(name)) {
			for (AbstractClassDefinition definition : this.value.castAsMap.keySet()) {
				if (definition.hasMemberField(name)) {
					return this.castAs(context, definition, position).onMemberAssign(context, name, valueGetter, position);
				}
			}

			throw new RuntimeError(
				"The member '%s' does not exist for the class '%s'".formatted(name, this.getTypeName()),
				position, context
			);
		}
		Value value = valueGetter.apply(context);
		if (!this.setMember(context, position, name, value)) {
			throw new RuntimeError(
				"The member '%s' cannot be set for the class '%s'".formatted(name, this.getTypeName()),
				position, context
			);
		}
		return value;
	}

	@Override
	public Value bracketAssign(Context context, Value other, Value assignValue, ISyntax syntaxPosition) throws CodeError {
		FunctionValue function = this.getOperatorMethod(Token.Type.SQUARE_BRACKETS, 3);
		if (function != null) {
			return function.call(context, ArucasList.arrayListOf(this, other, assignValue));
		}
		return super.bracketAssign(context, other, assignValue, syntaxPosition);
	}

	@Override
	public String getTypeName() {
		return this.getName();
	}

	@Override
	public final AbstractClassDefinition getDefinition(Context context) {
		return this.value;
	}
}
