package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.impl.ArucasValueListCustom;
import me.senseiwells.arucas.values.Value;

import java.util.List;
import java.util.Objects;

public abstract class FunctionValue extends Value<String> {
	public final List<String> argumentNames;
	public final ISyntax syntaxPosition;
	public final String deprecatedMessage;
	
	public FunctionValue(String name, ISyntax syntaxPosition, List<String> argumentNames, String deprecatedMessage) {
		super(name);
		this.syntaxPosition = syntaxPosition;
		this.argumentNames = argumentNames;
		this.deprecatedMessage = deprecatedMessage;
	}
	
	public String getName() {
		return this.value;
	}
	
	public int getParameterCount() {
		return this.argumentNames.size();
	}
	
	private void checkArguments(Context context, ArucasValueListCustom arguments, List<String> argumentNames) throws CodeError {
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

	private void populateArguments(Context context, ArucasValueListCustom arguments, List<String> argumentNames) {
		for (int i = 0; i < argumentNames.size(); i++) {
			String argumentName = argumentNames.get(i);
			Value<?> argumentValue = arguments.get(i);
			context.setLocal(argumentName, argumentValue);
		}
	}

	public void checkAndPopulateArguments(Context context, ArucasValueListCustom arguments, List<String> argumentNames) throws CodeError {
		this.checkArguments(context, arguments, argumentNames);
		this.populateArguments(context, arguments, argumentNames);
	}

	public CodeError throwInvalidParameterError(String details, Context context) {
		return new RuntimeError(details, this.syntaxPosition, context);
	}

	protected abstract Value<?> execute(Context context, ArucasValueListCustom arguments) throws CodeError, ThrowValue;
	
	/**
	 * API overridable method
	 */
	protected Value<?> callOverride(Context context, ArucasValueListCustom arguments, boolean returnable) throws CodeError {
		context.pushFunctionScope(this.syntaxPosition);
		try {
			Value<?> value = this.execute(context, arguments);
			context.popScope();
			return value;
		}
		catch (ThrowValue.Return tv) {
			if (!returnable) {
				throw new CodeError(
					CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
					tv.getMessage(),
					this.syntaxPosition
				);
			}
			context.moveScope(context.getReturnScope());
			context.popScope();
			return tv.getReturnValue();
		}
		catch (ThrowValue tv) {
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				tv.getMessage(),
				this.syntaxPosition
			);
		}
		catch (StackOverflowError e) {
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				"StackOverflow: Call stack went too deep",
				this.syntaxPosition
			);
		}
	}
	
	public final Value<?> call(Context context, ArucasValueListCustom arguments) throws CodeError {
		return this.callOverride(context, arguments, true);
	}

	public final Value<?> call(Context context, ArucasValueListCustom arguments, boolean returnable) throws CodeError {
		return this.callOverride(context, arguments, returnable);
	}
	
	@Override
	public final FunctionValue copy() {
		return this;
	}
	
	@Override
	public int getHashCode(Context context) throws CodeError {
		return Objects.hash(this.value, this.getParameterCount());
	}
	
	@Override
	public String getStringValue(Context context) throws CodeError {
		return "<function " + this.value + ">";
	}
	
	@Override
	public boolean isEquals(Context context, Value<?> other) {
//		if (other instanceof FunctionValue functionValue) {
//			return this.getParameterCount() == functionValue.getParameterCount() && super.equals(other);
//		}
		
		// The problem here is that it is not enough to check the parameter count and name
		// If this function was a delegate of a class and then we compared it to a delegate
		// of the same class but another instance is should always return false.
		return this == other;
	}
}
