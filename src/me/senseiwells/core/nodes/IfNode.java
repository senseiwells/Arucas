package me.senseiwells.core.nodes;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.interpreter.Interpreter;
import me.senseiwells.core.values.BooleanValue;
import me.senseiwells.core.values.Value;
import me.senseiwells.helpers.ThreeValues;
import me.senseiwells.helpers.TwoValues;

import java.util.List;

public class IfNode extends Node {

    List<ThreeValues<Node, Node, Boolean>> nodes;
    TwoValues<Node, Boolean> elseNode;

    public IfNode(List<ThreeValues<Node, Node, Boolean>> nodes, TwoValues<Node, Boolean> elseNode) {
        super(nodes.get(0).getValue1().token, nodes.get(0).getValue1().startPos, elseNode == null ? nodes.get(nodes.size() - 1).getValue1().endPos : elseNode.getValue1().endPos);
        this.nodes = nodes;
        this.elseNode = elseNode;
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error {
        for (ThreeValues<Node, Node, Boolean> nodes : this.nodes) {
            Value<?> conditionValue = interpreter.visit(nodes.getValue1(), context);
            if (!(conditionValue instanceof BooleanValue booleanValue))
                throw new Error(Error.ErrorType.ILLEGAL_OPERATION_ERROR, "Condition must result in either 'true' or 'false'", this.startPos, this.endPos);
            if (booleanValue.value)
                return interpreter.visit(nodes.getValue2(), context);
        }
        if (this.elseNode != null)
            return interpreter.visit(this.elseNode.getValue1(), context);
        return null;
    }
}
