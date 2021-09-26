package me.senseiwells.core.nodes;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.interpreter.Interpreter;
import me.senseiwells.core.tokens.KeyWordToken;
import me.senseiwells.core.tokens.Token;
import me.senseiwells.core.values.BooleanValue;
import me.senseiwells.core.values.NumberValue;
import me.senseiwells.core.values.Value;

public class BinaryOperatorNode extends Node {

    public final Node leftNode;
    public final Node rightNode;

    public BinaryOperatorNode(Node leftNode, Token operatorToken, Node rightNode) {
        super(operatorToken, leftNode.startPos, rightNode.endPos);
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }

    @Override
    public Value<?> visit(Interpreter interpreter, Context context) throws Error {
        Value<?> left = interpreter.visit(this.leftNode, context);
        Value<?> right = interpreter.visit(this.rightNode, context);
        try {
            Value<?> result;
            switch (this.token.type) {
                case PLUS -> result = ((NumberValue) left).addTo((NumberValue) right);
                case MINUS -> result = ((NumberValue) left).subtractBy((NumberValue) right);
                case MULTIPLY -> result = ((NumberValue) left).multiplyBy((NumberValue) right);
                case DIVIDE -> result = ((NumberValue) left).divideBy((NumberValue) right);
                case EQUALS -> result = left.isEqual(right);
                case NOT_EQUALS -> result = left.isNotEqual(right);
                case LESS_THAN, LESS_THAN_EQUAL, MORE_THAN, MORE_THAN_EQUAL -> result = ((NumberValue) left).compareNumber((NumberValue) right, this.token.type);
                default -> {
                    if (this.token instanceof KeyWordToken) {
                        switch (((KeyWordToken) this.token).keyWord) {
                            case AND -> result = ((BooleanValue) left).isAnd((BooleanValue) right);
                            case OR -> result = ((BooleanValue) left).isOr((BooleanValue) right);
                            default -> throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected an operator", this.startPos, this.endPos);
                        }
                    }
                    else
                        throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected an operator", this.startPos, this.endPos);
                }
            }
            result.setPos(this.startPos, this.endPos);
            return result;
        }
        //When you try to use an operator that doesn't work e.g. true/false
        catch (ClassCastException classCastException) {
            throw new Error(Error.ErrorType.ILLEGAL_OPERATION_ERROR, "The operation '" + (this.token instanceof KeyWordToken ? ((KeyWordToken) this.token).keyWord.toString() : this.token.type.toString()) + "' cannot be applied to '" + left.value + "' and '" + right.value + "'", this.startPos, this.endPos);
        }
    }

    @Override
    public String toString() {
        return '(' + this.leftNode.toString() + " " + this.token.toString() + " " + this.rightNode + ')';
    }
}
