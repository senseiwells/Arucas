package me.senseiwells.core.nodes;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.interpreter.Interpreter;
import me.senseiwells.core.tokens.Token;
import me.senseiwells.core.tokens.ValueToken;
import me.senseiwells.core.values.Value;

public class VariableAssignNode extends Node {

    public Node node;

    public VariableAssignNode(Token token, Node node) {
        super(token, token.startPos, token.endPos);
        this.node = node;
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error {
        String name = (String) ((ValueToken)this.token).tokenValue.value;
        Value<?> value = interpreter.visit(this.node, context);
        context.symbolTable.set(name, value);
        return value;
    }
}
