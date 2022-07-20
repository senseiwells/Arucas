package me.senseiwells.arucas.extensions.util;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.BuiltInException;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.Functions;
import me.senseiwells.arucas.utils.LazyGetter;
import me.senseiwells.arucas.utils.ValueRef;
import me.senseiwells.arucas.utils.impl.IArucasCollection;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class LazyValue extends Value {
	protected Value value;

	protected LazyValue() { }

	public static LazyValue of(LazyGetter getter) {
		return new Getter(getter);
	}

	public static LazyValue of(Context context, CompletableFuture<Value> completableFuture) {
		return new Future(completableFuture, context);
	}

	public boolean isReady() {
		return true;
	}

	public abstract Value get();

	@Override
	public Object getValue() {
		return this.get().getValue();
	}

	@Override
	public Value copy(Context context) throws CodeError {
		return this.get().copy(context);
	}

	@Override
	public Value newCopy(Context context) throws CodeError {
		return this.get().newCopy(context);
	}

	@Override
	public Value castAs(Context context, AbstractClassDefinition definition, ISyntax position) throws CodeError {
		return this.get().castAs(context, definition, position);
	}

	@Override
	public <T extends Value> T castAs(Context context, Class<T> clazz, ISyntax position) {
		return this.get().castAs(context, clazz, position);
	}

	@Override
	public Object asJavaValue() {
		return this.get().asJavaValue();
	}

	@Override
	public boolean isCollection() {
		return this.get().isCollection();
	}

	@Override
	public IArucasCollection asCollection(Context context, ISyntax syntaxPosition) throws CodeError {
		return this.get().asCollection(context, syntaxPosition);
	}

	@Override
	public Value onUnaryOperation(Context context, Token.Type type, ISyntax syntaxPosition) throws CodeError {
		return this.get().onUnaryOperation(context, type, syntaxPosition);
	}

	@Override
	public Value onBinaryOperation(Context context, Functions.UniFunction<Context, Value> valueGetter, Token.Type type, ISyntax syntaxPosition) throws CodeError {
		return this.get().onBinaryOperation(context, valueGetter, type, syntaxPosition);
	}

	@Override
	public BooleanValue isAnd(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		return this.get().isAnd(context, other, syntaxPosition);
	}

	@Override
	public BooleanValue isOr(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		return this.get().isOr(context, other, syntaxPosition);
	}

	@Override
	public Value xor(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		return this.get().xor(context, other, syntaxPosition);
	}

	@Override
	public Value addTo(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		return this.get().addTo(context, other, syntaxPosition);
	}

	@Override
	public Value subtractBy(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		return this.get().subtractBy(context, other, syntaxPosition);
	}

	@Override
	public Value multiplyBy(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		return this.get().multiplyBy(context, other, syntaxPosition);
	}

	@Override
	public Value divideBy(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		return this.get().divideBy(context, other, syntaxPosition);
	}

	@Override
	public Value powerBy(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		return this.get().powerBy(context, other, syntaxPosition);
	}

	@Override
	public Value compareNumber(Context context, Value other, Token.Type type, ISyntax syntaxPosition) throws CodeError {
		return this.get().compareNumber(context, other, type, syntaxPosition);
	}

	@Override
	public Value shiftLeft(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		return this.get().shiftLeft(context, other, syntaxPosition);
	}

	@Override
	public Value shiftRight(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		return this.get().shiftRight(context, other, syntaxPosition);
	}

	@Override
	public Value bitAnd(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		return this.get().bitAnd(context, other, syntaxPosition);
	}

	@Override
	public Value bitOr(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		return this.get().bitOr(context, other, syntaxPosition);
	}

	@Override
	public Value not(Context context, ISyntax syntaxPosition) throws CodeError {
		return this.get().not(context, syntaxPosition);
	}

	@Override
	public Value unaryPlus(Context context, ISyntax syntaxPosition) throws CodeError {
		return this.get().unaryPlus(context, syntaxPosition);
	}

	@Override
	public Value unaryMinus(Context context, ISyntax syntaxPosition) throws CodeError {
		return this.get().unaryMinus(context, syntaxPosition);
	}

	@Override
	public String getAsString(Context context) throws CodeError {
		return this.get().getAsString(context);
	}

	@Override
	public int getHashCode(Context context) throws CodeError {
		return this.get().getHashCode(context);
	}

	@Override
	public boolean isEquals(Context context, Value other) throws CodeError {
		return this.get().isEquals(context, other);
	}

	@Override
	public boolean isNotEquals(Context context, Value other) throws CodeError {
		return this.get().isNotEquals(context, other);
	}

	@Override
	public FunctionValue onMemberCall(Context context, String name, List<Value> arguments, ValueRef reference, ISyntax position) throws CodeError {
		if (name.equals("isReady") && arguments.isEmpty()) {
			reference.set(BooleanValue.of(this.isReady()));
			return null;
		}
		return this.get().onMemberCall(context, name, arguments, reference, position);
	}

	@Override
	public Value onMemberAccess(Context context, String name, ISyntax position) throws CodeError {
		return this.get().onMemberAccess(context, name, position);
	}

	@Override
	public Value onMemberAssign(Context context, String name, Functions.UniFunction<Context, Value> valueGetter, ISyntax position) throws CodeError {
		return this.get().onMemberAssign(context, name, valueGetter, position);
	}

	@Override
	public Value bracketAccess(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		return this.get().bracketAccess(context, other, syntaxPosition);
	}

	@Override
	public Value bracketAssign(Context context, Value other, Value assignValue, ISyntax syntaxPosition) throws CodeError {
		return this.get().bracketAssign(context, other, assignValue, syntaxPosition);
	}

	@Override
	public String getTypeName() {
		return this.get().getTypeName();
	}

	@Override
	public AbstractClassDefinition getDefinition(Context context) {
		return this.get().getDefinition(context);
	}

	private static class Future extends LazyValue {
		private CompletableFuture<Value> future;
		private Context context;

		private Future(CompletableFuture<Value> future, Context context) {
			this.future = future;
			this.context = context;
		}

		@Override
		public boolean isReady() {
			return this.future.isDone();
		}

		@Override
		public Value get() {
			if (this.value != null) {
				return this.value;
			}
			RuntimeError error;
			try {
				Value futureValue = this.future.get();
				if (futureValue != null) {
					if (futureValue == this) {
						throw new BuiltInException("LazyValue referenced itself");
					}
					this.future = null;
					this.context = null;
					return this.value = futureValue;
				}
				error = new RuntimeError("Failed to evaluate lazy value", this.context);
			}
			catch (InterruptedException | ExecutionException e) {
				error = new RuntimeError("LazyValue: " + CodeError.throwableToString(e), this.context);
			}
			this.context.getThreadHandler().tryError(this.context, error);
			return NullValue.NULL;
		}
	}

	private static class Getter extends LazyValue {
		private LazyGetter getter;

		private Getter(LazyGetter getter) {
			this.getter = getter;
		}

		@Override
		public Value get() {
			if (this.value == null) {
				Value value = this.getter.get();
				if (value == this) {
					throw new BuiltInException("LazyValue referenced itself");
				}
				this.value = value;
				this.getter = null;
			}
			return this.value;
		}
	}
}
