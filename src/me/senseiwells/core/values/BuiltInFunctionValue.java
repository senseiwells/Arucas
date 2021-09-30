package me.senseiwells.core.values;

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
        this.context = this.generateNewContext();
        Value<?> returnValue = null;
        if (function == null)
            throw new ErrorRuntime("Function " + this.value + " is not defined", this.startPos, this.endPos, this.context);
        this.checkAndPopulateArguments(arguments, function.argumentNames, this.context);
        switch (function) {
            case DEBUG -> this.toggleDebug();
            case PRINT -> this.print();
            case IS_NUMBER -> returnValue = this.isType(NumberValue.class);
            case IS_STRING -> returnValue = this.isType(StringValue.class);
            case IS_BOOLEAN -> returnValue = this.isType(BooleanValue.class);
            case IS_FUNCTION -> returnValue = this.isType(BaseFunctionValue.class);
            case IS_LIST -> returnValue = this.isType(ListValue.class);
            case GET_INDEX -> returnValue = this.modifyListIndex(false);
            case REMOVE_INDEX -> returnValue = this.modifyListIndex(true);
            case APPEND -> returnValue = this.appendList();
            case CONCAT -> returnValue = this.concatList();
        }
        return returnValue;
    }

    public void print() {
        System.out.println(this.context.symbolTable.get("printValue"));
    }

    private BooleanValue isType(Class<?> classInstance) {
        return new BooleanValue(classInstance.isInstance(this.context.symbolTable.get("value")));
    }

    private Value<?> modifyListIndex(boolean delete) throws Error {
        Value<?> value = this.context.symbolTable.get("list");
        Value<?> numValue = this.context.symbolTable.get("index");
        if (!(value instanceof ListValue listValue))
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Must pass a list into parameter 1", this.startPos, this.endPos);
        if (!(numValue instanceof NumberValue numberValue))
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Must pass an integer into parameter 2", this.startPos, this.endPos);
        int index = numberValue.value.intValue();
        if (index >= listValue.value.size() || index < 0)
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Parameter 2 is out of bounds", this.startPos, this.endPos);
        return delete ? listValue.value.remove(index) : listValue.value.get(index);
    }

    private Value<?> appendList() throws Error {
        Value<?> listValue = this.context.symbolTable.get("list");
        Value<?> value = this.context.symbolTable.get("value");
        if (!(listValue instanceof ListValue list))
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Must pass a list into parameter 1", this.startPos, this.endPos);
        list.value.add(value);
        return list;
    }

    private Value<?> concatList() throws Error {
        Value<?> list1 = this.context.symbolTable.get("list");
        Value<?> list2 = this.context.symbolTable.get("otherList");
        if (!(list1 instanceof ListValue listValue1) || !(list2 instanceof ListValue listValue2))
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Parameters must both be lists", this.startPos, this.endPos);
        listValue1.value.addAll(listValue2.value);
        return listValue1;
    }

    private void toggleDebug() throws Error {
        Value<?> value = this.context.symbolTable.get("boolean");
        if (!(value instanceof BooleanValue booleanValue))
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Cannot pass " + value.value + "to debug()", this.startPos, this.endPos);
        Run.debug = booleanValue.value;
    }

    @Override
    public Value<?> copy() {
        return new BuiltInFunctionValue(this.value).setPos(this.startPos, this.endPos).setContext(this.context);
    }

    public enum BuiltInFunction {
        //general functions
        DEBUG("debug", List.of("boolean")),
        PRINT("print", List.of("printValue")),
        IS_NUMBER("isNumber", List.of("value")),
        IS_STRING("isString", List.of("value")),
        IS_BOOLEAN("isBoolean", List.of("value")),
        IS_FUNCTION("isFunction", List.of("value")),
        IS_LIST("isList", List.of("value")),

        //list functions
        GET_INDEX("getIndex", List.of("list", "index")),
        REMOVE_INDEX("removeIndex", List.of("list", "index")),
        APPEND("append", List.of("list", "value")),
        CONCAT("concat", List.of("list", "otherList"));

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
