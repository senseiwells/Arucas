package me.senseiwells.core.parser;

import me.senseiwells.core.error.Context;
import me.senseiwells.core.error.Error;
import me.senseiwells.core.nodes.*;
import me.senseiwells.core.tokens.KeyWordToken;
import me.senseiwells.core.tokens.Token;
import me.senseiwells.helpers.TwoValues;

import java.util.LinkedList;
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
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected an expression", this.currentToken.startPos, this.currentToken.endPos);
        return result;
    }

    //todo LEFT HERE 9:00
    private Node call() throws Error {
        List<Node> argumentNodes = new LinkedList<>();
        Node atom = this.atom();
        if (this.currentHasNoBracket())
            return atom;
        this.advance();
        if (this.currentToken.type != Token.Type.RIGHT_BRACKET) {
            argumentNodes.add(this.expression());
            while (this.currentToken.type == Token.Type.COMMA) {
                this.advance();
                argumentNodes.add(this.expression());
            }
            if (this.currentToken.type != Token.Type.RIGHT_BRACKET)
                throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected a ')'", this.currentToken.startPos, this.currentToken.endPos);
        }
        this.advance();
        return new CallNode(atom, argumentNodes);
    }

    private Node atom() throws Error {
        Token token = this.currentToken;
        switch (token.type) {
            case IDENTIFIER -> {
                this.advance();
                return new VariableAccessNode(token);
            }
            case INT, FLOAT -> {
                this.advance();
                return new NumberNode(token);
            }
            case BOOLEAN -> {
                this.advance();
                return new BooleanNode(token);
            }
            case STRING -> {
                this.advance();
                return new StringNode(token);
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
            case KEYWORD -> {
                switch (((KeyWordToken) this.currentToken).keyWord) {
                    case IF -> {
                        return this.ifExpression();
                    }
                    case WHILE -> {
                        return this.whileExpression();
                    }
                    case FUN -> {
                        return this.functionDefinition();
                    }
                }
            }
        }
        throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Unexpected Token", this.currentToken.startPos, this.currentToken.endPos );
    }

    private Node factor() throws Error {
        Token token = this.currentToken;
        if (token.type == Token.Type.PLUS || token.type == Token.Type.MINUS) {
            this.advance();
            Node factor = this.factor();
            return new UnaryOperatorNode(token, factor);
        }
        return this.power();
    }

    private Node power() throws Error {
        Node left = this.call();
        while (this.currentToken.type.isTypeInArray(new Token.Type[]{Token.Type.POWER})) {
            Token operatorToken = this.currentToken;
            this.advance();
            Node right = this.factor();
            left = new BinaryOperatorNode(left, operatorToken, right);
        }
        return left;
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
        if (this.currentToken.type == Token.Type.KEYWORD && ((KeyWordToken) this.currentToken).keyWord == KeyWordToken.KeyWord.VAR) {
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
        Node left = this.comparisonExpression();
        while (this.currentToken.type == Token.Type.KEYWORD && (((KeyWordToken) this.currentToken).keyWord == KeyWordToken.KeyWord.AND || ((KeyWordToken) this.currentToken).keyWord == KeyWordToken.KeyWord.OR)) {
            Token operatorToken = this.currentToken;
            this.advance();
            Node right = this.comparisonExpression();
            left = new BinaryOperatorNode(left, operatorToken, right);
        }
        return left;
    }

    private Node comparisonExpression() throws Error {
        if (this.currentToken.type == Token.Type.KEYWORD && ((KeyWordToken) this.currentToken).keyWord == KeyWordToken.KeyWord.NOT) {
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

    private Node ifExpression() throws Error {
        List<TwoValues<Node, Node>> cases = new LinkedList<>();
        Node elseCase = null;
        this.advance();
        if (this.currentHasNoBracket())
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected 'if (...)'", this.currentToken.startPos, this.currentToken.endPos);
        Node condition = this.expression();
        if (!(this.currentToken.type == Token.Type.KEYWORD || ((KeyWordToken) this.currentToken).keyWord != KeyWordToken.KeyWord.THEN))
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected 'then' or '->'", this.currentToken.startPos, this.currentToken.endPos);
        this.advance();
        Node expression = this.expression();
        cases.add(new TwoValues<>(condition, expression));
        /* "elif (...) -> ..." code, removed as can do "else if (...) -> ..."
        while (this.currentToken.type == Token.Type.KEYWORD && ((KeyWordToken) this.currentToken).keyWord == KeyWordToken.KeyWord.ELIF) {
            this.advance();
            condition = this.expression();
            if (this.currentToken.type != Token.Type.KEYWORD || ((KeyWordToken) this.currentToken).keyWord != KeyWordToken.KeyWord.THEN)
                throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected 'then'", this.currentToken.startPos, this.currentToken.endPos);
            this.advance();
            expression = this.expression();
            cases.add(new TwoValues<>(condition, expression));
        }
         */
        if (this.currentToken.type == Token.Type.KEYWORD && ((KeyWordToken) this.currentToken).keyWord == KeyWordToken.KeyWord.ELSE) {
            this.advance();
            elseCase = this.expression();
        }
        return new IfNode(cases, elseCase);
    }

    private Node whileExpression() throws Error {
        this.advance();
        if (this.currentHasNoBracket())
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected 'while (...)'", this.currentToken.startPos, this.currentToken.endPos);
        Node condition = this.expression();
        if (this.currentToken.type != Token.Type.KEYWORD || ((KeyWordToken)this.currentToken).keyWord != KeyWordToken.KeyWord.THEN)
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected 'then' or '->'", this.currentToken.startPos, this.currentToken.endPos);
        this.advance();
        Node body = this.expression();
        return new WhileNode(condition, body);
    }

    private Node functionDefinition() throws Error {
        List<Token> argumentNameTokens = new LinkedList<>();
        Token variableNameToken = null;
        this.advance();
        if (this.currentToken.type == Token.Type.IDENTIFIER) {
            variableNameToken = currentToken;
            this.advance();
        }
        if (this.currentHasNoBracket())
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected '(...)'", this.currentToken.startPos, this.currentToken.endPos);
        this.advance();
        if (this.currentToken.type == Token.Type.IDENTIFIER) {
            argumentNameTokens.add(this.currentToken);
            this.advance();
            while (this.currentToken.type == Token.Type.COMMA) {
                this.advance();
                if (this.currentToken.type != Token.Type.IDENTIFIER)
                    throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected Identifier", this.currentToken.startPos, this.currentToken.endPos);
                argumentNameTokens.add(this.currentToken);
                this.advance();
            }
        }
        if (this.currentToken.type != Token.Type.RIGHT_BRACKET){
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected ',' or ')'", this.currentToken.startPos, this.currentToken.endPos);
        }
        this.advance();
        if (this.currentToken.type != Token.Type.KEYWORD || ((KeyWordToken)this.currentToken).keyWord != KeyWordToken.KeyWord.THEN)
            throw new Error(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected 'then' or '->'", this.currentToken.startPos, this.currentToken.endPos);
        this.advance();
        Node returnNode = this.expression();
        return new FunctionNode(variableNameToken, argumentNameTokens, returnNode);
    }


    private boolean currentHasNoBracket() {
        return this.currentToken.type != Token.Type.LEFT_BRACKET;
    }
 }