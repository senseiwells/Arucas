package me.senseiwells.core.values;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.interpreter.Interpreter;
import me.senseiwells.core.nodes.Node;

import java.util.List;

public class FunctionValue extends BaseFunctionValue {

    Node bodyNode;
    List<String> argumentNames;

    public FunctionValue(String name, Node bodyNode, List<String> argumentNames) {
        super(name);
        this.bodyNode = bodyNode;
        this.argumentNames = argumentNames;
    }

    public Value<?> execute(List<Value<?>> arguments) throws Error {
        Interpreter interpreter = new Interpreter();
        Context context = this.generateNewContext();
        this.checkAndPopulateArguments(arguments, this.argumentNames, context);
        return interpreter.visit(this.bodyNode, context);
    }

    @Override
    public Value<?> copy() {
        return new FunctionValue(this.value, this.bodyNode, this.argumentNames).setPos(this.startPos, this.endPos).setContext(this.context);
    }

    @Override
    public String toString() {
        return "<function " + this.value + ">";
     }
}
