package me.senseiwells.core.values;

public class BooleanValue extends Value<Boolean> {

    public BooleanValue(Boolean value) {
        super(value);
    }

    public BooleanValue isAnd(BooleanValue other) {
        BooleanValue booleanValue = new BooleanValue(this.value && other.value);
        booleanValue.setContext(this.context);
        return booleanValue;
    }

    public BooleanValue isOr(BooleanValue other) {
        BooleanValue booleanValue = new BooleanValue(this.value || other.value);
        booleanValue.setContext(this.context);
        return booleanValue;
    }

    public BooleanValue not() {
        BooleanValue value = new BooleanValue(!this.value);
        value.setContext(this.context);
        return value;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public Value<Boolean> copy() {
        return new BooleanValue(this.value);
    }
}
