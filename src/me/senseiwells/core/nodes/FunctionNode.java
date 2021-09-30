package me.senseiwells.core.nodes;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.interpreter.Interpreter;
import me.senseiwells.core.interpreter.SymbolTable;
import me.senseiwells.core.tokens.Token;
import me.senseiwells.core.tokens.ValueToken;
import me.senseiwells.core.values.BuiltInFunctionValue;
import me.senseiwells.core.values.FunctionValue;
import me.senseiwells.core.values.Value;

import java.util.LinkedList;
import java.util.List;

public class FunctionNode extends Node {

    static int lambdaNumber = 1;

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
        String functionName = this.variableNameToken != null ? (String) ((ValueToken)this.variableNameToken).tokenValue.value : "lambda_" + lambdaNumber++;
        if (SymbolTable.Literal.stringToLiteral(functionName) != null || BuiltInFunctionValue.BuiltInFunction.stringToFunction(functionName) != null)
            throw new Error(Error.ErrorType.ILLEGAL_OPERATION_ERROR, "Cannot define " + functionName + "() function as it is a predefined function", this.startPos, this.endPos);
        Node bodyNode = this.bodyNode;
        List<String> argumentNames = new LinkedList<>();
        this.argumentNameToken.forEach(t -> argumentNames.add((String) ((ValueToken)t).tokenValue.value));
        Value<?> functionValue = new FunctionValue(functionName, bodyNode, argumentNames).setContext(context).setPos(this.startPos, this.endPos);
        if (this.variableNameToken != null)
            context.symbolTable.set(functionName, functionValue);
        return functionValue;
    }
}
