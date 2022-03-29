package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassValue;

import java.util.List;
import java.util.Objects;

public class ClassMemberFunction extends UserDefinedFunction {
	protected ArucasClassValue thisValue;
	
	public ClassMemberFunction(ArucasClassValue thisValue, String name, List<String> argumentNames, ISyntax syntaxPosition) {
		super(name, argumentNames, syntaxPosition);
		this.thisValue = thisValue;
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
		arguments.add(0, this.thisValue);
		return super.execute(context, arguments);
	}
	
	@Override
	public String getAsString(Context context) throws CodeError {
		return "<class " + this.thisValue.getName() + "::" + this.getName() + "@" + Integer.toHexString(Objects.hashCode(this)) + ">";
	}
}
