package me.senseiwells.arucas.values;

import me.senseiwells.arucas.throwables.Error;

public interface Function {

    Value<?> execute(BuiltInFunctionValue function) throws Error;

}
