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

    public void setPos(Position startPos, Position endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public BooleanValue isEqual(Value<?> other) {
        BooleanValue booleanValue = new BooleanValue(this.value.equals(other.value));
        booleanValue.setContext(this.context);
        return booleanValue;
    }

    public BooleanValue isNotEqual(Value<?> other) {
        BooleanValue booleanValue = new BooleanValue(!this.value.equals(other.value));
        booleanValue.setContext(this.context);
        return booleanValue;
    }

    public abstract Value<T> copy();

}
