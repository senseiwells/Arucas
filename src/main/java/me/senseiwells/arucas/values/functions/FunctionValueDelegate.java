package me.senseiwells.arucas.values.functions;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.Value;

import java.util.List;

public class FunctionValueDelegate extends FunctionValue {
	private FunctionValue delegate;
	
	public FunctionValueDelegate() {
		super("$function-delegate");
	}
	
	public void setDelegate(FunctionValue value) {
		this.delegate = value;
	}
	
	@Override
	protected Value<?> execute(Context context, List<Value<?>> arguments) throws CodeError, ThrowValue {
		return this.delegate.execute(context, arguments);
	}
	
	@Override
	public Value<?> copy() {
		return this.delegate.copy();
	}
	
	@Override
	public String toString() {
		return this.delegate.toString();
	}
}
