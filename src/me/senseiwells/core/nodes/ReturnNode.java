package me.senseiwells.core.nodes;

import me.senseiwells.core.utils.Context;
import me.senseiwells.core.throwables.Error;
import me.senseiwells.core.throwables.ThrowValue;
import me.senseiwells.core.utils.Interpreter;
import me.senseiwells.core.utils.Position;
import me.senseiwells.core.tokens.KeyWordToken;
import me.senseiwells.core.values.NullValue;
import me.senseiwells.core.values.Value;

public class ReturnNode extends Node {

    Node returnNode;

    public ReturnNode(Node returnNode, Position startPos, Position endPos) {
        super(new KeyWordToken(KeyWordToken.KeyWord.RETURN, startPos, endPos));
        this.returnNode = returnNode;
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error, ThrowValue {
        Value<?> value = new NullValue();
        if (this.returnNode != null) {
            value = interpreter.visit(this.returnNode, context);
        }
        ThrowValue throwValue = new ThrowValue();
        throwValue.returnValue = value;
        throw throwValue;
    }
}
