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

	public Node parse() throws CodeError {
		List<Node> statements = new ArrayList<>();
		Position startPos = this.currentToken.startPos;
		
		// The initial program contains only special statements
		while (this.currentToken.type != Token.Type.FINISH) {
			// Remove all semicolons
			while (this.currentToken.type == Token.Type.SEMICOLON) {
				this.advance();
			}
			
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
	
				while (this.currentToken.type == Token.Type.SEMICOLON) {
					this.advance();
				}
	
				do {
					statements.add(this.statement());
					while (this.currentToken.type == Token.Type.SEMICOLON) {
						this.advance();
					}
				}
				while (this.currentToken.type != Token.Type.RIGHT_CURLY_BRACKET && this.currentToken.type != Token.Type.FINISH);

				this.throwIfNotType(Token.Type.RIGHT_CURLY_BRACKET, "Expected '}'");
	
				this.advance();
			}
			default -> statements.add(this.statement());
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
			case TRY -> {
				return this.tryExpression();
			}
			case FOREACH -> {
				return this.forEachExpression();
			}
		}
		
		Node node = this.expression();
		this.throwIfNotType(Token.Type.SEMICOLON, "Expected ; at end of line");

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
			switch (this.currentToken.type) {
				case ASSIGN_OPERATOR -> {
					this.recede();
					return setVariable();
				}
				case INCREMENT, DECREMENT -> {
					this.recede();
					return modifyVariable();
				}
			}
			this.recede();
		}
		
		return sizeComparisonExpression();
	}
	
	private VariableAssignNode setVariable() throws CodeError {
		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected an identifier");
		Token variableName = this.currentToken;
		if (this.context.isBuiltInFunction(variableName.content)) {
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				"Cannot override builtIn function %s()".formatted(variableName),
				variableName.startPos,
				variableName.endPos
			);
		}
		
		this.advance();
		this.throwIfNotType(Token.Type.ASSIGN_OPERATOR, "Expected an assignment operator");
		this.advance();
		Node expression = this.expression();
		
		this.context.setVariable(variableName.content, new NullValue());
		return new VariableAssignNode(variableName, expression);
	}

	private VariableAssignNode modifyVariable() throws CodeError {
		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected an identifier");
		Token variableName = this.currentToken;
		if (this.context.isBuiltInFunction(variableName.content)) {
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				"Cannot modify builtIn function %s()".formatted(variableName),
				variableName.startPos,
				variableName.endPos
			);
		}
		Node member = this.member();
		Token operatorToken = this.currentToken;
		Token.Type operatorType = switch (this.currentToken.type) {
			case INCREMENT -> Token.Type.PLUS;
			case DECREMENT -> Token.Type.MINUS;
			default -> throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Unknown unary memory operator", operatorToken.startPos, operatorToken.endPos);
		};
		
		this.advance();
		Node numberNode = new NumberNode(new Token(Token.Type.FLOAT, "1", operatorToken.startPos, operatorToken.endPos));
		
		this.context.setVariable(variableName.content, new NullValue());
		return new VariableAssignNode(variableName,
			new BinaryOperatorNode(member, new Token(operatorType, operatorToken.startPos, operatorToken.endPos), numberNode)
		);
	}

	private Node ifExpression() throws CodeError {
		this.advance();
		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected 'if (...)'");
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
		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected 'while (...)'");
		Node condition = this.expression();
		Node statement = this.statements();
		return new WhileNode(condition, statement);
	}
	
	private static int functionLambdaIndex = 1;
	private Node functionDefinition(boolean isLambda) throws CodeError {
		List<String> argumentNameTokens = new ArrayList<>();
		Token variableNameToken;
		this.advance();
		
		if (isLambda) {
			variableNameToken = new Token(
				Token.Type.IDENTIFIER, "%d$lambda".formatted(functionLambdaIndex++),
				this.currentToken.startPos,
				this.currentToken.endPos
			);
		}
		else {
			throwIfNotType(Token.Type.IDENTIFIER, "Expected function name");

			variableNameToken = this.currentToken;
			if (this.context.isBuiltInFunction(variableNameToken.content)) {
				throw new CodeError(
					CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Cannot override builtIn function %s()".formatted(variableNameToken),
					variableNameToken.startPos,
					variableNameToken.endPos
				);
			}

			this.advance();
		}
		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected '(...)'");
		this.advance();
		
		this.context.pushScope(this.currentToken.startPos);
		FunctionValueDelegate valueDelegate = new FunctionValueDelegate();
		this.context.setLocal(variableNameToken.content, valueDelegate);
		
		if (this.currentToken.type == Token.Type.IDENTIFIER) {
			this.recede();
			do {
				this.advance();
				this.throwIfNotType(Token.Type.IDENTIFIER, "Expected Identifier");
				
				argumentNameTokens.add(this.currentToken.content);
				this.context.setLocal(this.currentToken.content, new NullValue());
				this.advance();
			}
			while (this.currentToken.type == Token.Type.COMMA);
		}
		this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ',' or ')'");

		this.advance();
		
		// add Function variable to set
		Node statements = this.statements();
		this.context.popScope();
		
		FunctionNode functionNode = new FunctionNode(variableNameToken, argumentNameTokens, statements);
		valueDelegate.setDelegate(functionNode.functionValue);
		this.context.setVariable(variableNameToken.content, functionNode.functionValue);
		return functionNode;
	}

	private Node tryExpression() throws CodeError {
		this.advance();
		Node tryStatements = this.statements();

		this.throwIfNotType(Token.Type.CATCH, "Expected 'catch' after 'try'");
		this.advance();

		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected '(...)'");
		this.advance();

		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected Identifier");
		String errorParameterName = this.currentToken.content;
		this.context.setLocal(errorParameterName, new NullValue());
		this.advance();

		this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ')'");
		this.advance();

		Node catchStatements = this.statements();
		return new TryNode(tryStatements, catchStatements, errorParameterName);
	}

	private Node forEachExpression() throws CodeError {
		this.advance();

		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected '(...)'");
		this.advance();

		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected Identifier");
		String forParameterName = this.currentToken.content;
		this.context.setLocal(forParameterName, new NullValue());
		this.advance();

		this.throwIfNotType(Token.Type.COLON, "Expected ':'");
		this.advance();

		Node member = this.member();

		this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ')'");
		this.advance();

		Node statements = this.statements();
		return new ForNode(member, statements, forParameterName);
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
			this.throwIfNotType(Token.Type.RIGHT_SQUARE_BRACKET, "Expected a ']'");
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
		while (Token.Type.comparisonTokens.contains(this.currentToken.type)) {
			Token operatorToken = this.currentToken;
			this.advance();
			Node right = this.arithmeticExpression();
			left = new BinaryOperatorNode(left, operatorToken, right);
		}
		return left;
	}
	
	private Node arithmeticExpression() throws CodeError {
		Node left = this.term();
		while (this.currentToken.type == Token.Type.PLUS || this.currentToken.type == Token.Type.MINUS) {
			Token operatorToken = this.currentToken;
			this.advance();
			Node right = this.term();
			left = new BinaryOperatorNode(left, operatorToken, right);
		}
		return left;
	}
	
	private Node term() throws CodeError {
		Node left = this.factor();
		while (this.currentToken.type == Token.Type.MULTIPLY || this.currentToken.type == Token.Type.DIVIDE) {
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
		while (this.currentToken.type == Token.Type.POWER) {
			Token operatorToken = this.currentToken;
			this.advance();
			Node right = this.factor();
			left = new BinaryOperatorNode(left, operatorToken, right);
		}
		return left;
	}
	
	private Node call() throws CodeError {
		List<Node> argumentNodes = new ArrayList<>();
		Node member = this.member();
		if (this.currentToken.type != Token.Type.LEFT_BRACKET)
			return member;
		this.advance();
		if (this.currentToken.type != Token.Type.RIGHT_BRACKET) {
			argumentNodes.add(this.expression());
			while (this.currentToken.type == Token.Type.COMMA) {
				this.advance();
				argumentNodes.add(this.expression());
			}
			this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected a ')'");
		}
		this.advance();
		return new CallNode(member, argumentNodes);
	}

	private Node member() throws CodeError {
		Node left = this.atom();

		while (this.currentToken.type == Token.Type.DOT) {
			this.advance();
			List<Node> argumentNodes = new ArrayList<>();
			Node right = this.member();
			if (this.currentToken.type == Token.Type.LEFT_BRACKET) {
				this.advance();
				if (this.currentToken.type != Token.Type.RIGHT_BRACKET) {
					argumentNodes.add(this.expression());
					while (this.currentToken.type == Token.Type.COMMA) {
						this.advance();
						argumentNodes.add(this.expression());
					}
					this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected a ')'");
				}
				this.advance();
			}
			left = new MemberCallNode(left, right, argumentNodes);
		}

		return left;
	}

	private Node atom() throws CodeError {
		Token token = this.currentToken;
		switch (token.type) {
			case IDENTIFIER -> {
				// this needs to properly support member functions
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
				this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ')'");
				this.advance();
				return expression;
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

	private void throwIfNotType(Token.Type type, String errorMessage) throws CodeError {
		if (this.currentToken.type != type) {
			throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, errorMessage, this.currentToken.startPos, this.currentToken.endPos);
		}
	}

}
