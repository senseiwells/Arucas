package me.senseiwells.core.throwables;

import me.senseiwells.core.values.NullValue;
import me.senseiwells.core.values.Value;

public class ThrowValue extends Throwable {

    public Value<?> returnValue;
    public boolean shouldContinue;
    public boolean shouldBreak;

    public ThrowValue() {
        this.reset();
    }

    private void reset() {
        this.returnValue = new NullValue();
        this.shouldContinue = false;
        this.shouldBreak = false;
    }
}
