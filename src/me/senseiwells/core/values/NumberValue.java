package me.senseiwells.core.values;

import me.senseiwells.core.error.ErrorRuntime;
import me.senseiwells.core.tokens.Token;

public class NumberValue extends Value<Float>{

    public NumberValue(float value) {
        super(value);
    }

    public NumberValue addTo(NumberValue other) {
        NumberValue numberValue = new NumberValue(this.value + other.value);
        numberValue.setContext(this.context);
        return numberValue;
    }

    public NumberValue subtractBy(NumberValue other) {
        NumberValue numberValue = new NumberValue(this.value - other.value);
        numberValue.setContext(this.context);
        return numberValue;
    }

    public NumberValue multiplyBy(NumberValue other) {
        NumberValue numberValue = new NumberValue(this.value * other.value);
        numberValue.setContext(this.context);
        return numberValue;
    }

    public NumberValue divideBy(NumberValue other) throws ErrorRuntime {
        if (other.value == 0)
            throw new ErrorRuntime("You cannot divide by 0", other.startPos, other.endPos, context);
        NumberValue numberValue = new NumberValue(this.value / other.value);
        numberValue.setContext(this.context);
        return numberValue;
    }

    public BooleanValue compareNumber(NumberValue other, Token.Type type) {
        boolean bool;
        switch (type) {
            case LESS_THAN -> bool = this.value < other.value;
            case MORE_THAN -> bool = this.value > other.value;
            case MORE_THAN_EQUAL -> bool = this.value >= other.value;
            case LESS_THAN_EQUAL -> bool = this.value <= other.value;
            default -> bool = false;
        }
        BooleanValue value = new BooleanValue(bool);
        value.setContext(this.context);
        return value;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public Value<Float> copy() {
        return new NumberValue(this.value);
    }
}
