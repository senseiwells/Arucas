package me.senseiwells.core.nodes;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.interpreter.Interpreter;
import me.senseiwells.core.values.BooleanValue;
import me.senseiwells.core.values.Value;

public class WhileNode extends Node {

    Node condition;
    Node body;

    public WhileNode(Node condition, Node body) {
        super(condition.token, condition.startPos, body.endPos);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error {
        while (true) {
            Value<?> conditionValue = interpreter.visit(this.condition, context);
            if (!(conditionValue instanceof BooleanValue booleanValue))
                throw new Error(Error.ErrorType.ILLEGAL_OPERATION_ERROR, "Condition must result in either 'true' or 'false'", this.startPos, this.endPos);
            if (!booleanValue.value)
                break;
            interpreter.visit(this.body, context);
        }
        return null;
    }
}
