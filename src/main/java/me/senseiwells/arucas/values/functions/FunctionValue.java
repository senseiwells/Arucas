package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public abstract class FunctionValue extends Value<String> {
	public final List<String> argumentNames;
	public final ISyntax syntaxPosition;
	public final boolean isDeprecated;
	
	public FunctionValue(String name, ISyntax syntaxPosition, List<String> argumentNames, boolean isDeprecated) {
		super(name);
		this.syntaxPosition = syntaxPosition;
		this.argumentNames = argumentNames;
		this.isDeprecated = isDeprecated;
	}
	
	private void checkArguments(Context context, List<Value<?>> arguments, List<String> argumentNames) throws CodeError {
		int argumentSize = arguments == null ? 0 : arguments.size();
		if (argumentSize > argumentNames.size()) {
			throw new RuntimeError(
				"%s too many arguments passed into %s".formatted(arguments.size() - argumentNames.size(), this.value),
				this.syntaxPosition,
				context
			);
		}
		if (argumentSize < argumentNames.size()) {
			throw new RuntimeError(
				"%s too few arguments passed into %s".formatted(argumentNames.size() - argumentSize, this.value),
				this.syntaxPosition,
				context
			);
		}
	}

	private void populateArguments(Context context, List<Value<?>> arguments, List<String> argumentNames) {
		for (int i = 0; i < argumentNames.size(); i++) {
			String argumentName = argumentNames.get(i);
			Value<?> argumentValue = arguments.get(i);
			context.setLocal(argumentName, argumentValue);
		}
	}

	public void checkAndPopulateArguments(Context context, List<Value<?>> arguments, List<String> argumentNames) throws CodeError {
		this.checkArguments(context, arguments, argumentNames);
		this.populateArguments(context, arguments, argumentNames);
	}

	public CodeError throwInvalidParameterError(String details, Context context) {
		return new RuntimeError(details, this.syntaxPosition, context);
	}

	protected abstract Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError, ThrowValue;
	
	public final Value<?> call(Context context, List<Value<?>> arguments) throws CodeError, ThrowValue {
		context.pushFunctionScope(this.syntaxPosition);
		try {
			Value<?> value = this.execute(context, arguments);
			context.popScope();
			return value;
		}
		catch (ThrowValue.Return tv) {
			context.moveScope(context.getReturnScope());
			context.popScope();
			return tv.returnValue;
		}
		catch (StackOverflowError e) {
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				"StackOverflow: Call stack went too deep",
				this.syntaxPosition
			);
		}
	}
	
	@Override
	public final FunctionValue copy() {
		return this;
	}

	@Override
	public String toString() {
		return "<function %s>".formatted(this.value);
	}
}
