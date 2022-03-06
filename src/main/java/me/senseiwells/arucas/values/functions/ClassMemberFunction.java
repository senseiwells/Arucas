package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.classes.ArucasClassValue;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ClassMemberFunction extends UserDefinedFunction implements EmbeddableFunction {
	private Supplier<Value<?>> thisSupplier;
	private AbstractClassDefinition definition;
	protected ArucasClassValue thisValue;
	
	public ClassMemberFunction(ArucasClassValue thisValue, String name, List<String> argumentNames, ISyntax syntaxPosition) {
		super(name, argumentNames, syntaxPosition);
		this.thisValue = thisValue;
		this.definition = this.thisValue == null ? null : this.thisValue.value;
	}
	
	public ClassMemberFunction(String name, List<String> argumentNames, ISyntax syntaxPosition) {
		this(null, name, argumentNames, syntaxPosition);
	}
	
	public ClassMemberFunction copy(ArucasClassValue value) {
		ClassMemberFunction memberFunction = new ClassMemberFunction(value, this.getName(), this.argumentNames, this.syntaxPosition);
		memberFunction.complete(this.bodyNode);
		return memberFunction;
	}
	
	@Override
	public Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError, ThrowValue {
		// This value is always added as the first parameter
		ArucasClassValue thisValue = this.thisValue;
		if (this.thisSupplier != null) {
			Value<?> value = this.thisSupplier.get();
			if (!(value instanceof ArucasClassValue classValue) || classValue.value != this.definition) {
				throw new RuntimeError("Embedded member was not of type '%s'".formatted(this.definition.getName()), this.syntaxPosition, context);
			}
			thisValue = classValue;
		}

		arguments.add(0, thisValue);
		return super.execute(context, arguments);
	}
	
	@Override
	public String getAsString(Context context) throws CodeError {
		return "<class " + this.thisValue.getName() + "::" + this.getName() + "@" + Integer.toHexString(Objects.hashCode(this)) + ">";
	}

	@Override
	public void setCallingMember(Supplier<Value<?>> supplier) {
		if (this.thisSupplier == null) {
			this.thisSupplier = supplier;
		}
	}

	@Override
	public void setDefinition(AbstractClassDefinition definition) {
		if (this.definition == null) {
			this.definition = definition;
		}
	}
}
