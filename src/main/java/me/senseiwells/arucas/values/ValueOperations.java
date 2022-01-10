package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;

import java.util.Map;

public interface ValueOperations {
	default BooleanValue isAnd(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "AND", other, syntaxPosition);
	}

	default BooleanValue isOr(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "OR", other, syntaxPosition);
	}

	default Value<?> addTo(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "ADD", other, syntaxPosition);
	}

	default Value<?> subtractBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "SUBTRACT", other, syntaxPosition);
	}

	default Value<?> multiplyBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "MULTIPLY", other, syntaxPosition);
	}

	default Value<?> divideBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "DIVIDE", other, syntaxPosition);
	}

	default Value<?> powerBy(Context context, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, "POWER", other, syntaxPosition);
	}

	default BooleanValue compareNumber(Context context, Value<?> other, Token.Type type, ISyntax syntaxPosition) throws CodeError {
		throw cannotApplyError(context, type.toString(), other, syntaxPosition);
	}

	default BooleanValue not(Context context, ISyntax syntaxPosition) throws CodeError {
		throw new RuntimeError("The operation 'NOT' cannot be applied to %s".formatted(this.getStringValue(context)), syntaxPosition, context);
	}

    default BooleanValue isEqual(Value<?> other) {
        return BooleanValue.of(this.equals(other));
    }

    default BooleanValue isNotEqual(Value<?> other) {
        return BooleanValue.of(!this.equals(other));
    }

	String getStringValue(Context context) throws CodeError;
	int getHashCode(Context context) throws CodeError;
	boolean isEquals(Context context, Value<?> other) throws CodeError;

	private RuntimeError cannotApplyError(Context context, String operation, Value<?> other, ISyntax syntaxPosition) throws CodeError {
		return new RuntimeError("The operation '%s' cannot be applied to %s and %s".formatted(
			operation,
			this.getStringValue(context),
			other.getStringValue(context)),
			syntaxPosition,
			context
		);
	}

	final Map<Token.Type, Integer> OVERRIDABLE_OPERATOR_TOKENS = Map.ofEntries(
		Map.entry(Token.Type.PLUS, 2),
		Map.entry(Token.Type.MINUS, 2),
		Map.entry(Token.Type.MULTIPLY, 2),
		Map.entry(Token.Type.DIVIDE, 2),
		Map.entry(Token.Type.POWER, 2),
		Map.entry(Token.Type.LESS_THAN, 2),
		Map.entry(Token.Type.LESS_THAN_EQUAL, 2),
		Map.entry(Token.Type.MORE_THAN, 2),
		Map.entry(Token.Type.MORE_THAN_EQUAL, 2),
		Map.entry(Token.Type.EQUALS, 2),
		Map.entry(Token.Type.NOT_EQUALS, 2),
		Map.entry(Token.Type.NOT, 1)
	);
}
