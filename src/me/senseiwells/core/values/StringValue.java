package me.senseiwells.core.values;

public class StringValue extends Value<String> {

    public StringValue(String value) {
        super(value);
    }

    @Override
    public Value<String> copy() {
        return new StringValue(this.value);
    }
}
