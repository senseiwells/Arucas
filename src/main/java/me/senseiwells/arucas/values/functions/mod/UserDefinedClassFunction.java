package me.senseiwells.arucas.values.functions.mod;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.values.ListValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.ArucasClassValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserDefinedClassFunction extends UserDefinedFunction implements IMemberFunction {
	protected ArucasClassValue thisValue;

	public UserDefinedClassFunction(ArucasClassValue thisValue, String name, List<String> argumentNames, ISyntax position) {
		super(name, argumentNames, position);
		this.thisValue = thisValue;
	}

	public UserDefinedClassFunction(String name, List<String> argumentNames, ISyntax syntaxPosition) {
		super(name, argumentNames, syntaxPosition);
	}

	public UserDefinedClassFunction copy(ArucasClassValue value) {
		UserDefinedClassFunction memberFunction = new UserDefinedClassFunction(value, this.getName(), this.argumentNames, this.getPosition());
		memberFunction.complete(this.bodyNode);
		return memberFunction;
	}

	@Override
	protected Value execute(Context context, List<Value> arguments) throws CodeError {
		arguments.add(0, this.thisValue);
		return super.execute(context, arguments);
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return "<class " + this.thisValue.getName() + "::" + this.getName() + "@" + Integer.toHexString(Objects.hashCode(this)) + ">";
	}

	@Override
	public UserDefinedClassFunction setThisAndGet(Value thisValue) {
		if (thisValue instanceof ArucasClassValue classValue) {
			return this.copy(classValue);
		}
		return null;
	}

	public static final class Arbitrary extends UserDefinedClassFunction {
		private Arbitrary(ArucasClassValue thisValue, String name, List<String> argumentNames, ISyntax position) {
			super(thisValue, name, argumentNames, position);
		}

		public Arbitrary(String name, List<String> argumentNames, ISyntax syntaxPosition) {
			super(name, argumentNames, syntaxPosition);
		}

		@Override
		public UserDefinedClassFunction.Arbitrary copy(ArucasClassValue value) {
			UserDefinedClassFunction.Arbitrary member = new UserDefinedClassFunction.Arbitrary(
				value, this.getName(), this.argumentNames, this.getPosition()
			);
			member.complete(this.bodyNode);
			return member;
		}

		@Override
		public int getCount() {
			return -1;
		}

		@Override
		protected Value execute(Context context, List<Value> arguments) throws CodeError {
			ArucasList list = new ArucasList();
			// This can be empty
			if (!arguments.isEmpty()) {
				list.addAll(arguments);
			}

			List<Value> listOfList = new ArrayList<>();
			listOfList.add(new ListValue(list));
			return super.execute(context, listOfList);
		}
	}
}
