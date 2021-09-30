package me.senseiwells.core.nodes;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.interpreter.Interpreter;
import me.senseiwells.core.lexer.Position;
import me.senseiwells.core.tokens.Token;
import me.senseiwells.core.values.ListValue;
import me.senseiwells.core.values.Value;

import java.util.LinkedList;
import java.util.List;

public class ListNode extends Node {

    List<Node> elementNodes;

    public ListNode(List<Node> elementNodes, Position posStart, Position posEnd) {
        super(new Token(Token.Type.LIST, posStart, posEnd));
        this.elementNodes = elementNodes;
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error {
        List<Value<?>> elements = new LinkedList<>();
        for (Node elementNode : this.elementNodes)
            elements.add(elementNode.visit(interpreter, context));
        return new ListValue(elements).setContext(context).setPos(this.startPos, this.endPos);
    }
}
