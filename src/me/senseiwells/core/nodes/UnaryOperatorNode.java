package me.senseiwells.core.nodes;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.interpreter.Interpreter;
import me.senseiwells.core.tokens.KeyWordToken;
import me.senseiwells.core.tokens.Token;
import me.senseiwells.core.values.BooleanValue;
import me.senseiwells.core.values.NumberValue;
import me.senseiwells.core.values.Value;

public class UnaryOperatorNode extends Node {

    public final Node node;

    public UnaryOperatorNode(Token token, Node node) {
        super(token);
        this.node = node;
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error {
        Value<?> value = interpreter.visit(this.node, context);
        try {
            if (this.token.type == Token.Type.MINUS)
                value = ((NumberValue) value).multiplyBy(new NumberValue(-1));
            else if (this.token instanceof KeyWordToken && ((KeyWordToken) this.token).keyWord == KeyWordToken.KeyWord.NOT)
                value = ((BooleanValue) value).not();
            value.setPos(node.startPos, node.endPos);
            return value;
        }
        catch (ClassCastException classCastException) {
            throw new Error(Error.ErrorType.ILLEGAL_OPERATION_ERROR, "The operation '" + (this.token instanceof KeyWordToken ? ((KeyWordToken) this.token).keyWord.toString() : this.token.type.toString()) + "' cannot be applied to '" + value.value + "'", this.startPos, this.endPos);
        }
    }

    @Override
    public String toString() {
        return "(" + this.token + ", " + this.node + ")";
    }
}
