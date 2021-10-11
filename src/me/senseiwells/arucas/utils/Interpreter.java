package me.senseiwells.arucas.utils;

import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.nodes.Node;
import me.senseiwells.arucas.values.Value;

public class Interpreter {

    @Deprecated
    public Value<?> visit(Node node, Context context) throws CodeError, ThrowValue {
        return node.visit();
    }
}
