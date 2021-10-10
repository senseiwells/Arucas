package me.senseiwells.arucas.core;

import me.senseiwells.arucas.throwables.ThrowValue;
import me.senseiwells.arucas.utils.*;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.nodes.*;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.BuiltInFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        this.currentToken = this.operatorTokenIndex < this.tokens.size() ? this.tokens.get(this.operatorTokenIndex) : null;
    }

    private void recede() {
        this.operatorTokenIndex--;
        this.currentToken = this.operatorTokenIndex < this.tokens.size() ? this.tokens.get(this.operatorTokenIndex) : null;
    }

    private void recede(int times) {
        this.operatorTokenIndex -= times;
        this.currentToken = this.operatorTokenIndex < this.tokens.size() ? this.tokens.get(this.operatorTokenIndex) : null;
    }

    public Node parse() throws CodeError {
        Node result = this.statements();
        if (this.currentToken.type != Token.Type.FINISH)
            throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected an expression", this.currentToken.startPos, this.currentToken.endPos);
        return result;
    }

    private Node call() throws CodeError {
        List<Node> argumentNodes = new ArrayList<>();
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
                throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected a ')'", this.currentToken.startPos, this.currentToken.endPos);
        }
        this.advance();
        return new CallNode(atom, argumentNodes);
    }

    private Node atom() throws CodeError {
        Token token = this.currentToken;
        switch (token.type) {
            case IDENTIFIER -> {
                this.advance();
                return new VariableAccessNode(token);
            }
            case FLOAT -> {
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
            case NULL -> {
                this.advance();
                return new NullNode(token);
            }
            case LEFT_BRACKET -> {
                this.advance();
                Node expression = this.expression();
                if (this.currentToken.type == Token.Type.RIGHT_BRACKET) {
                    this.advance();
                    return expression;
                }
                throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Invalid conditional statement", this.currentToken.startPos, this.currentToken.endPos );
            }
            case LEFT_SQUARE_BRACKET -> {
                return this.listExpression();
            }
            case FUN -> {
                // FunctionLambda
                return this.functionDefinition(true);
            }
        }
        throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Unexpected Token: " + token, this.currentToken.startPos, this.currentToken.endPos);
    }

    private Node factor() throws CodeError {
        Token token = this.currentToken;
        if (token.type == Token.Type.PLUS || token.type == Token.Type.MINUS) {
            this.advance();
            Node factor = this.factor();
            return new UnaryOperatorNode(token, factor);
        }
        return this.power();
    }

    private Node power() throws CodeError {
        Node left = this.call();
        while (this.currentToken.type.isTypeInSet(Set.of(Token.Type.POWER))) {
            Token operatorToken = this.currentToken;
            this.advance();
            Node right = this.factor();
            left = new BinaryOperatorNode(left, operatorToken, right);
        }
        return left;
    }

    private Node term() throws CodeError {
        Node left = this.factor();
        while (this.currentToken.type.isTypeInSet(Set.of(Token.Type.MULTIPLY, Token.Type.DIVIDE))) {
            Token operatorToken = this.currentToken;
            this.advance();
            Node right = this.factor();
            left = new BinaryOperatorNode(left, operatorToken, right);
        }
        return left;
    }

    private Node statements() throws CodeError {
        List<Node> statements = new ArrayList<>();
        Position startPos = this.currentToken.startPos;
        
        if(this.currentToken.type == Token.Type.FINISH) {
            return new NullNode(this.currentToken);
        }
        
        while (this.currentToken.type == Token.Type.SEMICOLON) {
            this.advance();
        }
        
        while (true) {
            statements.add(this.statement());
            
            while (this.currentToken.type == Token.Type.SEMICOLON) {
                this.advance();
            }
            
            if(this.currentToken.type == Token.Type.RIGHT_CURLY_BRACKET
            || this.currentToken.type == Token.Type.FINISH) {
                break;
            }
        }
        return new ListNode(statements, startPos, this.currentToken.endPos);
    }

    private Node statement() throws CodeError {
        // Statements defined in this switch will not need a semicolon
        switch(this.currentToken.type) {
            case LEFT_CURLY_BRACKET -> {
                this.advance();
                Node statement = this.statements();
                if (this.currentToken.type != Token.Type.RIGHT_CURLY_BRACKET)
                    throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected '}'", this.currentToken.startPos, this.currentToken.endPos);
                this.advance();
                return statement;
            }
            case IF -> {
                return this.ifExpression();
            }
            case WHILE -> {
                return this.whileExpression();
            }
            case FUN -> {
                // FunctionDeclaration
                return this.functionDefinition(false);
            }
        }
        
        Node node = this.expression();
        if (this.currentToken.type != Token.Type.SEMICOLON)
            throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected ; at end of line", this.currentToken.startPos, this.currentToken.endPos);
        
        this.advance();
        return node;
    }

    private Node expression() throws CodeError {
        Position startPos = this.currentToken.startPos;
        switch(this.currentToken.type) {
            case RETURN -> {
                this.advance();
                int cachedIndex = this.operatorTokenIndex;
                try {
                    Node expression = this.expression();
                    return new ReturnNode(expression, startPos, this.currentToken.endPos);
                }
                catch (CodeError error) {
                    if (error.errorType != CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR)
                        throw error;
                    this.recede(this.operatorTokenIndex - cachedIndex);
                    return new ReturnNode(null, startPos, this.currentToken.endPos);
                }
            }
            case CONTINUE -> {
                this.advance();
                return new ContinueNode(startPos, this.currentToken.endPos);
            }
            case BREAK -> {
                this.advance();
                return new BreakNode(startPos, this.currentToken.endPos);
            }
        }
        
        //Initialise variable with keyword 'var' -> stores value in map
        if (this.currentToken.type == Token.Type.VAR) {
            return this.setVariable();
        }
        //If identifier is already a variable -> can assign value without 'var' keyword
        else if (this.currentToken.type == Token.Type.IDENTIFIER) {
            Token cachedToken = this.currentToken;
            this.advance();
            if (this.currentToken.type == Token.Type.ASSIGN_OPERATOR) {
                this.advance();
                return new VariableAssignNode(cachedToken, this.expression());
            }
            this.recede();
        }
        Node left = this.comparisonExpression();
    
        while (this.currentToken.type == Token.Type.AND || this.currentToken.type == Token.Type.OR) {
            Token operatorToken = this.currentToken;
            this.advance();
            Node right = this.comparisonExpression();
            left = new BinaryOperatorNode(left, operatorToken, right);
        }
        return left;
    }

    private VariableAssignNode setVariable() throws CodeError {
        this.advance();
        if (this.currentToken.type != Token.Type.IDENTIFIER)
            throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected an identifier", this.currentToken.startPos, this.currentToken.endPos);
        Token token = this.currentToken;
        this.advance();
        if (this.currentToken.type != Token.Type.ASSIGN_OPERATOR)
            throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected an assignment operator", this.currentToken.startPos, this.currentToken.endPos);
        this.advance();
        Node expression = this.expression();
        return new VariableAssignNode(token, expression);
    }

    private Node comparisonExpression() throws CodeError {
        if (this.currentToken.type == Token.Type.NOT) {
            Token token = this.currentToken;
            this.advance();
            Node node = this.comparisonExpression();
            return new UnaryOperatorNode(token, node);
        }
        Node left = this.arithmeticExpression();
        while (this.currentToken.type.isTypeInSet(Set.of(Token.Type.EQUALS, Token.Type.NOT_EQUALS, Token.Type.LESS_THAN, Token.Type.LESS_THAN_EQUAL, Token.Type.MORE_THAN, Token.Type.MORE_THAN_EQUAL))) {
            Token operatorToken = this.currentToken;
            this.advance();
            Node right = this.arithmeticExpression();
            left = new BinaryOperatorNode(left, operatorToken, right);
        }
        return left;
    }

    private Node arithmeticExpression() throws CodeError {
        Node left = this.term();
        while (this.currentToken.type.isTypeInSet(Set.of(Token.Type.PLUS, Token.Type.MINUS))) {
            Token operatorToken = this.currentToken;
            this.advance();
            Node right = this.term();
            left = new BinaryOperatorNode(left, operatorToken, right);
        }
        return left;
    }

    private Node ifExpression() throws CodeError {
        var allCases = this.ifExpressionCases();
        return new IfNode(allCases.getValue1(), allCases.getValue2());
    }

    private TwoValues<List<ThreeValues<Node, Node, Boolean>>, TwoValues<Node, Boolean>> ifExpressionCases() throws CodeError {
        List<ThreeValues<Node, Node, Boolean>> cases = new ArrayList<>();
        TwoValues<Node, Boolean> elseCase;
        if (this.currentToken.type != Token.Type.IF)
            throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected '" + Token.Type.IF + "'", this.currentToken.startPos, this.currentToken.endPos);
        this.advance();
        if (this.currentHasNoBracket())
            throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected 'if (...)'", this.currentToken.startPos, this.currentToken.endPos);
        Node condition = this.expression();
        switch (this.currentToken.type) {
            case LEFT_CURLY_BRACKET -> {
                this.advance();
                Node statement = this.statements();
                cases.add(new ThreeValues<>(condition, statement, true));
                if (this.currentToken.type != Token.Type.RIGHT_CURLY_BRACKET)
                    throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected '}'", this.currentToken.startPos, this.currentToken.endPos);
                this.advance();
                var allCases = this.elseExpression();
                elseCase = allCases.getValue2();
                cases.addAll(allCases.getValue1());
            }
            case THEN -> {
                this.advance();
                Node expression = this.statement();
                cases.add(new ThreeValues<>(condition, expression, false));
                var allCases = this.elseExpression();
                elseCase = allCases.getValue2();
                cases.addAll(allCases.getValue1());
            }
            default -> throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected 'then','->', or '{'", this.currentToken.startPos, this.currentToken.endPos);
        }
        return new TwoValues<>(cases, elseCase);
    }

    private TwoValues<List<ThreeValues<Node, Node, Boolean>>, TwoValues<Node, Boolean>> elseExpression() throws CodeError {
        List<ThreeValues<Node, Node, Boolean>> cases = new ArrayList<>();
        TwoValues<Node, Boolean> elseCase;
        int reverse = 0;
        while (this.currentToken.type == Token.Type.SEMICOLON) {
            this.advance();
            reverse++;
        }
        if (this.currentToken.type == Token.Type.ELSE) {
            this.advance();
            switch (this.currentToken.type) {
                case IF -> {
                    var allCases = this.ifExpressionCases();
                    cases = allCases.getValue1();
                    elseCase = allCases.getValue2();
                    return new TwoValues<>(cases, elseCase);
                }
                case LEFT_CURLY_BRACKET -> {
                    this.advance();
                    Node statement = this.statements();
                    elseCase = new TwoValues<>(statement, true);
                    if (this.currentToken.type != Token.Type.RIGHT_CURLY_BRACKET)
                        throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected '}'", this.currentToken.startPos, this.currentToken.endPos);
                    this.advance();
                    return new TwoValues<>(cases, elseCase);
                }
                case THEN -> {
                    this.advance();
                    Node expression = this.statement();
                    elseCase = new TwoValues<>(expression, false);
                    return new TwoValues<>(cases, elseCase);
                }
                default -> throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected 'then','->', or '{'", this.currentToken.startPos, this.currentToken.endPos);
            }
        }
        this.recede(reverse);
        return new TwoValues<>(cases, null);
    }

    private Node whileExpression() throws CodeError {
        this.advance();
        if (this.currentHasNoBracket())
            throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected 'while (...)'", this.currentToken.startPos, this.currentToken.endPos);
        Node condition = this.expression();
        if (this.currentToken.type != Token.Type.LEFT_CURLY_BRACKET)
            throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected '{'", this.currentToken.startPos, this.currentToken.endPos);
        this.advance();
        Node statement = this.statements();
        if (this.currentToken.type != Token.Type.RIGHT_CURLY_BRACKET)
            throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected '}'", this.currentToken.startPos, this.currentToken.endPos);
        this.advance();
        return new WhileNode(condition, statement);
    }
    
    private int functionLambdaIndex = 1;
    private Node functionDefinition(boolean isLambda) throws CodeError {
        List<Token> argumentNameTokens = new ArrayList<>();
        Token variableNameToken;
        this.advance();
        
        if(isLambda) {
            variableNameToken = new Token(Token.Type.IDENTIFIER, "%d$lambda".formatted(functionLambdaIndex++), this.currentToken.startPos, this.currentToken.endPos);
        } else {
            if (this.currentToken.type == Token.Type.IDENTIFIER) {
                variableNameToken = currentToken;
                this.advance();
            } else {
                throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected function name", this.currentToken.startPos, this.currentToken.endPos);
            }
        }
        
        
        if (this.currentHasNoBracket())
            throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected '(...)'", this.currentToken.startPos, this.currentToken.endPos);
        this.advance();
        if (this.currentToken.type == Token.Type.IDENTIFIER) {
            argumentNameTokens.add(this.currentToken);
            this.advance();
            while (this.currentToken.type == Token.Type.COMMA) {
                this.advance();
                if (this.currentToken.type != Token.Type.IDENTIFIER)
                    throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected Identifier", this.currentToken.startPos, this.currentToken.endPos);
                argumentNameTokens.add(this.currentToken);
                this.advance();
            }
        }
        if (this.currentToken.type != Token.Type.RIGHT_BRACKET){
            throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected ',' or ')'", this.currentToken.startPos, this.currentToken.endPos);
        }
        this.advance();
        if (this.currentToken.type != Token.Type.LEFT_CURLY_BRACKET)
            throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected '{'", this.currentToken.startPos, this.currentToken.endPos);
        this.advance();
        Node returnNode = this.statements();
        if (this.currentToken.type != Token.Type.RIGHT_CURLY_BRACKET)
            throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected '}'", this.currentToken.startPos, this.currentToken.endPos);
        this.advance();
        return new FunctionNode(variableNameToken, argumentNameTokens, returnNode);
    }

    private Node listExpression() throws CodeError {
        List<Node> elementList = new ArrayList<>();
        Position startPos = this.currentToken.startPos;
        this.advance();
        if (this.currentToken.type != Token.Type.RIGHT_SQUARE_BRACKET) {
            elementList.add(this.expression());
            while (this.currentToken.type == Token.Type.COMMA) {
                this.advance();
                elementList.add(this.expression());
            }
            if (this.currentToken.type != Token.Type.RIGHT_SQUARE_BRACKET)
                throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected a ']'", this.currentToken.startPos, this.currentToken.endPos);
        }
        this.advance();
        return new ListNode(elementList, startPos, this.currentToken.endPos);
    }


    private boolean currentHasNoBracket() {
        return this.currentToken.type != Token.Type.LEFT_BRACKET;
    }
 }