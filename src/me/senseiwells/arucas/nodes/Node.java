package me.senseiwells.arucas.nodes;

import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.Interpreter;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.SymbolTable;
import me.senseiwells.arucas.values.Value;

public abstract class Node {
    public final Token token;
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

    public abstract Value<?> visit(Interpreter interpreter, Context context) throws CodeError, ThrowValue;

    @Override
    public String toString() {
        return this.token.toString();
    }
}
