package me.senseiwells.arucas.core;

import me.senseiwells.arucas.utils.*;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.nodes.*;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.FunctionValueDelegate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Parser {

	private final List<Token> tokens;
	private final Context context;
	private int operatorTokenIndex;
	private Token currentToken;

	public Parser(List<Token> tokens, Context context) {
		this.tokens = tokens;
		this.operatorTokenIndex = -1;
		this.advance();
		this.context = context.createChildContext("Parser Context");
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
		List<Node> statements = new ArrayList<>();
		Position startPos = this.currentToken.startPos;
		
		// The initial program contains only special statements
		while (this.currentToken.type != Token.Type.FINISH) {
			// Remove all semicolons
			while (this.currentToken.type == Token.Type.SEMICOLON)
				this.advance();
			
			statements.add(this.statement());
		}
		
		return new ListNode(statements, startPos, this.currentToken.endPos);
	}

	private Node statements() throws CodeError {
		List<Node> statements = new ArrayList<>();
		Position startPos = this.currentToken.startPos;
		
		this.context.pushScope(startPos);
		
		switch (this.currentToken.type) {
			case FINISH, SEMICOLON -> {
				this.advance();
				this.context.popScope();
				return new NullNode(this.currentToken);
			}
			
			case LEFT_CURLY_BRACKET -> {
				this.advance();
				
				if (this.currentToken.type == Token.Type.RIGHT_CURLY_BRACKET) {
					this.advance();
					this.context.popScope();
					return new NullNode(this.currentToken);
				}
	
				while (this.currentToken.type == Token.Type.SEMICOLON)
					this.advance();
	
				do {
					statements.add(this.statement());
					while (this.currentToken.type == Token.Type.SEMICOLON)
						this.advance();
				}
				while (this.currentToken.type != Token.Type.RIGHT_CURLY_BRACKET && this.currentToken.type != Token.Type.FINISH);
	
				if (this.currentToken.type != Token.Type.RIGHT_CURLY_BRACKET)
					throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected '}'", this.currentToken.startPos, this.currentToken.endPos);
	
				this.advance();
			}
			
			default -> {
				// Read one statement only
				statements.add(this.statement());
			}
		}
		
		this.context.popScope();
		return new ScopeNode(statements, startPos, this.currentToken.endPos);
	}

	private Node statement() throws CodeError {
		// Statements defined in this switch will not need a semicolon
		switch (this.currentToken.type) {
			case LEFT_CURLY_BRACKET -> {
				return this.statements();
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
		switch (this.currentToken.type) {
			case RETURN -> {
				this.advance();
				if (this.currentToken.type == Token.Type.SEMICOLON) {
					return new ReturnNode(new NullNode(this.currentToken), startPos, this.currentToken.endPos);
				}
				
				Node expression = this.sizeComparisonExpression();
				return new ReturnNode(expression, startPos, this.currentToken.endPos);
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
			this.advance();
			return this.setVariable();
		}
		//If identifier is already a variable -> can assign value without 'var' keyword
		else if (this.currentToken.type == Token.Type.IDENTIFIER) {
			this.advance();
			if (this.currentToken.type == Token.Type.ASSIGN_OPERATOR) {
				this.recede();
				return setVariable();
			}
			this.recede();
		}
		
		return sizeComparisonExpression();
	}
	
	private VariableAssignNode setVariable() throws CodeError {
		if (this.currentToken.type != Token.Type.IDENTIFIER)
			throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected an identifier", this.currentToken.startPos, this.currentToken.endPos);
		Token variableName = this.currentToken;
		if (this.context.isBuiltInFunction(variableName.content))
			throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Cannot override builtIn function " + variableName + "()", variableName.startPos, variableName.endPos);
		
		this.advance();
		if (this.currentToken.type != Token.Type.ASSIGN_OPERATOR)
			throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected an assignment operator", this.currentToken.startPos, this.currentToken.endPos);
		this.advance();
		Node expression = this.expression();
		
		this.context.setVariable(variableName.content, new NullValue());
		return new VariableAssignNode(variableName, expression);
	}

	private Node ifExpression() throws CodeError {
		if (this.currentToken.type != Token.Type.IF)
			throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected '" + Token.Type.IF + "'", this.currentToken.startPos, this.currentToken.endPos);
		this.advance();
		if (this.currentToken.type != Token.Type.LEFT_BRACKET)
			throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected 'if (...)'", this.currentToken.startPos, this.currentToken.endPos);
		Node condition = this.expression();
		Node statement = this.statements();
		
		if (this.currentToken.type == Token.Type.ELSE) {
			this.advance();
			
			Node elseStatement = this.statements();
			return new IfNode(condition, statement, elseStatement);
		}
		
		NullNode nullNode = new NullNode(this.currentToken);
		return new IfNode(condition, statement, nullNode);
	}

	private Node whileExpression() throws CodeError {
		this.advance();
		if (this.currentToken.type != Token.Type.LEFT_BRACKET)
			throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected 'while (...)'", this.currentToken.startPos, this.currentToken.endPos);
		Node condition = this.expression();
		Node statement = this.statements();
		return new WhileNode(condition, statement);
	}
	
	private static int functionLambdaIndex = 1;
	private Node functionDefinition(boolean isLambda) throws CodeError {
		List<Token> argumentNameTokens = new ArrayList<>();
		Token variableNameToken;
		this.advance();
		
		if (isLambda) {
			variableNameToken = new Token(Token.Type.IDENTIFIER, "%d$lambda".formatted(functionLambdaIndex++), this.currentToken.startPos, this.currentToken.endPos);
		}
		else {
			if (this.currentToken.type == Token.Type.IDENTIFIER) {
				variableNameToken = this.currentToken;
				
				if (this.context.isBuiltInFunction(variableNameToken.content))
					throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Cannot override builtIn function " + variableNameToken + "()", variableNameToken.startPos, variableNameToken.endPos);
				 
				this.advance();
			}
			else {
				throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected function name", this.currentToken.startPos, this.currentToken.endPos);
			}
		}
		if (this.currentToken.type != Token.Type.LEFT_BRACKET)
			throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected '(...)'", this.currentToken.startPos, this.currentToken.endPos);
		this.advance();
		
		this.context.pushScope(this.currentToken.startPos);
		FunctionValueDelegate valueDelegate = new FunctionValueDelegate();
		this.context.setLocal(variableNameToken.content, valueDelegate);
		
		if (this.currentToken.type == Token.Type.IDENTIFIER) {
			this.recede();
			do {
				this.advance();
				if (this.currentToken.type != Token.Type.IDENTIFIER)
					throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected Identifier", this.currentToken.startPos, this.currentToken.endPos);
				
				argumentNameTokens.add(this.currentToken);
				this.context.setLocal(this.currentToken.content, new NullValue());
				this.advance();
			}
			while (this.currentToken.type == Token.Type.COMMA);
		}
		if (this.currentToken.type != Token.Type.RIGHT_BRACKET) {
			throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected ',' or ')'", this.currentToken.startPos, this.currentToken.endPos);
		}
		this.advance();
		
		// add Function variable to set
		Node statements = this.statements();
		this.context.popScope();
		
		FunctionNode functionNode = new FunctionNode(variableNameToken, argumentNameTokens, statements);
		valueDelegate.setDelegate(functionNode.functionValue);
		this.context.setVariable(variableNameToken.content, functionNode.functionValue);
		return functionNode;
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
	
	private Node sizeComparisonExpression() throws CodeError {
		Node left = this.comparisonExpression();
		
		while (this.currentToken.type == Token.Type.AND || this.currentToken.type == Token.Type.OR) {
			Token operatorToken = this.currentToken;
			this.advance();
			Node right = this.comparisonExpression();
			left = new BinaryOperatorNode(left, operatorToken, right);
		}
		return left;
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
	
	private Node call() throws CodeError {
		List<Node> argumentNodes = new ArrayList<>();
		Node atom = this.atom();
		if (this.currentToken.type != Token.Type.LEFT_BRACKET)
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
				Value<?> value = this.context.getVariable(token.content);
				if (value == null) {
					throw new CodeError(CodeError.ErrorType.UNKNOWN_IDENTIFIER, "Could not find '" + token.content + "'", token.startPos, token.endPos);
				}
				
				if (value instanceof FunctionValue) {
					return new DirectAccessNode(token, value);
				}
				
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
				throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Invalid conditional statement", this.currentToken.startPos, this.currentToken.endPos);
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
	
}
