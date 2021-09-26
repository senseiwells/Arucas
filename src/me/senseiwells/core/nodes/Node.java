package me.senseiwells.core.nodes;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.interpreter.Interpreter;
import me.senseiwells.core.lexer.Position;
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

    public abstract Value<?> visit(Interpreter interpreter, Context context) throws Error;

    @Override
    public String toString() {
        return this.token.toString();
    }
}
