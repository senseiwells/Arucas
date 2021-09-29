package me.senseiwells.core.values;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.lexer.Position;

public abstract class Value<T> {

    public T value;
    Position startPos;
    Position endPos;
    Context context;

    public Value(T value) {
        this.value = value;
    }

    public Value<T> setPos(Position startPos, Position endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
        return this;
    }

    public Value<T> setContext(Context context) {
        this.context = context;
        return this;
    }

    public BooleanValue isEqual(Value<?> other) {
        return (BooleanValue) new BooleanValue(this.value.equals(other.value)).setContext(this.context);
    }

    public BooleanValue isNotEqual(Value<?> other) {
        return (BooleanValue) new BooleanValue(!this.value.equals(other.value)).setContext(this.context);
    }

    public abstract Value<T> copy();

    @Override
    public String toString() {
        return this.value.toString();
    }

}
