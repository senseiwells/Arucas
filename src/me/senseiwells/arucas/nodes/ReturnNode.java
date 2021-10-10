package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Interpreter;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.values.Value;

import java.util.Objects;

public class ReturnNode extends Node {
    public final Node returnNode;

    public ReturnNode(Node returnNode, Position startPos, Position endPos) {
        super(new Token(Token.Type.RETURN, startPos, endPos));
        this.returnNode = Objects.requireNonNull(returnNode);
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws CodeError, ThrowValue {
        Value<?> value = interpreter.visit(this.returnNode, context);
        ThrowValue throwValue = new ThrowValue();
        throwValue.returnValue = value;
        throw throwValue;
    }
}
