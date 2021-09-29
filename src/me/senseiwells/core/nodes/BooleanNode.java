package me.senseiwells.core.nodes;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.interpreter.Interpreter;
import me.senseiwells.core.tokens.Token;
import me.senseiwells.core.tokens.ValueToken;
import me.senseiwells.core.values.BooleanValue;
import me.senseiwells.core.values.Value;

public class BooleanNode extends Node {

    public BooleanNode(Token token) {
        super(token);
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) {
        ValueToken token = ((ValueToken)this.token);
        return token.tokenValue.setPos(this.startPos, this.endPos).setContext(context);
    }
}
