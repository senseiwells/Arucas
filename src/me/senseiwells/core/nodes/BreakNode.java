package me.senseiwells.core.nodes;

import me.senseiwells.core.utils.Context;
import me.senseiwells.core.throwables.ThrowValue;
import me.senseiwells.core.utils.Interpreter;
import me.senseiwells.core.utils.Position;
import me.senseiwells.core.tokens.KeyWordToken;
import me.senseiwells.core.values.Value;

public class BreakNode extends Node {

    public BreakNode(Position startPos, Position endPos) {
        super(new KeyWordToken(KeyWordToken.KeyWord.BREAK, startPos, endPos));
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws ThrowValue {
        ThrowValue throwValue = new ThrowValue();
        throwValue.shouldBreak = true;
        throw throwValue;
    }
}
