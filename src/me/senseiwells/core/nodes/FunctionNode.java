package me.senseiwells.core.nodes;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.interpreter.Interpreter;
import me.senseiwells.core.lexer.Position;
import me.senseiwells.core.tokens.Token;
import me.senseiwells.core.values.Value;

import java.util.List;

public class FunctionNode extends Node {

    Token variableNameToken;
    List<Token> argumentNameToken;
    Node bodyNode;

    public FunctionNode(Token varNameToken, List<Token> argumentNameToken, Node bodyNode) {
        super(bodyNode.token, varNameToken != null ? varNameToken.startPos : argumentNameToken.size() > 0 ? argumentNameToken.get(0).startPos : bodyNode.startPos, bodyNode.endPos);
        this.variableNameToken = varNameToken;
        this.argumentNameToken = argumentNameToken;
        this.bodyNode = bodyNode;
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error {
        return null;
    }
}
