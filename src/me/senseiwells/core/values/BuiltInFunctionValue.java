package me.senseiwells.core.values;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.error.ErrorRuntime;
import me.senseiwells.core.run.Run;

import java.util.List;

public class BuiltInFunctionValue extends BaseFunctionValue {

    public BuiltInFunctionValue(String name) {
        super(name);
    }

    @Override
    public Value<?> execute(List<Value<?>> arguments) throws Error {
        BuiltInFunction function = BuiltInFunction.stringToFunction(this.value);
        Context context = this.generateNewContext();
        Value<?> returnValue = null;
        if (function == null)
            throw new ErrorRuntime("Function " + this.value + " is not defined", this.startPos, this.endPos, this.context);
        this.checkAndPopulateArguments(arguments, function.argumentNames, context);
        switch (function) {
            case DEBUG -> this.toggleDebug(context);
            case PRINT -> this.print(context);
            case IS_NUMBER -> returnValue = isType(context, NumberValue.class);
            case IS_STRING -> returnValue = isType(context, StringValue.class);
            case IS_BOOLEAN -> returnValue = isType(context, BooleanValue.class);
            case IS_FUNCTION -> returnValue = isType(context, BaseFunctionValue.class);
        }
        return returnValue;
    }

    public void print(Context context) {
        System.out.println(context.symbolTable.get("printValue"));
    }

    private BooleanValue isType(Context context, Class<?> classInstance) {
        return new BooleanValue(classInstance.isInstance(context.symbolTable.get("value")));
    }

    private void toggleDebug(Context context) throws Error {
        Value<?> value = context.symbolTable.get("boolean");
        if (!(value instanceof BooleanValue booleanValue))
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Cannot pass " + value.value + "to debug()", this.startPos, this.endPos);
        Run.debug = booleanValue.value;
    }

    @Override
    public Value<?> copy() {
        return new BuiltInFunctionValue(this.value).setPos(this.startPos, this.endPos).setContext(this.context);
    }

    public enum BuiltInFunction {

        DEBUG("debug", List.of("boolean")),
        PRINT("print", List.of("printValue")),
        IS_NUMBER("isNumber", List.of("value")),
        IS_STRING("isString",List.of("value")),
        IS_BOOLEAN("isBoolean", List.of("value")),
        IS_FUNCTION("isFunction", List.of("value"));
        //GET_INDEX
        //APPEND
        //CONCAT

        public String name;
        List<String> argumentNames;

        BuiltInFunction(String name, List<String> argumentNames) {
            this.name = name;
            this.argumentNames = argumentNames;
        }

        public static BuiltInFunction stringToFunction(String word) {
            for (BuiltInFunction value : BuiltInFunction.values()) {
                if (word.equals(value.name))
                    return value;
            }
            return null;
        }
    }
}
