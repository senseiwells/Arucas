package me.senseiwells.arucas.values.functions.mod;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.arucas.values.GenericValue;
import me.senseiwells.arucas.values.Value;

import java.util.List;
import java.util.Objects;

import static me.senseiwells.arucas.utils.ValueTypes.FUNCTION;

public abstract class FunctionValue extends GenericValue<String> {
	private final ISyntax syntaxPosition;
	private final String deprecationMessage;
	private final int parameters;

	protected FunctionValue(String name, ISyntax position, int parameters, String deprecationMessage) {
		super(name);
		this.syntaxPosition = position;
		this.deprecationMessage = deprecationMessage;
		this.parameters = parameters;
	}

	public String getName() {
		return this.value;
	}

	public int getCount() {
		return this.parameters;
	}

	public String getDeprecationMessage() {
		return this.deprecationMessage;
	}

	public ISyntax getPosition() {
		return this.syntaxPosition;
	}

	public RuntimeError getError(Context context, String details) {
		return new RuntimeError(details, this.syntaxPosition, context);
	}

	public RuntimeError getError(Context context, String details, Object... objects) {
		return new RuntimeError(details.formatted(objects), this.syntaxPosition, context);
	}

	protected abstract Value execute(Context context, List<Value> arguments) throws CodeError;

	public final Value callSafe(Context context, ExceptionUtils.ThrowableSupplier<List<Value>> arguments) {
		try {
			return this.call(context, arguments.get());
		}
		catch (Throwable throwable) {
			context.getThreadHandler().tryError(context, throwable);
			return null;
		}
	}

	public final Value call(Context context, List<Value> arguments) throws CodeError {
		return this.call(context, arguments, true);
	}

	public Value call(Context context, List<Value> arguments, boolean returnable) throws CodeError {
		context.pushFunctionScope(this.syntaxPosition);
		try {
			Value value = this.execute(context, arguments);
			context.popScope();
			return value;
		}
		catch (ThrowValue.Return throwValue) {
			if (!returnable) {
				throw this.getError(context, throwValue.getMessage());
			}
			context.moveScope(context.getReturnScope());
			context.popScope();
			return throwValue.getReturnValue();
		}
		catch (RuntimeError runtimeError) {
			runtimeError.setContext(context);
			throw runtimeError;
		}
		catch (RuntimeException e) {
			throw this.getError(context, e.getMessage());
		}
		catch (StackOverflowError e) {
			throw this.getError(context, "StackOverflow: Call stack went too deep");
		}
	}

	@Override
	public GenericValue<String> copy(Context context) throws CodeError {
		return this;
	}

	@Override
	public Object asJavaValue() {
		return this;
	}

	@Override
	public boolean isEquals(Context context, Value other) throws CodeError {
		return this == other;
	}

	@Override
	public String getTypeName() {
		return FUNCTION;
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return "<function - " + this.getName() + ">";
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		return Objects.hash(this.getName(), this.getCount());
	}
}
