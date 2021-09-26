package me.senseiwells.core.parser;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.nodes.*;
import me.senseiwells.core.tokens.KeyWordToken;
import me.senseiwells.core.tokens.Token;

import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int operatorTokenIndex;
    private Token currentToken;
    private final Context context;

    public Parser(List<Token> tokens, Context context) {
        this.tokens = tokens;
        this.operatorTokenIndex = -1;
        this.context = context;
        this.advance();
    }

    private void advance() {
        this.operatorTokenIndex++;
        this.currentToken = this.operatorTokenIndex < this.tokens.toArray().length ? this.tokens.get(this.operatorTokenIndex) : null;
    }

    private void recede() {
        this.operatorTokenIndex--;
        this.currentToken = this.operatorTokenIndex < this.tokens.toArray().length ? this.tokens.get(this.operatorTokenIndex) : null;
    }

    public Node parse() throws Error {
        Node result = this.expression();
        if (this.currentToken.type != Token.Type.END)
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected an operator", this.currentToken.startPos, this.currentToken.endPos);
        return result;
    }

    private Node factor() throws Error {
        Token token = this.currentToken;
        switch (token.type) {
            case IDENTIFIER -> {
                this.advance();
                return new VariableAccessNode(token);
            }
            case PLUS, MINUS -> {
                this.advance();
                Node factor = this.factor();
                return new UnaryOperatorNode(token, factor);
            }
            case INT, FLOAT -> {
                this.advance();
                return new NumberNode(token);
            }
            case BOOLEAN -> {
                this.advance();
                return new BooleanNode(token);
            }
            case LEFT_BRACKET -> {
                this.advance();
                Node expression = this.expression();
                if (this.currentToken.type == Token.Type.RIGHT_BRACKET) {
                    this.advance();
                    return expression;
                }
                throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected ')'", this.currentToken.startPos, this.currentToken.endPos );
            }
        }
        throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected an integer, float, identifier, or '('", token.startPos, token.endPos);
    }

    private Node term() throws Error {
        Node left = this.factor();
        while (this.currentToken.type.isTypeInArray(new Token.Type[]{Token.Type.MULTIPLY, Token.Type.DIVIDE})) {
            Token operatorToken = this.currentToken;
            this.advance();
            Node right = this.factor();
            left = new BinaryOperatorNode(left, operatorToken, right);
        }
        return left;
    }

    private Node expression() throws Error {
        //Initialise variable with keyword 'var' -> stores value in map
        if (this.currentToken instanceof KeyWordToken && ((KeyWordToken) this.currentToken).keyWord == KeyWordToken.KeyWord.VAR) {
            this.advance();
            if (this.currentToken.type != Token.Type.IDENTIFIER)
                throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected an identifier", this.currentToken.startPos, this.currentToken.endPos);
            Token token = this.currentToken;
            this.advance();
            if (this.currentToken.type != Token.Type.ASSIGN_OPERATOR)
                throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected an assignment operator", this.currentToken.startPos, this.currentToken.endPos);
            this.advance();
            Node expression = this.expression();
            return new VariableAssignNode(token, expression);
        }
        //If identifier is already a variable -> can assign value without 'var' keyword
        else if (this.currentToken.type == Token.Type.IDENTIFIER) {
            Token cachedToken = this.currentToken;
            this.advance();
            if (this.currentToken.type == Token.Type.ASSIGN_OPERATOR && new VariableAccessNode(cachedToken).hasValue(this.context)) {
                this.advance();
                Node expression = this.expression();
                return new VariableAssignNode(cachedToken, expression);
            }
            this.recede();
        }
        try {
            Node left = this.comparisonExpression();
            while (this.currentToken instanceof KeyWordToken && (((KeyWordToken) this.currentToken).keyWord == KeyWordToken.KeyWord.AND || ((KeyWordToken) this.currentToken).keyWord == KeyWordToken.KeyWord.OR)) {
                Token operatorToken = this.currentToken;
                this.advance();
                Node right = this.comparisonExpression();
                left = new BinaryOperatorNode(left, operatorToken, right);
            }
            return left;
        }
        catch (Error e) {
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected 'var', integer, float, or '('", this.currentToken.startPos, this.currentToken.endPos);
        }
    }

    private Node comparisonExpression() throws Error {
        if (this.currentToken instanceof KeyWordToken && ((KeyWordToken) this.currentToken).keyWord == KeyWordToken.KeyWord.NOT) {
            Token token = this.currentToken;
            this.advance();
            Node node = this.comparisonExpression();
            return new UnaryOperatorNode(token, node);
        }
        Node left = this.arithmeticExpression();
        while (this.currentToken.type.isTypeInArray(new Token.Type[]{Token.Type.EQUALS, Token.Type.NOT_EQUALS, Token.Type.LESS_THAN, Token.Type.LESS_THAN_EQUAL, Token.Type.MORE_THAN, Token.Type.MORE_THAN_EQUAL})) {
            Token operatorToken = this.currentToken;
            this.advance();
            Node right = this.arithmeticExpression();
            left = new BinaryOperatorNode(left, operatorToken, right);
        }
        return left;
    }

    private Node arithmeticExpression() throws Error {
        Node left = this.term();
        while (this.currentToken.type.isTypeInArray(new Token.Type[]{Token.Type.PLUS, Token.Type.MINUS})) {
            Token operatorToken = this.currentToken;
            this.advance();
            Node right = this.term();
            left = new BinaryOperatorNode(left, operatorToken, right);
        }
        return left;
    }
 }