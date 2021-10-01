package me.senseiwells.core.utils;

import me.senseiwells.core.throwables.Error;
import me.senseiwells.core.throwables.ThrowValue;
import me.senseiwells.core.nodes.Node;
import me.senseiwells.core.values.Value;

public class Interpreter {

    public Value<?> visit(Node node, Context context) throws Error, ThrowValue {
        return node.visit(this, context);
    }
}
