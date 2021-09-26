package me.senseiwells.core.interpreter;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.nodes.Node;
import me.senseiwells.core.values.Value;

public class Interpreter {

    public Value<?> visit(Node node, Context context) throws Error {
        return node.visit(this, context);
    }
}
