package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.Interpreter;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.BooleanValue;
import me.senseiwells.arucas.values.Value;

public class BooleanNode extends Node {
    public final BooleanValue value;
    
    public BooleanNode(Token token) {
        super(token);
        this.value = new BooleanValue(Boolean.parseBoolean(token.content));
        this.value.setPos(this.startPos, this.endPos);
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) {
        return value.setContext(context);
    }
}
