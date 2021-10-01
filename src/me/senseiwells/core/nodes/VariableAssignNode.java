package me.senseiwells.core.nodes;

import me.senseiwells.core.utils.Context;
import me.senseiwells.core.throwables.Error;
import me.senseiwells.core.throwables.ThrowValue;
import me.senseiwells.core.utils.Interpreter;
import me.senseiwells.core.utils.SymbolTable;
import me.senseiwells.core.tokens.Token;
import me.senseiwells.core.tokens.ValueToken;
import me.senseiwells.core.values.BuiltInFunctionValue;
import me.senseiwells.core.values.Value;

public class VariableAssignNode extends Node {

    public Node node;

    public VariableAssignNode(Token token, Node node) {
        super(token, token.startPos, token.endPos);
        this.node = node;
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error, ThrowValue {
        String name = (String) ((ValueToken) this.token).tokenValue.value;
        if (SymbolTable.Literal.stringToLiteral(name) != null || BuiltInFunctionValue.BuiltInFunction.stringToFunction(name) != null)
            throw new Error(Error.ErrorType.ILLEGAL_OPERATION_ERROR, "Cannot assign " + name + " value as it is a constant", this.startPos, this.endPos);
        Value<?> value = interpreter.visit(this.node, context);
        context.symbolTable.set(name, value);
        return value;
    }
}
