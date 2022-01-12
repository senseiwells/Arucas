package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;

/**
 * This class should only be extended by {@link Value}.
 */
public abstract class BaseValue implements ValueIdentifier {

	/**
	 * This method is just to be able to get the
	 * Value from inside this class
	 */
	protected abstract Object getValue();

	/**
	 * This should return a shallow copy of
	 * an applicable Value
	 */
	public abstract Value<?> copy(Context context) throws CodeError;

	/**
	 * This should return a deep copy
	 * of an applicable Value
	 */
	public abstract Value<?> newCopy(Context context) throws CodeError;

	/**
	 * This gets called when the <code>&&</code> binary operator
	 * is used in Arucas
	 *
	 * @param context The current context
	 * @param other The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return Whether the two values are both true
	 * @throws CodeError If the two values cannot use this operator
	 */
	public BooleanValue isAnd(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "AND", other, syntaxPosition);
	}

	/**
	 * This gets called when the <code>||</code> binary operator
	 * is used in Arucas
	 *
	 * @param context The current context
	 * @param other The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return Whether at least one of the two values is true
	 * @throws CodeError If the two values cannot use this operator
	 */
	public BooleanValue isOr(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "OR", other, syntaxPosition);
	}

	/**
	 * This gets called when the <code>+</code> binary operator
	 * is used in Arucas
	 *
	 * @param context The current context
	 * @param other The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return The value plus another
	 * @throws CodeError If the two values cannot use this operator
	 */
	public Value<?> addTo(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "ADD", other, syntaxPosition);
	}

	/**
	 * This gets called when the <code>-</code> binary operator
	 * is used in Arucas
	 *
	 * @param context The current context
	 * @param other The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return The value subtracted by another
	 * @throws CodeError If the two values cannot use this operator
	 */
	public Value<?> subtractBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "SUBTRACT", other, syntaxPosition);
	}

	/**
	 * This gets called when the <code>-</code> binary operator
	 * is used in Arucas
	 *
	 * @param context The current context
	 * @param other The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return The value multiplied by another
	 * @throws CodeError If the two values cannot use this operator
	 */
	public Value<?> multiplyBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "MULTIPLY", other, syntaxPosition);
	}

	/**
	 * This gets called when the <code>/</code> binary operator
	 * is used in Arucas
	 *
	 * @param context The current context
	 * @param other The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return The value divided by another
	 * @throws CodeError If the two values cannot use this operator
	 */
	public Value<?> divideBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "DIVIDE", other, syntaxPosition);
	}

	/**
	 * This gets called when the <code>^</code> binary operator
	 * is used in Arucas
	 *
	 * @param context The current context
	 * @param other The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return The value to the exponent of another
	 * @throws CodeError If the two values cannot use this operator
	 */
	public Value<?> powerBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "POWER", other, syntaxPosition);
	}

	/**
	 * This gets called when <code>>, >=, <, <=</code> binary operators
	 * is used in Arucas
	 *
	 * @param context The current context
	 * @param other The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return Whether the comparison between the values is true
	 * @throws CodeError If the two values cannot use this operator
	 */
	public BooleanValue compareNumber(Context context, Value<?> other, Token.Type type, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, type.toString(), other, syntaxPosition);
	}

	/**
	 * This gets called when the <code>!</code> unary operators
	 * is used in Arucas
	 *
	 * @param context The current context
	 * @param syntaxPosition The current position
	 * @return The inverted value
	 * @throws CodeError If the value cannot use this operator
	 */
	public BooleanValue not(Context context, ISyntax syntaxPosition) throws CodeError {
		throw new RuntimeError("The operation 'NOT' cannot be applied to %s".formatted(this.getAsString(context)), syntaxPosition, context);
	}

	/**
	 * This gets called when the <code>==</code> binary operator
	 * is used in Arucas
	 *
	 * @param other The other Value you are comparing
	 * @return Whether the two values are equal
	 */
	public BooleanValue isEqualTo(Value<?> other) {
		return BooleanValue.of(this.equals(other));
	}

	/**
	 * This gets called when the <code>==</code> binary operator
	 * is used in Arucas
	 *
	 * @param other The other Value you are comparing
	 * @return Whether the two values are not equal
	 */
	public BooleanValue isNotEqualTo(Value<?> other) {
		return this.isEqualTo(other).not();
	}

	private RuntimeError cannotApplyError(Context context, String operation, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		return new RuntimeError("The operation '%s' cannot be applied to %s and %s".formatted(
			operation,
			this.getAsString(context),
			other.getAsString(context)),
			syntaxPosition,
			context
		);
	}

	/**
	 * This method should not be used, instead
	 * {@link #isEquals(Context, Value)}
	 * should be used.
	 *
	 * This cannot do reference check only,
	 * Parser#switchStatement() would break.
	 */
	@Deprecated
	@Override
	public final boolean equals(Object other) {
		if (other instanceof Value<?> otherValue) {
			return this.getValue().equals(otherValue.getValue());
		}
		return false;
	}

	/**
	 * This method should not be used, instead
	 * {@link #getHashCode(Context)}
	 * should be used.
	 */
	@Deprecated
	@Override
	public final int hashCode() {
		return this.getValue().hashCode();
	}

	/**
	 * This method should not be used, instead
	 * {@link #getAsString(Context)},
	 * should be used.
	 */
	@Deprecated
	@Override
	public final String toString() {
		return this.getValue() == null ? "null" : this.getValue().toString();
	}
}
