package me.senseiwells.core.values;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.error.ErrorRuntime;
import me.senseiwells.core.interpreter.Interpreter;
import me.senseiwells.core.interpreter.SymbolTable;
import me.senseiwells.core.nodes.Node;

import java.util.List;

public class FunctionValue extends Value<Node> {

    String name;
    List<String> argumentNames;

    public FunctionValue(String name, Node bodyNode, List<String> argumentNames) {
        super(bodyNode);
        this.name = name;
        this.argumentNames = argumentNames;
    }

    public Value<?> execute(List<Value<?>> arguments) throws Error {
        Interpreter interpreter = new Interpreter();
        Context context = new Context(this.name, this.context, this.startPos);
        context.symbolTable = new SymbolTable(context.parent.symbolTable);
        if (arguments.size() > this.argumentNames.size())
            throw new ErrorRuntime(arguments.size() - this.argumentNames.size() + " too many arguments passed into " + this.name, this.startPos, this.endPos, context);
        if (arguments.size() < this.argumentNames.size())
            throw new ErrorRuntime(this.argumentNames.size() - arguments.size() + " too few arguments passed into " + this.name, this.startPos, this.endPos, context);
        for (int i = 0; i < this.argumentNames.size(); i++) {
            String argumentName = this.argumentNames.get(i);
            Value<?> argumentValue = arguments.get(i);
            argumentValue.setContext(context);
            context.symbolTable.set(argumentName, argumentValue);
        }
        return interpreter.visit(this.value, context);
    }

    @Override
    public Value<Node> copy() {
        return new FunctionValue(this.name, this.value, this.argumentNames);
    }

    @Override
    public String toString() {
        return "<function " + this.name + ">";
     }
}
