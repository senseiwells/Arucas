package me.senseiwells.core.nodes;

import me.senseiwells.core.utils.Context;
import me.senseiwells.core.throwables.ErrorRuntime;
import me.senseiwells.core.utils.Interpreter;
import me.senseiwells.core.tokens.Token;
import me.senseiwells.core.tokens.ValueToken;
import me.senseiwells.core.values.Value;

public class VariableAccessNode extends Node {

    public VariableAccessNode(Token token) {
        super(token, token.startPos, token.endPos);
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws ErrorRuntime {
        String name = (String) ((ValueToken)this.token).tokenValue.value;
        Value<?> value = context.symbolTable.get(name);
        if (value == null)
            throw new ErrorRuntime(name + " is not defined", this.startPos, this.endPos, context);
        value = value.copy();
        value.setPos(this.startPos, this.endPos);
        return value;
    }

    public boolean hasValue(Context context) {
        String name = (String) ((ValueToken)this.token).tokenValue.value;
        Value<?> value = context.symbolTable.get(name);
        return value != null;
    }
}
