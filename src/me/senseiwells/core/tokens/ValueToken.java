package me.senseiwells.core.tokens;

import me.senseiwells.core.lexer.Position;

public class ValueToken<T> extends Token {

    public T value;

    public ValueToken(Type type, Position startPos, Position endPos, T value) {
        super(type, startPos, endPos);
        this.value = value;
    }

    @Override
    public String toString() {
        return "Token{" + "type=" + this.type + ", value=" + this.value + '}';
    }
}
