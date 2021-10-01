package me.senseiwells.core.nodes;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.interpreter.Interpreter;
import me.senseiwells.core.lexer.Position;
import me.senseiwells.core.tokens.KeyWordToken;
import me.senseiwells.core.values.Value;

public class ReturnNode extends Node {

    Node returnNode;

    ReturnNode(Node returnNode, Position startPos, Position endPos) {
        super(new KeyWordToken(KeyWordToken.KeyWord.RETURN, startPos, endPos));
        this.returnNode = returnNode;
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error {
        return null;
    }
}
