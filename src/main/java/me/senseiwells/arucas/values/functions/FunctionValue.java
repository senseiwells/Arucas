package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ErrorRuntime;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.SymbolTable;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public abstract class FunctionValue extends Value<String> {
	public FunctionValue(String name) {
		super(name);
	}
	
	private void checkArguments(List<Value<?>> arguments, List<String> argumentNames) throws ErrorRuntime {
		int argumentSize = arguments == null ? 0 : arguments.size();
		if (argumentSize > argumentNames.size())
			throw new ErrorRuntime(arguments.size() - argumentNames.size() + " too many arguments passed into " + this.value, this.startPos, this.endPos, this.context);
		if (argumentSize < argumentNames.size())
			throw new ErrorRuntime(argumentNames.size() - argumentSize + " too few arguments passed into " + this.value, this.startPos, this.endPos, this.context);
	}

	private void populateArguments(List<Value<?>> arguments, List<String> argumentNames, Context context) {
		for (int i = 0; i < argumentNames.size(); i++) {
			String argumentName = argumentNames.get(i);
			Value<?> argumentValue = arguments.get(i);
			argumentValue.setContext(context);
			context.setVariable(argumentName, argumentValue);
		}
	}

	public void checkAndPopulateArguments(List<Value<?>> arguments, List<String> argumentNames, Context context) throws ErrorRuntime {
		this.checkArguments(arguments, argumentNames);
		this.populateArguments(arguments, argumentNames, context);
	}

	public Value<?> getValueFromTable(String key) {
		return this.context.getVariable(key);
	}

	public CodeError throwInvalidParameterError(String details) {
		return new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, details, this.startPos, this.endPos);
	}

	protected abstract Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError, ThrowValue;
	
	public final Value<?> call(Context context, List<Value<?>> arguments) throws CodeError, ThrowValue {
		context.pushFunctionScope(this.startPos);
		try {
			Value<?> value = execute(context, arguments);
			context.popScope();
			return value;
		}
		catch (ThrowValue tv) {
			context.moveScope(context.symbolTable.getReturnScope());
			context.popScope();
			return tv.returnValue;
		}
	}
	
	@Override
	public abstract Value<?> copy();

	@Override
	public String toString() {
		return "<function " + this.value + ">";
	}
}
