package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.Interpreter;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;

public class NullNode extends Node {
    public final NullValue value;

    @Deprecated
    public NullNode(Token token) {
        super(token, null);
        this.value = new NullValue();
        this.value.setPos(this.startPos, this.endPos);
    }

    public NullNode(Token token, Context context) {
        super(token, context);
        this.value = new NullValue();
        this.value.setPos(this.startPos, this.endPos);
    }

    @Override
    public Value<?> visit() {
        return value.setContext(this.context);
    }
}
