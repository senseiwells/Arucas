package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.Functions;
import me.senseiwells.arucas.utils.ValueRef;
import me.senseiwells.arucas.utils.impl.IArucasCollection;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.functions.Delegatable;
import me.senseiwells.arucas.values.functions.FunctionValue;

import java.util.List;

/**
 * This class should only be extended by {@link GenericValue}.
 */
public abstract class Value implements ValueIdentifier {

	/**
	 * This method is just to be able to get the
	 * Value from inside this class
	 */
	public abstract Object getValue();

	/**
	 * This should return a shallow copy of
	 * an applicable Value
	 */
	public abstract Value copy(Context context) throws CodeError;

	/**
	 * This should return a deep copy
	 * of an applicable Value
	 */
	public abstract Value newCopy(Context context) throws CodeError;

	/**
	 * This returns the Java equivalent of the
	 * value, most of the time this is the wrapped value
	 */
	public Object asJavaValue() {
		return this.getValue();
	}

	/**
	 * This returns whether the value is a collection
	 */
	public boolean isCollection() {
		return false;
	}

	/**
	 * This gets the value as a collection
	 *
	 * @param context        The current context
	 * @param syntaxPosition The current position
	 * @return The value as a collection
	 * @throws CodeError If the value is not a collection
	 */
	public IArucasCollection asCollection(Context context, ISyntax syntaxPosition) throws CodeError {
		throw new RuntimeError("'%s' is not a collection".formatted(this.getAsString(context)), syntaxPosition, context);
	}

	/**
	 * This gets called when a unary operator is used on the
	 * value, it provides the type of unary operation
	 *
	 * @param context        The current context
	 * @param type           The type of operator
	 * @param syntaxPosition The current position
	 * @return The result of the operation
	 * @throws CodeError If the value cannot use the operator
	 */
	public Value onUnaryOperation(Context context, Token.Type type, ISyntax syntaxPosition) throws CodeError {
		return switch (type) {
			case NOT -> this.not(context, syntaxPosition);
			case MINUS -> this.unaryMinus(context, syntaxPosition);
			case PLUS -> this.unaryPlus(context, syntaxPosition);
			default -> throw new RuntimeError("Unknown operator '%s'".formatted(type), syntaxPosition, context);
		};
	}

	/**
	 * This gets called when a binary operator is used on the
	 * value, it provides a getter function for the right value
	 *
	 * @param context        The current context
	 * @param valueGetter    Getter for the right value
	 * @param type           The type of operation
	 * @param syntaxPosition The current position
	 * @return The result of the operation
	 * @throws CodeError If the value cannot use the operator
	 */
	public Value onBinaryOperation(Context context, Functions.UniFunction<Context, Value> valueGetter, Token.Type type, ISyntax syntaxPosition) throws CodeError {
		return this.onBinaryOperation(context, valueGetter.apply(context), type, syntaxPosition);
	}

	/**
	 * @see #onBinaryOperation(Context, Functions.UniFunction, Token.Type, ISyntax)
	 */
	protected Value onBinaryOperation(Context context, Value other, Token.Type type, ISyntax syntaxPosition) throws CodeError {
		return switch (type) {
			case PLUS -> this.addTo(context, other, syntaxPosition);
			case MINUS -> this.subtractBy(context, other, syntaxPosition);
			case MULTIPLY -> this.multiplyBy(context, other, syntaxPosition);
			case DIVIDE -> this.divideBy(context, other, syntaxPosition);
			case POWER -> this.powerBy(context, other, syntaxPosition);
			case AND -> this.isAnd(context, other, syntaxPosition);
			case OR -> this.isOr(context, other, syntaxPosition);
			case BIT_AND -> this.bitAnd(context, other, syntaxPosition);
			case BIT_OR -> this.bitOr(context, other, syntaxPosition);
			case XOR -> this.xor(context, other, syntaxPosition);
			case SHIFT_LEFT -> this.shiftLeft(context, other, syntaxPosition);
			case SHIFT_RIGHT -> this.shiftRight(context, other, syntaxPosition);
			case SQUARE_BRACKETS -> this.bracketAccess(context, other, syntaxPosition);
			case EQUALS -> BooleanValue.of(this.isEquals(context, other));
			case NOT_EQUALS -> BooleanValue.of(this.isNotEquals(context, other));
			case LESS_THAN, LESS_THAN_EQUAL, MORE_THAN, MORE_THAN_EQUAL -> this.compareNumber(context, other, type, syntaxPosition);
			default -> throw new RuntimeError("Unknown operator '%s'".formatted(type), syntaxPosition, context);
		};
	}

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
	public BooleanValue isAnd(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
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
	public BooleanValue isOr(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "OR", other, syntaxPosition);
	}

	/**
	 * This gets called when the binary operator <code>~</code>
	 *
	 * @param context        The current context
	 * @param other          The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return Whether exclusively one of the two values is true
	 * @throws CodeError If the two values cannot use this operator
	 */
	public Value xor(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "XOR", other, syntaxPosition);
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
	public Value addTo(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
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
	public Value subtractBy(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
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
	public Value multiplyBy(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
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
	public Value divideBy(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
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
	public Value powerBy(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
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
	public Value compareNumber(Context context, Value other, Token.Type type, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, type.toString(), other, syntaxPosition);
	}

	/**
	 * This gets called when binary operator <code><<</code>
	 * is used in Arucas
	 *
	 * @param context        The current context
	 * @param other          How far you are shifting
	 * @param syntaxPosition The current position
	 * @return The shifted value
	 * @throws CodeError If the two values cannot use this operator
	 */
	public Value shiftLeft(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "SHIFT_LEFT", other, syntaxPosition);
	}

	/**
	 * This gets called when binary operator <code>>></code>
	 * is used in Arucas
	 *
	 * @param context        The current context
	 * @param other          How far you are shifting
	 * @param syntaxPosition The current position
	 * @return The shifted value
	 * @throws CodeError If the two values cannot use this operator
	 */
	public Value shiftRight(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "SHIFT_RIGHT", other, syntaxPosition);
	}

	/**
	 * This gets called when binary operator <code>&</code>
	 *
	 * @param context        The current context
	 * @param other          The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return The bitwise AND of the two values
	 * @throws CodeError If the two values cannot use this operator
	 */
	public Value bitAnd(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "BIT_AND", other, syntaxPosition);
	}

	/**
	 * This gets called when binary operator <code>|</code>
	 *
	 * @param context        The current context
	 * @param other          The other Value you are comparing
	 * @param syntaxPosition The current position
	 * @return The bitwise OR of the two values
	 * @throws CodeError If the two values cannot use this operator
	 */
	public Value bitOr(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "BIT_OR", other, syntaxPosition);
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
	public Value not(Context context, ISyntax syntaxPosition) throws CodeError {
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
	public Value unaryPlus(Context context, ISyntax syntaxPosition) throws CodeError {
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
	public Value unaryMinus(Context context, ISyntax syntaxPosition) throws CodeError {
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
	public abstract boolean isEquals(Context context, Value other) throws CodeError;

	/**
	 * This gets called when the binary operator <code>!=</code>
	 * is used in Arucas
	 *
	 * @param other The other Value you are comparing
	 * @return Whether the two values are not equal
	 */
	@Override
	public boolean isNotEquals(Context context, Value other) throws CodeError {
		return ValueIdentifier.super.isNotEquals(context, other);
	}

	/**
	 * This is called when a value calls a member on itself
	 *
	 * @param context   The current context
	 * @param name      The name of the member it's calling
	 * @param arguments The arguments that are being passed into the function
	 * @param reference A reference that is null, if the reference is not null
	 *                  after then function has returns that reference will be
	 *                  used instead of the normal function return value
	 * @param position  The current syntax position
	 * @return The function value to be called
	 */
	public FunctionValue onMemberCall(Context context, String name, List<Value> arguments, ValueRef reference, ISyntax position) throws CodeError {
		arguments.add(0, this);
		return context.getMemberFunction(this.getClass(), name, arguments.size());
	}

	/**
	 * This is called when a value access a member on itself
	 *
	 * @param context  The current context
	 * @param name     The name of the member it's accessing
	 * @param position The current syntax position
	 * @return The value that is being accessed which may be null
	 */
	public Value onMemberAccess(Context context, String name, ISyntax position) {
		// Get delegate if method exists
		Value value = context.getMemberFunction(this.getClass(), name, -2);

		// We must set the value now if it is a member function
		if (value instanceof Delegatable function) {
			return function.getDelegate(this);
		}

		return null;
	}

	/**
	 * This is called when a value assigns a member on itself
	 *
	 * @param context  The current context
	 * @param name     The name of the member it's assigning
	 * @param position The current syntax position
	 * @return The value that it was just assigned
	 * @throws RuntimeError if the value cannot assign a member
	 */
	public Value onMemberAssign(Context context, String name, Functions.UniFunction<Context, Value> valueGetter, ISyntax position) throws CodeError {
		throw new RuntimeError("You can only assign values to class member values", position, context);
	}

	/**
	 * This gets called when the binary operator <code>[...]</code>
	 * is used in Arucas
	 *
	 * @param context        The current context
	 * @param other          The access value
	 * @param syntaxPosition The current position
	 * @return The access value
	 * @throws CodeError If the values cannot use this operator
	 */
	public Value bracketAccess(Context context, Value other, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "BRACKET_ACCESS", other, syntaxPosition);
	}

	/**
	 * This gets called when the binary operator <code>[...]</code>
	 * is used in Arucas to assign a value with a value
	 *
	 * @param context        The current context
	 * @param other          The access value
	 * @param assignValue    The value to assign
	 * @param syntaxPosition The current position
	 * @return The assigned value
	 * @throws CodeError If the values cannot use this operator
	 */
	public Value bracketAssign(Context context, Value other, Value assignValue, ISyntax syntaxPosition) throws CodeError {
		throw this.cannotApplyError(context, "BRACKET_ASSIGN", other, assignValue, syntaxPosition);
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

	private RuntimeError cannotApplyError(Context context, String operation, Value other, ISyntax syntaxPosition) throws CodeError {
		return new RuntimeError("The operation '%s' cannot be applied to %s and %s".formatted(
			operation,
			this.getAsString(context),
			other.getAsString(context)),
			syntaxPosition,
			context
		);
	}

	@SuppressWarnings("SameParameterValue")
	private RuntimeError cannotApplyError(Context context, String operation, Value second, Value third, ISyntax syntaxPosition) throws CodeError {
		return new RuntimeError("The operation '%s' cannot be applied to %s and %s and %s".formatted(
			operation,
			this.getAsString(context),
			second.getAsString(context),
			third.getAsString(context)),
			syntaxPosition,
			context
		);
	}

	public static String getValueName(Class<? extends Value> valueClass) {
		ClassDoc doc = valueClass.getAnnotation(ClassDoc.class);
		return doc == null ? valueClass.getSimpleName() : doc.name();
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
