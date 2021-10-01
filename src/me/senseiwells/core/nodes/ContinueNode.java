package me.senseiwells.core.nodes;

import me.senseiwells.core.utils.Context;
import me.senseiwells.core.throwables.Error;
import me.senseiwells.core.throwables.ThrowValue;
import me.senseiwells.core.utils.Interpreter;
import me.senseiwells.core.utils.Position;
import me.senseiwells.core.tokens.KeyWordToken;
import me.senseiwells.core.values.Value;

public class ContinueNode extends Node {

    public ContinueNode(Position startPos, Position endPos) {
        super(new KeyWordToken(KeyWordToken.KeyWord.CONTINUE, startPos, endPos));
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error, ThrowValue {
        ThrowValue value = new ThrowValue();
        value.shouldContinue = true;
        throw value;
    }
}
