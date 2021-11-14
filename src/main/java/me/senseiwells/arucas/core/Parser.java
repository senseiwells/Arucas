package me.senseiwells.arucas.core;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.utils.*;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.nodes.*;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.MemberFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private Token getLastToken() {
		int index = this.operatorTokenIndex - 1;
		return this.tokens.get(index < 0 ? 0 : (index >= this.tokens.size() ? this.tokens.size() - 1 : index));
	}

	public Node parse() throws CodeError {
		List<Node> statements = new ArrayList<>();
		ISyntax startPos = this.currentToken.syntaxPosition;
		
		// The initial program contains only special statements
		while (this.currentToken.type != Token.Type.FINISH) {
			// Remove all semicolons
			while (this.currentToken.type == Token.Type.SEMICOLON) {
				this.advance();
			}
			
			statements.add(this.statement());
		}
		
		return new ListNode(statements, startPos, this.currentToken.syntaxPosition);
	}

	private Node statements() throws CodeError {
		List<Node> statements = new ArrayList<>();
		ISyntax startPos = this.currentToken.syntaxPosition;
		
		this.context.pushScope(this.currentToken.syntaxPosition);
		
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
		return new ScopeNode(statements, startPos, this.currentToken.syntaxPosition);
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
		ISyntax startPos = this.currentToken.syntaxPosition;
		switch (this.currentToken.type) {
			case RETURN -> {
				this.advance();
				if (this.currentToken.type == Token.Type.SEMICOLON) {
					return new ReturnNode(new NullNode(this.currentToken), startPos, startPos);
				}
				
				Node expression = this.sizeComparisonExpression();
				return new ReturnNode(expression, startPos, this.currentToken.syntaxPosition);
			}
			case CONTINUE -> {
				this.advance();
				return new ContinueNode(startPos);
			}
			case BREAK -> {
				this.advance();
				return new BreakNode(startPos);
			}
		}
		
		// Initialise variable with keyword 'var' -> stores value in map
		if (this.currentToken.type == Token.Type.VAR) {
			this.advance();
			return this.setVariable();
		}
		// If identifier is already a variable -> can assign value without 'var' keyword
		else if (this.currentToken.type == Token.Type.IDENTIFIER) {
			this.advance();
			switch (this.currentToken.type) {
				case ASSIGN_OPERATOR -> {
					this.recede();
					return this.setVariable();
				}
				case INCREMENT, DECREMENT -> {
					this.recede();
					return this.modifyVariable();
				}
			}
			this.recede();
		}
		
		return this.sizeComparisonExpression();
	}
	
	private VariableAssignNode setVariable() throws CodeError {
		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected an identifier");
		Token variableName = this.currentToken;
		if (this.context.isBuiltInFunction(variableName.content)) {
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				"Cannot override built in function %s()".formatted(variableName.content),
				variableName.syntaxPosition
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
				variableName.syntaxPosition
			);
		}
		
		Node member = this.member(false);
		Token operatorToken = this.currentToken;
		Token.Type operatorType = switch (this.currentToken.type) {
			case INCREMENT -> Token.Type.PLUS;
			case DECREMENT -> Token.Type.MINUS;
			default -> throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Unknown unary memory operator", operatorToken.syntaxPosition);
		};
		
		this.advance();
		Node numberNode = new NumberNode(new Token(Token.Type.NUMBER, "1", operatorToken.syntaxPosition));
		
		this.context.setVariable(variableName.content, new NullValue());
		return new VariableAssignNode(variableName,
			new BinaryOperatorNode(member, new Token(operatorType, operatorToken.syntaxPosition), numberNode)
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
		Token functionStartToken = this.currentToken;
		this.advance();
		List<String> argumentNameTokens = new ArrayList<>();
		Token variableNameToken;
		
		if (isLambda) {
			variableNameToken = new Token(
				Token.Type.IDENTIFIER, "%d$lambda".formatted(functionLambdaIndex++),
				this.currentToken.syntaxPosition
			);
		}
		else {
			throwIfNotType(Token.Type.IDENTIFIER, "Expected function name");

			variableNameToken = this.currentToken;
			if (this.context.isBuiltInFunction(variableNameToken.content)) {
				throw new CodeError(
					CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Cannot override builtIn function %s()".formatted(variableNameToken),
					variableNameToken.syntaxPosition
				);
			}

			this.advance();
		}
		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected 'fun (...)'");
		this.advance();
		
		this.context.pushScope(this.currentToken.syntaxPosition);
		
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
		
		FunctionNode functionNode = new FunctionNode(functionStartToken, variableNameToken, argumentNameTokens);
		this.context.setLocal(variableNameToken.content, functionNode.functionValue);
		
		Node statements = this.statements();
		this.context.popScope();
		
		functionNode.complete(statements);
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

		Node member = this.member(false);

		this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ')'");
		this.advance();

		Node statements = this.statements();
		return new ForNode(member, statements, forParameterName);
	}

	private Node listExpression() throws CodeError {
		List<Node> elementList = new ArrayList<>();
		ISyntax startPos = this.currentToken.syntaxPosition;
		this.advance();
		if (this.currentToken.type != Token.Type.RIGHT_SQUARE_BRACKET) {
			elementList.add(this.expression());
			while (this.currentToken.type == Token.Type.COMMA) {
				this.advance();
				elementList.add(this.expression());
			}
			this.throwIfNotType(Token.Type.RIGHT_SQUARE_BRACKET, "Expected a ']'");
		}
		ISyntax endPos = this.currentToken.syntaxPosition;
		this.advance();
		return new ListNode(elementList, startPos, endPos);
	}

	private Node mapExpression() throws CodeError {
		Map<Node, Node> elementMap = new HashMap<>();
		ISyntax startPos = this.currentToken.syntaxPosition;
		
		this.advance();
		if (this.currentToken.type != Token.Type.RIGHT_CURLY_BRACKET) {
			this.recede();
			do {
				this.advance();
				Node keyNode = this.expression();
				this.throwIfNotType(Token.Type.COLON, "Expected a ':' between key and value");
				this.advance();
				Node valueNode = this.expression();
				elementMap.put(keyNode, valueNode);
			}
			while (this.currentToken.type == Token.Type.COMMA);
			this.throwIfNotType(Token.Type.RIGHT_CURLY_BRACKET, "Expected a '}'");
		}
		ISyntax endPos = this.currentToken.syntaxPosition;
		this.advance();
		return new MapNode(elementMap, startPos, endPos);
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
		Node member = this.member(false);
		if (this.currentToken.type != Token.Type.LEFT_BRACKET) {
			return member;
		}
		
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
		return member(new CallNode(member, argumentNodes));
	}

	private Node member(boolean isMember) throws CodeError {
		Node left = this.atom(isMember);
		return member(left);
	}

	private Node member(Node left) throws CodeError {
		while (this.currentToken.type == Token.Type.DOT) {
			this.advance();
			List<Node> argumentNodes = new ArrayList<>();
			Node right = this.member(true);
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

	private Node atom(boolean isMember) throws CodeError {
		Token token = this.currentToken;
		switch (token.type) {
			case IDENTIFIER -> {
				// this needs to properly support member functions
				this.advance();
				Value<?> value = this.context.getVariable(token.content);
				if (value == null) {
					throw new CodeError(CodeError.ErrorType.UNKNOWN_IDENTIFIER, "Could not find '%s'".formatted(token.content), token.syntaxPosition);
				}
				
				if (value instanceof MemberFunction && !isMember) {
					throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Members must be called from Values", token.syntaxPosition);
				}
				
				if (value instanceof FunctionValue) {
					return new DirectAccessNode(token, value);
				}
				
				return new VariableAccessNode(token);
			}
			case NUMBER -> {
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
				return this.functionDefinition(true);
			}
			case LEFT_CURLY_BRACKET -> {
				return this.mapExpression();
			}
		}
		throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Unexpected Token: %s".formatted(token), this.currentToken.syntaxPosition);
	}

	private void throwIfNotType(Token.Type type, String errorMessage) throws CodeError {
		if (this.currentToken.type != type) {
			/*
			 * Because we want to tell the user that we expected a token after the previous one
			 * we should instead of telling them that the next token position was wrong tell them
			 * at the exact location it did not exist.
			 *
			 * This means that we need to read the previous token and then get the position of the
			 * last index of that token.
			 */
			Token lastToken = getLastToken();
			ISyntax lastTokenPosition = ISyntax.lastOf(lastToken.syntaxPosition);
			throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, errorMessage, lastTokenPosition);
		}
	}

}
