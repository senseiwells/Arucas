package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.GenericValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClassMemberFunction extends UserDefinedFunction implements IMemberFunction {
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
	public Value execute(Context context, List<Value> arguments) throws CodeError, ThrowValue {
		// This value is always added as the first parameter
		arguments.add(0, this.thisValue);
		return super.execute(context, arguments);
	}
	
	@Override
	public String getAsString(Context context) throws CodeError {
		return "<class " + this.thisValue.getName() + "::" + this.getName() + "@" + Integer.toHexString(Objects.hashCode(this)) + ">";
	}

	@Override
	public FunctionValue setThisAndGet(Value thisValue) {
		if (thisValue instanceof ArucasClassValue classValue) {
			return this.copy(classValue);
		}
		return null;
	}

	public static final class Arbitrary extends ClassMemberFunction {
		public Arbitrary(ArucasClassValue thisValue, String name, List<String> argumentNames, ISyntax syntaxPosition) {
			super(thisValue, name, argumentNames, syntaxPosition);
		}

		public Arbitrary(String name, List<String> argumentNames, ISyntax syntaxPosition) {
			super(name, argumentNames, syntaxPosition);
		}

		@Override
		public ClassMemberFunction.Arbitrary copy(ArucasClassValue value) {
			ClassMemberFunction.Arbitrary memberFunction = new ClassMemberFunction.Arbitrary(value, this.getName(), this.argumentNames, this.syntaxPosition);
			memberFunction.complete(this.bodyNode);
			return memberFunction;
		}

		@Override
		public int getParameterCount() {
			return -1;
		}

		@Override
		public Value execute(Context context, List<Value> arguments) throws CodeError, ThrowValue {
			ArucasList list = new ArucasList();
			// This can be empty
			if (arguments != null && !arguments.isEmpty()) {
				list.addAll(arguments);
			}

			List<Value> listOfList = new ArrayList<>();
			listOfList.add(new ListValue(list));
			return super.execute(context, listOfList);
		}
	}
}
