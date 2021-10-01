package me.senseiwells.core.nodes;

import me.senseiwells.core.utils.Context;
import me.senseiwells.core.throwables.Error;
import me.senseiwells.core.throwables.ThrowValue;
import me.senseiwells.core.utils.Interpreter;
import me.senseiwells.core.utils.Position;
import me.senseiwells.core.tokens.Token;
import me.senseiwells.core.values.Value;

public abstract class Node {

    public Token token;
    public Position startPos;
    public Position endPos;

    Node(Token token, Position startPos, Position endPos) {
        this.token = token;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    Node(Token token) {
        this(token, token.startPos, token.endPos);
    }

    public abstract Value<?> visit(Interpreter interpreter, Context context) throws Error, ThrowValue;

    @Override
    public String toString() {
        return this.token.toString();
    }
}
