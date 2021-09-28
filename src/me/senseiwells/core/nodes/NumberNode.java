package me.senseiwells.core.nodes;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.interpreter.Interpreter;
import me.senseiwells.core.tokens.Token;
import me.senseiwells.core.tokens.ValueToken;
import me.senseiwells.core.values.NumberValue;
import me.senseiwells.core.values.Value;

public class NumberNode extends Node {

    public NumberNode(Token token) {
        super(token);
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) {
        ValueToken token = ((ValueToken)this.token);
        NumberValue number = (NumberValue) token.tokenValue;
        number.setPos(this.startPos, this.endPos);
        number.setContext(context);
        return number;
    }
}
