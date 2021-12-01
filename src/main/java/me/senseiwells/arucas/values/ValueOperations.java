package me.senseiwells.arucas.values;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.tokens.Token;

public interface ValueOperations {
    default BooleanValue isAnd(Value<?> other, ISyntax syntaxPosition) throws CodeError {
        throw cannotApplyError("AND", other, syntaxPosition);
    }

    default BooleanValue isOr(Value<?> other, ISyntax syntaxPosition) throws CodeError {
        throw cannotApplyError("OR", other, syntaxPosition);
    }

    default Value<?> addTo(Value<?> other, ISyntax syntaxPosition) throws CodeError {
        throw cannotApplyError("ADD", other, syntaxPosition);
    }

    default Value<?> subtractBy(Value<?> other, ISyntax syntaxPosition) throws CodeError {
        throw cannotApplyError("SUBTRACT", other, syntaxPosition);
    }

    default Value<?> multiplyBy(Value<?> other, ISyntax syntaxPosition) throws CodeError {
        throw cannotApplyError("MULTIPLY", other, syntaxPosition);
    }

    default Value<?> divideBy(Value<?> other, ISyntax syntaxPosition) throws CodeError {
        throw cannotApplyError("DIVIDE", other, syntaxPosition);
    }

    default Value<?> powerBy(Value<?> other, ISyntax syntaxPosition) throws CodeError {
        throw cannotApplyError("POWER", other, syntaxPosition);
    }

    default BooleanValue compareNumber(Value<?> other, Token.Type type, ISyntax syntaxPosition) throws CodeError {
        throw cannotApplyError(type.toString(), other, syntaxPosition);
    }

    default BooleanValue not(ISyntax syntaxPosition) throws CodeError {
        throw new RuntimeError("The operation 'NOT' cannot be applied to %s".formatted(this), syntaxPosition);
    }

    default BooleanValue isEqual(Value<?> other) {
        return new BooleanValue(this.equals(other));
    }

    default BooleanValue isNotEqual(Value<?> other) {
        return new BooleanValue(!this.equals(other));
    }

    private RuntimeError cannotApplyError(String operation, Value<?> other, ISyntax syntaxPosition) {
        return new RuntimeError("The operation '%s' cannot be applied to %s and %s".formatted(operation, this, other), syntaxPosition);
    }
}
