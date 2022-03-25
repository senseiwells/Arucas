package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;

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
	 * This gets called when the binary operator <code>&&</code>
	 * is used in Arucas
	 *
	 * @param context        The current context
	 * @param other          The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return Whether the two values are both true
	 * @throws CodeError If the two values cannot use this operator
	 */
	public BooleanValue isAnd(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "AND", other, syntaxPosition);
	}

	/**
	 * This gets called when the binary operator <code>||</code>
	 * is used in Arucas
	 *
	 * @param context        The current context
	 * @param other          The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return Whether at least one of the two values is true
	 * @throws CodeError If the two values cannot use this operator
	 */
	public BooleanValue isOr(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "OR", other, syntaxPosition);
	}

	/**
	 * This gets called when the binary operator <code>+</code>
	 * is used in Arucas
	 *
	 * @param context        The current context
	 * @param other          The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return The value plus another
	 * @throws CodeError If the two values cannot use this operator
	 */
	public Value<?> addTo(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "ADD", other, syntaxPosition);
	}

	/**
	 * This gets called when the binary operator <code>-</code>
	 * is used in Arucas
	 *
	 * @param context        The current context
	 * @param other          The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return The value subtracted by another
	 * @throws CodeError If the two values cannot use this operator
	 */
	public Value<?> subtractBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "SUBTRACT", other, syntaxPosition);
	}

	/**
	 * This gets called when the binary operator <code>*</code>
	 * is used in Arucas
	 *
	 * @param context        The current context
	 * @param other          The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return The value multiplied by another
	 * @throws CodeError If the two values cannot use this operator
	 */
	public Value<?> multiplyBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "MULTIPLY", other, syntaxPosition);
	}

	/**
	 * This gets called when the binary operator <code>/</code>
	 * is used in Arucas
	 *
	 * @param context        The current context
	 * @param other          The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return The value divided by another
	 * @throws CodeError If the two values cannot use this operator
	 */
	public Value<?> divideBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "DIVIDE", other, syntaxPosition);
	}

	/**
	 * This gets called when the binary operator <code>^</code>
	 * is used in Arucas
	 *
	 * @param context        The current context
	 * @param other          The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return The value to the exponent of another
	 * @throws CodeError If the two values cannot use this operator
	 */
	public Value<?> powerBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "POWER", other, syntaxPosition);
	}

	/**
	 * This gets called when binary operators <code>>, >=, <, <=</code>
	 * is used in Arucas
	 *
	 * @param context        The current context
	 * @param other          The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return Whether the comparison between the values is true
	 * @throws CodeError If the two values cannot use this operator
	 */
	public BooleanValue compareNumber(Context context, Value<?> other, Token.Type type, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, type.toString(), other, syntaxPosition);
	}

	/**
	 * This gets called when the unary operator <code>!</code>
	 * is used in Arucas
	 *
	 * @param context        The current context
	 * @param syntaxPosition The current position
	 * @return The inverted value
	 * @throws CodeError If the value cannot use this operator
	 */
	public BooleanValue not(Context context, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "NOT", syntaxPosition);
	}

	/**
	 * This gets called when the unary operator <code>+</code>
	 * is used in Arucas
	 *
	 * @param context        The current context
	 * @param syntaxPosition The current position
	 * @return The unary positive of the value
	 * @throws CodeError If the value cannot use this operator
	 */
	public Value<?> unaryPlus(Context context, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "POSITIVE", syntaxPosition);
	}

	/**
	 * This gets called when the unary operator <code>-</code>
	 * is used in Arucas
	 *
	 * @param context        The current context
	 * @param syntaxPosition The current position
	 * @return The unary negative of the value
	 * @throws CodeError If the value cannot use this operator
	 */
	public Value<?> unaryMinus(Context context, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "NEGATIVE", syntaxPosition);
	}

	/**
	 * This gets called when the binary operator <code>==</code>
	 * is used in Arucas
	 *
	 * @param other The other Value you are comparing
	 * @return Whether the two values are equal
	 */
	@Override
	public abstract boolean isEquals(Context context, Value<?> other) throws CodeError;

	/**
	 * This gets called when the binary operator <code>!=</code>
	 * is used in Arucas
	 *
	 * @param other The other Value you are comparing
	 * @return Whether the two values are not equal
	 */
	@Override
	public boolean isNotEquals(Context context, Value<?> other) throws CodeError {
		return ValueIdentifier.super.isNotEquals(context, other);
	}

	/**
	 * This gets the name of the class, this is important to get the
	 * class definition of the value to be able to get the {@link TypeValue}.
	 * This should never be null
	 *
	 * @return The name of the type
	 */
	public abstract String getTypeName();

	public TypeValue getType(Context context, ISyntax syntaxPosition) throws CodeError {
		AbstractClassDefinition definition = context.getClassDefinition(this.getTypeName());
		if (definition == null) {
			throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Value has no type", syntaxPosition);
		}
		return definition.getType();
	}

	private RuntimeError cannotApplyError(Context context, String operation, ISyntax syntaxPosition) throws CodeError {
		return new RuntimeError("The operation '%s' cannot be applied to %s".formatted(
			operation,
			this.getAsString(context)),
			syntaxPosition,
			context
		);
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
	 * should be used
	 */
	@Deprecated
	@Override
	public final boolean equals(Object other) {
		return this == other;
	}

	/**
	 * This method should not be used, instead
	 * {@link #getHashCode(Context)}
	 * should be used
	 */
	@Deprecated
	@Override
	public final int hashCode() {
		return this.getValue() == null ? 0 : this.getValue().hashCode();
	}

	/**
	 * This method should not be used, instead
	 * {@link #getAsString(Context)},
	 * should be used
	 */
	@Deprecated
	@Override
	public final String toString() {
		return this.getValue() == null ? "null" : this.getValue().toString();
	}
}
