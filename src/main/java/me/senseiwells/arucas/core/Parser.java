package me.senseiwells.arucas.core;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.nodes.*;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.MutableSyntaxImpl;
import me.senseiwells.arucas.utils.StringUtils;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.classes.ArucasClassDefinition;
import me.senseiwells.arucas.values.functions.ClassMemberFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.UserDefinedFunction;

import java.util.*;

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
				return this.ifStatement();
			}
			case WHILE -> {
				return this.whileStatement();
			}
			case CLASS -> {
				return this.classStatement();
			}
			case FUN -> {
				return this.functionDefinition(false);
			}
			case TRY -> {
				return this.tryStatement();
			}
			case FOREACH -> {
				return this.forEachStatement();
			}
			case SWITCH -> {
				return this.switchStatement();
			}
		}
		
		ISyntax startPos = this.currentToken.syntaxPosition;
		Node node = switch (this.currentToken.type) {
			case RETURN -> {
				this.advance();
				if (this.currentToken.type == Token.Type.SEMICOLON) {
					yield new ReturnNode(new NullNode(this.currentToken), startPos, startPos);
				}
				
				Node expression = this.sizeComparisonExpression();
				yield new ReturnNode(expression, startPos, this.currentToken.syntaxPosition);
			}
			case CONTINUE -> {
				this.advance();
				yield new ContinueNode(startPos);
			}
			case BREAK -> {
				this.advance();
				yield new BreakNode(startPos);
			}
			default -> this.expression();
		};
		
		this.throwIfNotType(Token.Type.SEMICOLON, "Expected ; at end of line");
		this.advance();
		return node;
	}
	
	private ArucasClassNode classStatement() throws CodeError {
		ISyntax startPos = this.currentToken.syntaxPosition;
		this.advance();
		
		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected an identifier");
		Token className = this.currentToken;
		this.context.throwIfClassNameTaken(className.content, className.syntaxPosition);
		this.advance();
		
		this.throwIfNotType(Token.Type.LEFT_CURLY_BRACKET, "Expected '{'");
		this.advance();

		// Push the stack definition so that we can detect it from identifiers
		ArucasClassDefinition definition = new ArucasClassDefinition(className.content);
		this.context.addClassDefinition(definition);

		// Push scopes to declare class body
		this.context.pushScope(startPos);

		
		while (this.currentToken.type != Token.Type.RIGHT_CURLY_BRACKET) {
			// [Static/Member] variables
			// [Static/Member] methods
			// Constructors
			
			boolean isStatic = this.currentToken.type == Token.Type.STATIC;
			if (isStatic) {
				this.advance();
			}
			
			switch (this.currentToken.type) {
				case VAR -> {
					this.advance();
					this.throwIfNotType(Token.Type.IDENTIFIER, "Expected an identifier");
					Token token = this.currentToken;
					this.advance();

					switch (this.currentToken.type) {
						case ASSIGN_OPERATOR -> {
							this.advance();
							definition.addMemberVariableNode(isStatic, token.content, this.sizeComparisonExpression());
							this.throwIfNotType(Token.Type.SEMICOLON, "Expected ';'");
							this.advance();
						}
						case SEMICOLON -> {
							definition.addMemberVariableNode(isStatic, token.content, new NullNode(this.currentToken));
							this.advance();
						}
					}
				}
				case IDENTIFIER -> {
					// This could either be a constructor or a member variable
					
					Token token = this.currentToken;
					this.advance();

					switch (this.currentToken.type) {
						case ASSIGN_OPERATOR -> {
							this.advance();
							definition.addMemberVariableNode(isStatic, token.content, this.sizeComparisonExpression());
							this.throwIfNotType(Token.Type.SEMICOLON, "Expected ';'");
							this.advance();
						}
						case LEFT_BRACKET -> {
							if (!token.content.equals(definition.getName())) {
								throw new CodeError(
									CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR,
									"Constructor must have the same name as class",
									this.currentToken.syntaxPosition
								);
							}
							ClassMemberFunction constructor = this.classConstructor(isStatic, token.content);
							definition.addConstructor(constructor);
						}
						case SEMICOLON -> {
							definition.addMemberVariableNode(isStatic, token.content, new NullNode(this.currentToken));
							this.advance();
						}
					}
				}
				case FUN -> {
					if (!isStatic) {
						ClassMemberFunction method = this.classMethod();
						definition.addMethod(method);
					}
					else {
						UserDefinedFunction method = this.staticClassMethod();
						definition.addStaticMethod(method);
					}
				}
				case OPERATOR -> {
					this.advance();
					Token token = this.currentToken;
					ClassMemberFunction operatorMethod = this.operatorMethod(isStatic, token);
					definition.addOperatorMethod(token.type, operatorMethod);
				}
				case LEFT_CURLY_BRACKET -> definition.addStaticInitialiser(this.statements());
				default -> throw new CodeError(
					CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR,
					"Expected Identifier or function",
					this.currentToken.syntaxPosition
				);
			}
		}
		
		this.context.popScope();
		this.throwIfNotType(Token.Type.RIGHT_CURLY_BRACKET, "Expected '}'");
		ISyntax endPos = this.currentToken.syntaxPosition;
		this.advance();
		return new ArucasClassNode(definition, startPos, endPos);
	}

	private ClassMemberFunction classConstructor(boolean isStatic, String name) throws CodeError {
		ISyntax startPos = this.currentToken.syntaxPosition;
		if (isStatic) {
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				"Cannot have a static constructor",
				startPos
			);
		}
		this.advance();

		this.context.pushScope(this.currentToken.syntaxPosition);

		List<String> argumentNames = this.getClassMemberArguments();

		MutableSyntaxImpl syntaxPosition = new MutableSyntaxImpl(startPos.getStartPos(), null);
		ClassMemberFunction classConstructor = new ClassMemberFunction(name, argumentNames, syntaxPosition);
		this.context.setLocal(name, classConstructor);

		Node statements = this.statements();
		this.context.popScope();

		classConstructor.complete(statements);
		syntaxPosition.end = statements.syntaxPosition.getEndPos();

		return classConstructor;
	}
	
	private ClassMemberFunction classMethod() throws CodeError {
		ISyntax startPos = this.currentToken.syntaxPosition;
		this.advance();
		
		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected method name");
		Token variableNameToken = this.currentToken;
		this.advance();
		
		// Make sure that we can parameter length overload class functions!
		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected '('");
		this.advance();
		
		this.context.pushScope(this.currentToken.syntaxPosition);
		
		List<String> argumentNames = this.getClassMemberArguments();
		
		MutableSyntaxImpl syntaxPosition = new MutableSyntaxImpl(startPos.getStartPos(), null);
		ClassMemberFunction classMethod = new ClassMemberFunction(variableNameToken.content, argumentNames, syntaxPosition);
		this.context.setLocal(variableNameToken.content, classMethod);
		
		Node statements = this.statements();
		this.context.popScope();
		
		classMethod.complete(statements);
		syntaxPosition.end = statements.syntaxPosition.getEndPos();
		
		return classMethod;
	}

	private List<String> getClassMemberArguments() throws CodeError {
		List<String> argumentNames = new ArrayList<>();
		argumentNames.add("this");
		this.context.setLocal("this", new NullValue());

		if (this.currentToken.type == Token.Type.IDENTIFIER) {
			this.recede();
			do {
				this.advance();
				this.throwIfNotType(Token.Type.IDENTIFIER, "Expected Identifier");

				argumentNames.add(this.currentToken.content);
				this.context.setLocal(this.currentToken.content, new NullValue());
				this.advance();
			}
			while (this.currentToken.type == Token.Type.COMMA);
		}
		this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ',' or ')'");
		this.advance();
		return argumentNames;
	}

	private UserDefinedFunction staticClassMethod() throws CodeError {
		ISyntax startPos = this.currentToken.syntaxPosition;
		this.advance();

		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected method name");
		Token variableNameToken = this.currentToken;
		this.advance();

		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected '('");
		this.advance();

		this.context.pushScope(this.currentToken.syntaxPosition);

		List<String> argumentNames = new ArrayList<>();
		if (this.currentToken.type == Token.Type.IDENTIFIER) {
			this.recede();
			do {
				this.advance();
				this.throwIfNotType(Token.Type.IDENTIFIER, "Expected Identifier");

				argumentNames.add(this.currentToken.content);
				this.context.setLocal(this.currentToken.content, new NullValue());
				this.advance();
			}
			while (this.currentToken.type == Token.Type.COMMA);
		}
		this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ',' or ')'");
		this.advance();

		MutableSyntaxImpl syntaxPosition = new MutableSyntaxImpl(startPos.getStartPos(), null);
		UserDefinedFunction staticClassMethod = new UserDefinedFunction(variableNameToken.content, argumentNames, syntaxPosition);
		this.context.setLocal(variableNameToken.content, staticClassMethod);

		Node statements = this.statements();
		this.context.popScope();

		staticClassMethod.complete(statements);
		syntaxPosition.end = statements.syntaxPosition.getEndPos();

		return staticClassMethod;
	}

	private ClassMemberFunction operatorMethod(boolean isStatic, Token token) throws CodeError {
		ISyntax startPos = token.syntaxPosition;
		if (isStatic) {
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				"Cannot have a static operator method",
				startPos
			);
		}
		if (!ValueOperations.overridableOperatorTokens.containsKey(token.type)) {
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				"Cannot override operator %s".formatted(this.currentToken.type),
				startPos
			);
		}
		this.advance();

		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected '('");
		this.advance();

		this.context.pushScope(this.currentToken.syntaxPosition);
		List<String> argumentNames = this.getClassMemberArguments();

		int requiredParameters = ValueOperations.overridableOperatorTokens.get(token.type);
		if (argumentNames.size() != requiredParameters) {
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				"Operator %s requires %d parameters".formatted(token.type, requiredParameters - 1),
				startPos
			);
		}

		MutableSyntaxImpl syntaxPosition = new MutableSyntaxImpl(startPos.getStartPos(), null);
		ClassMemberFunction operatorMethod = new ClassMemberFunction("$%s".formatted(token.type), argumentNames, syntaxPosition);

		Node statements = this.statements();
		this.context.popScope();

		operatorMethod.complete(statements);
		syntaxPosition.end = statements.syntaxPosition.getEndPos();

		return operatorMethod;
	}
	
	private VariableAssignNode setVariable() throws CodeError {
		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected an identifier");
		Token variableName = this.currentToken;
		this.context.throwIfStackNameTaken(variableName.content, variableName.syntaxPosition);
		
		this.advance();
		this.throwIfNotType(Token.Type.ASSIGN_OPERATOR, "Expected an assignment operator");
		this.advance();
		Node expression = this.expression();
		
		this.context.setVariable(variableName.content, NullValue.NULL);
		return new VariableAssignNode(variableName, expression);
	}

	private VariableAssignNode modifyVariable() throws CodeError {
		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected an identifier");
		Token variableName = this.currentToken;
		this.context.throwIfStackNameTaken(variableName.content, variableName.syntaxPosition);
		
		Node member = this.member();
		Token operatorToken = this.currentToken;
		Token.Type operatorType = switch (this.currentToken.type) {
			case INCREMENT -> Token.Type.PLUS;
			case DECREMENT -> Token.Type.MINUS;
			default -> throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Unknown unary memory operator", operatorToken.syntaxPosition);
		};
		
		this.advance();
		Node numberNode = new NumberNode(new Token(Token.Type.NUMBER, "1", operatorToken.syntaxPosition));
		
		this.context.setVariable(variableName.content, NullValue.NULL);
		return new VariableAssignNode(variableName,
			new BinaryOperatorNode(member, new Token(operatorType, operatorToken.syntaxPosition), numberNode)
		);
	}

	private Node modifyMember(Node left, Node right) throws CodeError {
		Token operatorToken = this.currentToken;
		Token.Type operatorType = switch (operatorToken.type) {
			case INCREMENT -> Token.Type.PLUS;
			case DECREMENT -> Token.Type.MINUS;
			default -> throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Unknown unary memory operator", operatorToken.syntaxPosition);
		};

		this.advance();
		Node numberNode = new NumberNode(new Token(Token.Type.NUMBER, "1", operatorToken.syntaxPosition));

		return new MemberAssignNode(left, right,
			new BinaryOperatorNode(new MemberAccessNode(left, right), new Token(operatorType, operatorToken.syntaxPosition), numberNode)
		);
	}

	private Node modifyStatic(Token nameToken, AbstractClassDefinition definition) throws CodeError {
		Token operatorToken = this.currentToken;
		Token.Type operatorType = switch (operatorToken.type) {
			case INCREMENT -> Token.Type.PLUS;
			case DECREMENT -> Token.Type.MINUS;
			default -> throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Unknown unary memory operator", operatorToken.syntaxPosition);
		};

		this.advance();
		Node numberNode = new NumberNode(new Token(Token.Type.NUMBER, "1", operatorToken.syntaxPosition));

		return new StaticAssignNode(nameToken, definition,
			new BinaryOperatorNode(new StaticAccessNode(nameToken, definition), new Token(operatorType, operatorToken.syntaxPosition), numberNode)
		);
	}

	private Node ifStatement() throws CodeError {
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

	private Node whileStatement() throws CodeError {
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
			this.throwIfNotType(Token.Type.IDENTIFIER, "Expected function name");

			variableNameToken = this.currentToken;
			this.context.throwIfStackNameTaken(variableNameToken.content, variableNameToken.syntaxPosition);

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
				this.context.setLocal(this.currentToken.content, NullValue.NULL);
				this.advance();
			}
			while (this.currentToken.type == Token.Type.COMMA);
		}
		this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ',' or ')'");
		this.advance();
		
		FunctionNode functionNode = new FunctionNode(functionStartToken, variableNameToken, argumentNameTokens);
		this.context.setLocal(variableNameToken.content, functionNode.getFunctionValue());
		
		Node statements = this.statements();
		this.context.popScope();
		
		functionNode.complete(statements);
		this.context.setVariable(variableNameToken.content, functionNode.getFunctionValue());
		return functionNode;
	}
	
	private Node newDefinition() throws CodeError {
		ISyntax startPos = this.currentToken.syntaxPosition;
		this.advance();
		
		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected Identifier");
		Token className = this.currentToken;
		this.advance();
		
		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected '(...)'");
		this.advance();
		
		List<Node> arguments = new ArrayList<>();
		if (this.currentToken.type != Token.Type.RIGHT_BRACKET) {
			arguments.add(this.expression());
			while (this.currentToken.type == Token.Type.COMMA) {
				this.advance();
				arguments.add(this.expression());
			}
		}
		
		this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ')' or ','");
		
		ISyntax endPos = this.currentToken.syntaxPosition;
		this.advance();
		return new NewNode(className, arguments, startPos, endPos);
	}

	private Node tryStatement() throws CodeError {
		this.advance();
		Node tryStatements = this.statements();

		this.throwIfNotType(Token.Type.CATCH, "Expected 'catch' after 'try'");
		this.advance();

		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected '(...)'");
		this.advance();

		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected Identifier");
		String errorParameterName = this.currentToken.content;
		this.context.setLocal(errorParameterName, NullValue.NULL);
		this.advance();

		this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ')'");
		this.advance();

		Node catchStatements = this.statements();
		return new TryNode(tryStatements, catchStatements, errorParameterName);
	}

	private Node forEachStatement() throws CodeError {
		this.advance();

		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected '(...)'");
		this.advance();

		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected Identifier");
		String forParameterName = this.currentToken.content;
		this.context.setLocal(forParameterName, NullValue.NULL);
		this.advance();

		this.throwIfNotType(Token.Type.COLON, "Expected ':'");
		this.advance();

		Node member = this.member();

		this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ')'");
		this.advance();

		Node statements = this.statements();
		return new ForNode(member, statements, forParameterName);
	}

	private Node switchStatement() throws CodeError {
		ISyntax startPos = this.currentToken.syntaxPosition;
		this.advance();
		
		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected '(...)'");
		this.advance();
		
		Node valueNode = this.expression();
		
		this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ')'");
		this.advance();
		
		this.throwIfNotType(Token.Type.LEFT_CURLY_BRACKET, "Expected '{'");
		this.advance();
		
		Node defaultCase = null;
		Set<Value<?>> allValues = new HashSet<>();
		Token.Type valueType = null;
		Map<Node, Set<Value<?>>> cases = new LinkedHashMap<>();
		while (this.currentToken.type != Token.Type.RIGHT_CURLY_BRACKET) {
			if (this.currentToken.type == Token.Type.DEFAULT) {
				if (defaultCase != null) {
					throw new CodeError(
						CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Switch statements can only contain one default statement",
						this.currentToken.syntaxPosition
					);
				}
				
				this.advance();
				this.throwIfNotType(Token.Type.POINTER, "Expected '->' but got '%s'".formatted(this.currentToken.content));
				this.advance();
				
				defaultCase = this.statements();
				continue;
			}
			
			this.throwIfNotType(Token.Type.CASE, "Expected 'case'");
			this.advance();
			
			if (valueType == null) {
				if (this.currentToken.type == Token.Type.STRING
				|| this.currentToken.type == Token.Type.NUMBER) {
					valueType = this.currentToken.type;
				}
				else {
					throw new CodeError(
						CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Switch statements can only contain numbers and strings",
						this.currentToken.syntaxPosition
					);
				}
			}
			
			Set<Value<?>> values = new HashSet<>();
			while (true) {
				Token token = this.currentToken;
				this.throwIfNotType(valueType, "Expected a value of type '%s' but got '%s'".formatted(valueType, token.type));
				this.advance();
				
				Value<?> value = null;
				switch (valueType) {
					case NUMBER -> value = new NumberValue(Double.parseDouble(token.content));
					case STRING -> {
						try {
							value = new StringValue(StringUtils.unescapeString(token.content.substring(1, token.content.length() - 1)));
						}
						catch (RuntimeException e) {
							throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, e.getMessage(), token.syntaxPosition);
						}
					}
				}
				
				if (!allValues.add(value)) {
					// We do not allow multiple cases to have the same condition.
					throw new CodeError(
						CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Switch statements can not contain duplicate conditions. '%s'".formatted(token.content),
						token.syntaxPosition
					);
				}
				
				values.add(value);
				if (this.currentToken.type == Token.Type.COMMA) {
					this.advance();
					continue;
				}
				
				this.throwIfNotType(Token.Type.POINTER, "Expected '->' but got '%s'".formatted(this.currentToken.content));
				this.advance();
				break;
			}
			
			Node caseBody = this.statements();
			cases.put(caseBody, values);
		}
		
		this.throwIfNotType(Token.Type.RIGHT_CURLY_BRACKET, "Expected '}'");
		ISyntax endPos = this.currentToken.syntaxPosition;
		this.advance();
		return new SwitchNode(valueNode, defaultCase, cases, startPos, endPos);
	}
	
	private Node expression() throws CodeError {
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
		Node member = this.member();
		if (this.currentToken.type != Token.Type.LEFT_BRACKET) {
			return member;
		}
		
		if (member instanceof StringNode) {
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				"Cannot call a non function value String",
				this.currentToken.syntaxPosition
			);
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
		
		if (member instanceof FunctionAccessNode accessNode) {
			FunctionValue functionValue = this.context.getBuiltInFunction(accessNode.token.content, argumentNodes.size());
			member = new DirectAccessNode(accessNode.token, functionValue);
		}
		
		this.advance();
		return this.member(new CallNode(member, argumentNodes));
	}

	private Node member() throws CodeError {
		Node left = this.atom(false);
		return this.member(left);
	}

	private Node member(Node left) throws CodeError {
		while (this.currentToken.type == Token.Type.DOT) {
			this.advance();
			Node right = this.atom(true);
			switch (this.currentToken.type) {
				case LEFT_BRACKET -> {
					this.advance();
					List<Node> argumentNodes = new ArrayList<>();
					if (this.currentToken.type != Token.Type.RIGHT_BRACKET) {
						argumentNodes.add(this.expression());
						while (this.currentToken.type == Token.Type.COMMA) {
							this.advance();
							argumentNodes.add(this.expression());
						}
						this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected a ')'");
					}
					this.advance();
					if (!(right instanceof FunctionAccessNode)) {
						throw new CodeError(
							CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
							"%s is not a valid member function name".formatted(right.token.content),
							right.syntaxPosition
						);
					}
					left = new MemberCallNode(left, right, argumentNodes);
				}
				case ASSIGN_OPERATOR -> {
					this.advance();
					Node valueNode = this.expression();
					return new MemberAssignNode(left, right, valueNode);
				}
				case INCREMENT, DECREMENT -> left = this.modifyMember(left, right);
				default -> left = new MemberAccessNode(left, right);
			}
		}
		return left;
	}

	private Node staticMember(AbstractClassDefinition classDefinition) throws CodeError {
		this.advance();
		Token name = this.currentToken;
		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected '%s.member'".formatted(classDefinition.getName()));
		this.advance();
		return switch (this.currentToken.type) {
			case ASSIGN_OPERATOR -> {
				this.advance();
				Node valueNode = this.expression();
				yield new StaticAssignNode(name, classDefinition, valueNode);
			}
			case LEFT_BRACKET -> {
				this.advance();
				List<Node> argumentNodes = new ArrayList<>();
				if (this.currentToken.type != Token.Type.RIGHT_BRACKET) {
					argumentNodes.add(this.expression());
					while (this.currentToken.type == Token.Type.COMMA) {
						this.advance();
						argumentNodes.add(this.expression());
					}
					this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected a ')'");
				}
				this.advance();
				yield new StaticCallNode(name, classDefinition, argumentNodes);
			}
			case INCREMENT, DECREMENT -> this.modifyStatic(name, classDefinition);
			default -> new StaticAccessNode(name, classDefinition);
		};
	}

	private Node atom(boolean isMember) throws CodeError {
		Token token = this.currentToken;
		switch (token.type) {
			case IDENTIFIER -> {
				this.advance();
				if (isMember || this.context.isBuiltInFunction(token.content)) {
					/*
					 * Because we are calling a member function there is no way to know the
					 * type of the value we are calling from.
					 * We just have to trust that
					 *
					 * For the case of the built-in function we do not know the number of parameters
					 * We would have to calculate the number of arguments first...
					 */
					return new FunctionAccessNode(token);
				}

				AbstractClassDefinition classDefinition = this.context.getClassDefinition(token.content);
				if (classDefinition != null && this.currentToken.type == Token.Type.DOT) {
					return this.staticMember(classDefinition);
				}

				Value<?> value = this.context.getVariable(token.content);
				if (value == null) {
					throw new CodeError(CodeError.ErrorType.UNKNOWN_IDENTIFIER, "Could not find '%s'".formatted(token.content), token.syntaxPosition);
				}
				
				if (value instanceof FunctionValue) {
					return new DirectAccessNode(token, value);
				}
				
				return new VariableAccessNode(token);
			}
			case THIS -> {
				this.advance();
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
				try {
					return new StringNode(token, new StringValue(StringUtils.unescapeString(token.content.substring(1, token.content.length() - 1))));
				}
				catch (RuntimeException e) {
					throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, e.getMessage(), token.syntaxPosition);
				}
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
			case NEW -> {
				return this.newDefinition();
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
			Token lastToken = this.getLastToken();
			ISyntax lastTokenPosition = ISyntax.lastOf(lastToken.syntaxPosition);
			throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, errorMessage, lastTokenPosition);
		}
	}
}
