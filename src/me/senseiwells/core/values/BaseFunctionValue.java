package me.senseiwells.core.values;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.error.ErrorRuntime;
import me.senseiwells.core.interpreter.SymbolTable;

import java.util.List;

public abstract class BaseFunctionValue extends Value<String> {

    public BaseFunctionValue(String name) {
        super(name);
    }

    public Context generateNewContext() {
        Context context = new Context(this.value, this.context, this.startPos);
        context.symbolTable = new SymbolTable(context.parent.symbolTable);
        return context;
    }

    private void checkArguments(List<Value<?>> arguments, List<String> argumentNames) throws ErrorRuntime {
        if (arguments.size() > argumentNames.size())
            throw new ErrorRuntime(arguments.size() - argumentNames.size() + " too many arguments passed into " + this.value, this.startPos, this.endPos, this.context);
        if (arguments.size() < argumentNames.size())
            throw new ErrorRuntime(argumentNames.size() - arguments.size() + " too few arguments passed into " + this.value, this.startPos, this.endPos, this.context);
    }

    private void populateArguments(List<Value<?>> arguments, List<String> argumentNames, Context context) {
        for (int i = 0; i < argumentNames.size(); i++) {
            String argumentName = argumentNames.get(i);
            Value<?> argumentValue = arguments.get(i);
            argumentValue.setContext(context);
            context.symbolTable.set(argumentName, argumentValue);
        }
    }

    public void checkAndPopulateArguments(List<Value<?>> arguments, List<String> argumentNames, Context context) throws ErrorRuntime {
        this.checkArguments(arguments, argumentNames);
        this.populateArguments(arguments, argumentNames, context);
    }

    public abstract Value<?> execute(List<Value<?>> arguments) throws Error;

    @Override
    public abstract Value<?> copy();
}
