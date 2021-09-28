package me.senseiwells.core.tokens;

import me.senseiwells.core.lexer.Position;
import me.senseiwells.core.values.Value;

public class ValueToken extends Token {

    public Value<?> tokenValue;

    public ValueToken(Type type, Position startPos, Position endPos, Value<?> value) {
        super(type, startPos, endPos);
        this.tokenValue = value;
    }

    @Override
    public String toString() {
        return "Token{" + "type=" + this.type + ", value=" + this.tokenValue + '}';
    }
}
