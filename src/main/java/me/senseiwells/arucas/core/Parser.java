package me.senseiwells.arucas.core;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.nodes.*;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.throwables.RuntimeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.utils.ArucasClassDefinitionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.utils.MutableSyntaxImpl;
import me.senseiwells.arucas.utils.StringUtils;
import me.senseiwells.arucas.utils.impl.ArucasSet;
import me.senseiwells.arucas.values.NullValue;
import me.senseiwells.arucas.values.StringValue;
import me.senseiwells.arucas.values.Value;
import me.senseiwells.arucas.values.classes.AbstractClassDefinition;
import me.senseiwells.arucas.values.classes.ArucasEnumDefinition;
import me.senseiwells.arucas.values.classes.ArucasClassDefinition;
import me.senseiwells.arucas.values.functions.ClassMemberFunction;
import me.senseiwells.arucas.values.functions.FunctionValue;
import me.senseiwells.arucas.values.functions.UserDefinedFunction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Parser {
	private final List<Token> tokens;
	private final Context context;
	private final Stack<StackType> parseStack;

	private int operatorTokenIndex;
	private Token currentToken;

	public Parser(List<Token> tokens, Context context) {
		this.tokens = tokens;
		this.context = context.createParserContext();
		this.parseStack = new Stack<>();
		this.operatorTokenIndex = -1;
		this.advance();
	}

	private boolean advance() {
		return this.setTokenIndex(++this.operatorTokenIndex);
	}

	@SuppressWarnings("UnusedReturnValue")
	private boolean recede() {
		return this.setTokenIndex(--this.operatorTokenIndex);
	}

	private boolean setTokenIndex(int index) {
		this.operatorTokenIndex = index;
		this.currentToken = index < this.tokens.size() ? this.tokens.get(index) : null;
		return this.currentToken != null;
	}

	private Token getPreviousToken() {
		int index = this.operatorTokenIndex - 1;
		return this.tokens.get(index < 0 ? 0 : (index >= this.tokens.size() ? this.tokens.size() - 1 : index));
	}

	private Token peekNextToken() {
		int index = this.operatorTokenIndex + 1;
		return index < this.tokens.size() ? this.tokens.get(index) : this.currentToken;
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

		this.context.clearCachedDefinitions();

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
			case ENUM -> {
				return this.enumStatement();
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
			case FOR -> {
				return this.forStatement();
			}
			case SWITCH -> {
				return this.switchStatement();
			}
			case IMPORT -> {
				return this.importStatement();
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
			case THROW -> {
				this.advance();
				Node expression = this.sizeComparisonExpression();
				yield new ThrowNode(expression, startPos, this.currentToken.syntaxPosition);
			}
			default -> this.expression();
		};

		this.throwIfNotType(Token.Type.SEMICOLON, "Expected ; at end of line");
		this.advance();
		return node;
	}

	private Node importStatement() throws CodeError {
		this.advance();

		boolean all = this.currentToken.type == Token.Type.MULTIPLY;
		if (!all) {
			this.throwIfNotType(Token.Type.IDENTIFIER, "Expected class name");
		}
		Token className = this.currentToken;
		this.advance();

		this.throwIfNotType(Token.Type.FROM, "Expected 'from' keyword");
		this.advance();

		StringBuilder stringBuilder = new StringBuilder();
		do {
			this.throwIfNotType(Token.Type.IDENTIFIER, "Expected file name");
			stringBuilder.append(this.currentToken.content);
			this.advance();
			if (this.currentToken.type != Token.Type.DOT) {
				break;
			}
			stringBuilder.append("\\");
		}
		while (this.advance());

		String fileName = stringBuilder.toString();

		this.throwIfNotType(Token.Type.SEMICOLON, "Expected ; at end of line");
		this.advance();

		// We evaluate import classes at compile time since their definitions are required for other compilation
		// This is also needed so we can make sure there are no stack name conflicts since
		ArucasClassDefinitionMap importDefinitions = this.context.getCachedDefinitions(fileName);
		if (importDefinitions == null) {
			try {
				Path importPath = this.context.getImportPath();
				Path filePath = importPath.resolve(fileName + ".arucas");
				String fileContent = Files.readString(filePath);
				Context childContext = this.context.createChildContext("Import - " + className.content + " from " + fileName);
				importDefinitions = Run.importClasses(childContext, fileName, fileContent);
				this.context.addCachedDefinition(fileName, importDefinitions);
			}
			catch (IOException e) {
				throw new CodeError(
					CodeError.ErrorType.RUNTIME_ERROR,
					e.getMessage(), className.syntaxPosition
				);
			}
			catch (StackOverflowError e) {
				throw new CodeError(
					CodeError.ErrorType.RUNTIME_ERROR,
					"StackOverflow: Likely due to cyclical import",
					className.syntaxPosition
				);
			}
		}

		if (all) {
			for (AbstractClassDefinition definition : importDefinitions) {
				if (this.context.getClassDefinition(definition.getName()) != definition)  {
					this.throwIfStackNameTaken(definition.getName(), className.syntaxPosition);
				}
				this.context.addClassDefinition(definition);
			}
		}
		else {
			AbstractClassDefinition definition = importDefinitions.get(className.content);
			if (definition == null) {
				throw new RuntimeError("No such class '%s' exists".formatted(className.content), className.syntaxPosition, this.context);
			}
			if (this.context.getClassDefinition(definition.getName()) != definition)  {
				this.throwIfStackNameTaken(definition.getName(), className.syntaxPosition);
			}
			this.context.addClassDefinition(definition);
		}

		return new NullNode(className);
	}

	private ArucasClassNode classStatement() throws CodeError {
		ISyntax startPos = this.currentToken.syntaxPosition;
		this.advance();

		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected an identifier");
		Token className = this.currentToken;
		this.throwIfStackNameTaken(className);
		this.advance();

		this.throwIfNotType(Token.Type.LEFT_CURLY_BRACKET, "Expected '{'");
		this.advance();

		// Push the stack definition so that we can detect it from identifiers
		ArucasClassDefinition definition = new ArucasClassDefinition(className.content);
		this.context.addClassDefinition(definition);
		return this.internalClassStatements(definition, startPos);
	}

	private ArucasClassNode enumStatement() throws CodeError {
		ISyntax startPos = this.currentToken.syntaxPosition;
		this.advance();

		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected an identifier");
		Token enumName = this.currentToken;
		this.throwIfStackNameTaken(enumName);
		this.advance();

		this.throwIfNotType(Token.Type.LEFT_CURLY_BRACKET, "Expected '{'");
		this.advance();

		ArucasEnumDefinition definition = new ArucasEnumDefinition(enumName.content);
		this.context.addClassDefinition(definition);
		this.context.pushScope(startPos);

		while (this.currentToken.type == Token.Type.IDENTIFIER) {
			Token identifier = this.currentToken;

			if (definition.hasEnum(identifier.content)) {
				throw new CodeError(
					CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
					"Cannot have duplicate enum constants",
					identifier.syntaxPosition
				);
			}
			this.advance();

			List<Node> parameters = new ArrayList<>();

			if (this.currentToken.type == Token.Type.LEFT_BRACKET) {
				this.advance();
				if (this.currentToken.type != Token.Type.RIGHT_BRACKET) {
					parameters.add(this.expression());
					while (this.currentToken.type == Token.Type.COMMA) {
						this.advance();
						parameters.add(this.expression());
					}
				}
				this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ')'");
				this.advance();
			}
			definition.addEnum(identifier.content, new ListNode(parameters, identifier.syntaxPosition, this.currentToken.syntaxPosition));

			if (this.currentToken.type != Token.Type.COMMA) {
				break;
			}

			this.advance();
		}

		this.context.popScope();

		if (this.currentToken.type == Token.Type.SEMICOLON) {
			this.advance();
			return this.internalClassStatements(definition, startPos);
		}

		this.throwIfNotType(Token.Type.RIGHT_CURLY_BRACKET, "Expected '}'");
		ISyntax endPos = this.currentToken.syntaxPosition;
		this.advance();
		return new ArucasClassNode(definition, startPos, endPos);
	}

	private ArucasClassNode internalClassStatements(ArucasClassDefinition definition, ISyntax startPos) throws CodeError {
		// Push scopes to declare class body
		this.context.pushScope(startPos);

		while (this.currentToken.type != Token.Type.RIGHT_CURLY_BRACKET) {
			boolean isStatic = this.currentToken.type == Token.Type.STATIC;
			if (isStatic) {
				this.advance();
			}

			switch (this.currentToken.type) {
				case VAR -> {
					this.advance();
					this.throwIfNotType(Token.Type.IDENTIFIER, "Expected an identifier");
					Token token = this.currentToken;

					if (definition.hasMemberVariable(isStatic, token.content)) {
						throw new CodeError(
							CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
							"Cannot have duplicate members",
							token.syntaxPosition
						);
					}
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
						default -> {
							throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Expected ';' or assignment", token.syntaxPosition);
						}
					}
				}
				case IDENTIFIER -> {
					Token token = this.currentToken;
					this.advance();

					if (this.currentToken.type == Token.Type.LEFT_BRACKET) {
						if (!token.content.equals(definition.getName())) {
							throw new CodeError(
								CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR,
								"Constructor must have the same name as the class",
								this.currentToken.syntaxPosition
							);
						}
						ClassMemberFunction constructor = this.classConstructor(isStatic, token.content);
						definition.addConstructor(constructor);
					}
					else {
						throw new CodeError(
							CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR,
							"Expected class constructor",
							this.currentToken.syntaxPosition
						);
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
				case LEFT_CURLY_BRACKET -> {
					if (!isStatic) {
						throw new CodeError(
							CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR,
							"Unexpected '{'",
							this.currentToken.syntaxPosition
						);
					}
					definition.addStaticInitializer(this.statements());
				}
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
		ClassMemberFunction classConstructor = this.isStackTypePop(StackType.ARBITRARY) ?
			new ClassMemberFunction.Arbitrary(name, argumentNames, syntaxPosition): new ClassMemberFunction(name, argumentNames, syntaxPosition);
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
		String name = this.currentToken.content;
		this.advance();

		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected '('");
		this.advance();

		this.context.pushScope(this.currentToken.syntaxPosition);

		List<String> argumentNames = this.getClassMemberArguments();

		MutableSyntaxImpl syntaxPosition = new MutableSyntaxImpl(startPos.getStartPos(), null);
		ClassMemberFunction classMethod = this.isStackTypePop(StackType.ARBITRARY) ?
			new ClassMemberFunction.Arbitrary(name, argumentNames, syntaxPosition): new ClassMemberFunction(name, argumentNames, syntaxPosition);

		this.context.setLocal(name, classMethod);

		Node statements = this.statements();
		this.context.popScope();

		classMethod.complete(statements);
		syntaxPosition.end = statements.syntaxPosition.getEndPos();

		return classMethod;
	}

	private List<String> getClassMemberArguments() throws CodeError {
		List<String> argumentNames = new ArrayList<>();
		argumentNames.add("this");
		this.context.setLocal("this", NullValue.NULL);

		if (this.currentToken.type == Token.Type.IDENTIFIER) {
			do {
				this.throwIfNotType(Token.Type.IDENTIFIER, "Expected Identifier");
				this.throwIfStackNameTaken(this.currentToken);
				String varName = this.currentToken.content;
				argumentNames.add(varName);
				this.advance();

				if (this.currentToken.type == Token.Type.ARBITRARY) {
					// 2 because we include 'this'
					if (argumentNames.size() == 2) {
						this.parseStack.push(StackType.ARBITRARY);
						this.context.setLocal(varName, NullValue.NULL);
						this.advance();
						break;
					}
					throw new CodeError(
						CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR,
						"Cannot have multiple parameters with arbitrary parameter function",
						this.currentToken.syntaxPosition
					);
				}

				this.context.setLocal(varName, NullValue.NULL);
			}
			while (this.currentToken.type == Token.Type.COMMA && this.advance());
		}
		this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ',' or ')'");
		this.advance();
		return argumentNames;
	}

	private UserDefinedFunction staticClassMethod() throws CodeError {
		ISyntax startPos = this.currentToken.syntaxPosition;
		MutableSyntaxImpl syntaxPosition = new MutableSyntaxImpl(startPos.getStartPos(), null);
		this.advance();

		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected method name");
		Token variableNameToken = this.currentToken;
		this.advance();

		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected '('");
		this.advance();

		this.context.pushScope(this.currentToken.syntaxPosition);

		List<String> argumentNames = new ArrayList<>();

		UserDefinedFunction staticClassMethod = null;
		if (this.currentToken.type == Token.Type.IDENTIFIER) {
			do {
				this.throwIfNotType(Token.Type.IDENTIFIER, "Expected Identifier");
				this.throwIfStackNameTaken(this.currentToken);
				String varName = this.currentToken.content;
				argumentNames.add(varName);
				this.advance();

				if (this.currentToken.type == Token.Type.ARBITRARY) {
					if (argumentNames.size() == 1) {
						staticClassMethod = new UserDefinedFunction.Arbitrary(variableNameToken.content, varName, syntaxPosition);
						this.context.setLocal(varName, NullValue.NULL);
						this.advance();
						break;
					}
					throw new CodeError(
						CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR,
						"Cannot have multiple parameters with arbitrary parameter function",
						this.currentToken.syntaxPosition
					);
				}

				this.context.setLocal(varName, NullValue.NULL);
			}
			while (this.currentToken.type == Token.Type.COMMA && this.advance());
		}
		this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ',' or ')'");
		this.advance();

		if (staticClassMethod == null) {
			staticClassMethod = new UserDefinedFunction(variableNameToken.content, argumentNames, syntaxPosition);
		}
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
		this.advance();

		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected '('");
		this.advance();

		this.context.pushScope(this.currentToken.syntaxPosition);
		List<String> argumentNames = this.getClassMemberArguments();
		int parameters = argumentNames.size();

		CodeError noSuchOperator = new CodeError(
			CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
			"No such operator %s with %d parameters".formatted(token.type, parameters),
			startPos
		);

		switch (parameters) {
			case 1 -> {
				if (!Token.Type.OVERRIDABLE_UNARY_OPERATORS.contains(token.type)) {
					throw noSuchOperator;
				}
			}
			case 2 -> {
				if (!Token.Type.OVERRIDABLE_BINARY_OPERATORS.contains(token.type)) {
					throw noSuchOperator;
				}
			}
			default -> throw noSuchOperator;
		}


		MutableSyntaxImpl syntaxPosition = new MutableSyntaxImpl(startPos.getStartPos(), null);
		ClassMemberFunction operatorMethod = new ClassMemberFunction("$%s".formatted(token.type), argumentNames, syntaxPosition);

		Node statements = this.statements();
		this.context.popScope();

		operatorMethod.complete(statements);
		syntaxPosition.end = statements.syntaxPosition.getEndPos();

		return operatorMethod;
	}

	// Checks whether the following code is unpacking code
	private boolean isUnpackable() throws CodeError {
		int position = this.operatorTokenIndex;
		this.parseStack.add(StackType.UNPACKING);
		do {
			Node node = this.expression();

			if (!(node instanceof VariableAssignNode)) {
				this.parseStack.pop();
				this.setTokenIndex(position);
				return false;
			}
		}
		while (this.currentToken.type == Token.Type.COMMA && this.advance());
		this.parseStack.pop();

		boolean isPackable = this.currentToken.type == Token.Type.ASSIGN_OPERATOR;
		this.setTokenIndex(position);

		return isPackable;
	}

	private VariableAssignNode setUnpacking() throws CodeError {
		return this.setUnpacking(null);
	}

	private VariableAssignNode setUnpacking(VariableAssignNode firstNode) throws CodeError {
		this.parseStack.add(StackType.UNPACKING);
		List<VariableAssignNode> assignNodes = new ArrayList<>();
		if (firstNode != null) {
			assignNodes.add(firstNode);
		}
		Token start = this.currentToken;
		do {
			Node node = this.expression();
			if (!(node instanceof VariableAssignNode assignNode)) {
				throw new CodeError(CodeError.ErrorType.ILLEGAL_OPERATION_ERROR, "Expected assignable values when unpacking", this.currentToken.syntaxPosition);
			}
			assignNodes.add(assignNode);
		}
		while (this.currentToken.type == Token.Type.COMMA && this.advance());
		this.parseStack.pop();

		this.throwIfNotType(Token.Type.ASSIGN_OPERATOR, "Expected an assignment operator");

		this.advance();
		Node expression = this.expression();

		return new UnpackAssignNode(start, assignNodes, expression);
	}

	private VariableAssignNode setVariable() throws CodeError {
		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected an identifier");
		Token variableName = this.currentToken;
		this.throwIfStackNameTaken(variableName);

		this.advance();
		if (this.isStackType(StackType.UNPACKING)) {
			this.context.setVariable(variableName.content, NullValue.NULL);
			return new VariableAssignNode(variableName, new NullNode(this.currentToken));
		}

		this.throwIfNotType(Token.Type.ASSIGN_OPERATOR, "Expected an assignment operator");
		this.advance();
		Node expression = this.expression();

		this.context.setVariable(variableName.content, NullValue.NULL);
		return new VariableAssignNode(variableName, expression);
	}

	private VariableAssignNode modifyVariable() throws CodeError {
		this.throwIfNotType(Token.Type.IDENTIFIER, "Expected an identifier");
		Token variableName = this.currentToken;
		this.throwIfStackNameTaken(variableName);

		Node member = this.member();
		Token operatorToken = this.currentToken;
		Token.Type operatorType = switch (this.currentToken.type) {
			case INCREMENT -> Token.Type.PLUS;
			case DECREMENT -> Token.Type.MINUS;
			default -> throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, "Unknown unary memory operator", operatorToken.syntaxPosition);
		};

		this.advance();
		Node numberNode = new NumberNode(new Token(Token.Type.NUMBER, "1", operatorToken.syntaxPosition), 1.0D);

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
		Node numberNode = new NumberNode(new Token(Token.Type.NUMBER, "1", operatorToken.syntaxPosition), 1.0D);

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
		Node numberNode = new NumberNode(new Token(Token.Type.NUMBER, "1", operatorToken.syntaxPosition), 1.0D);

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

	private int functionLambdaIndex = 1;

	private Node functionDefinition(boolean isLambda) throws CodeError {
		this.parseStack.add(StackType.FUN);
		Token functionStartToken = this.currentToken;
		this.advance();
		List<String> argumentNameTokens = new ArrayList<>();
		Token variableNameToken;

		if (isLambda) {
			variableNameToken = new Token(
				Token.Type.IDENTIFIER, "%d$lambda".formatted(this.functionLambdaIndex++),
				this.currentToken.syntaxPosition
			);
		}
		else {
			this.throwIfNotType(Token.Type.IDENTIFIER, "Expected function name");

			variableNameToken = this.currentToken;
			this.throwIfStackNameTaken(variableNameToken);

			this.advance();
		}
		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected 'fun (...)'");
		this.advance();

		this.context.pushScope(this.currentToken.syntaxPosition);

		FunctionNode functionNode = null;
		if (this.currentToken.type == Token.Type.IDENTIFIER) {
			do {
				this.throwIfNotType(Token.Type.IDENTIFIER, "Expected Identifier");
				this.throwIfStackNameTaken(this.currentToken);
				String varName = this.currentToken.content;
				argumentNameTokens.add(varName);
				this.advance();

				if (this.currentToken.type == Token.Type.ARBITRARY) {
					// We can't have more than one argument be arbitrary
					// Since this would not work with the current FunctionMap system
					if (argumentNameTokens.size() == 1) {
						functionNode = new FunctionNode(
							functionStartToken,
							new UserDefinedFunction.Arbitrary(variableNameToken.content, varName, functionStartToken.syntaxPosition)
						);
						this.context.setLocal(varName, NullValue.NULL);
						this.advance();
						break;
					}
					throw new CodeError(
						CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR,
						"Cannot have multiple parameters with arbitrary parameter function",
						this.currentToken.syntaxPosition
					);
				}

				this.context.setLocal(varName, NullValue.NULL);
			}
			while (this.currentToken.type == Token.Type.COMMA && this.advance());
		}
		this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ',' or ')'");
		this.advance();

		if (functionNode == null) {
			functionNode = new FunctionNode(functionStartToken, variableNameToken, argumentNameTokens);
		}
		this.context.setLocal(variableNameToken.content, functionNode.getFunctionValue());

		Node statements = this.statements();
		this.context.popScope();

		functionNode.complete(statements);
		this.context.setVariable(variableNameToken.content, functionNode.getFunctionValue());
		this.parseStack.pop();
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
		return new ForeachNode(member, statements, forParameterName);
	}

	private Node forStatement() throws CodeError {
		this.advance();

		this.throwIfNotType(Token.Type.LEFT_BRACKET, "Expected '(...)'");
		this.advance();

		Node initExpression = new NullNode(this.currentToken);
		if (this.currentToken.type != Token.Type.SEMICOLON) {
			initExpression = this.expression();
			this.throwIfNotType(Token.Type.SEMICOLON, "Expected ';'");
		}
		this.advance();

		Node condition = new BooleanNode(new Token(Token.Type.BOOLEAN, "true", this.currentToken.syntaxPosition));
		if (this.currentToken.type != Token.Type.SEMICOLON) {
			condition = this.expression();
			this.throwIfNotType(Token.Type.SEMICOLON, "Expected ';'");
		}
		this.advance();

		Node endExpression = new NullNode(this.currentToken);
		if (this.currentToken.type != Token.Type.RIGHT_BRACKET) {
			endExpression = this.expression();
			this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ')'");
		}
		this.advance();

		Node statements = this.statements();

		return new ForNode(initExpression, condition, endExpression, statements);
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

		List<Set<Node>> casesList = new ArrayList<>();
		List<ArucasSet> rawCasesList = new ArrayList<>();
		List<Node> caseStatementsList = new ArrayList<>();
		ArucasSet allRawCases = new ArucasSet();
		Node defaultCase = null;

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

			Set<Node> cases = new LinkedHashSet<>();
			ArucasSet rawCases = new ArucasSet();

			do {
				this.advance();
				Node expression = this.expression();

				Value<?> value;
				if (expression instanceof DirectAccessNode direct && (value = direct.getValue()) != null) {
					if (allRawCases.contains(this.context, value)) {
						throw new CodeError(
							CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR,
							"Switch statements can not contain duplicate conditions. '%s'".formatted(value.getAsString(this.context)),
							this.currentToken.syntaxPosition
						);
					}
					allRawCases.add(this.context, value);
					rawCases.add(this.context, value);
					continue;
				}

				cases.add(expression);
			}
			while (this.currentToken.type == Token.Type.COMMA);

			this.throwIfNotType(Token.Type.POINTER, "Expected '->' but got '%s'".formatted(this.currentToken.content));
			this.advance();

			casesList.add(cases.isEmpty() ? null : cases);
			rawCasesList.add(rawCases.isEmpty() ? null : rawCases);
			caseStatementsList.add(this.statements());
		}

		this.throwIfNotType(Token.Type.RIGHT_CURLY_BRACKET, "Expected '}'");
		ISyntax endPos = this.currentToken.syntaxPosition;
		this.advance();

		return new SwitchNode(valueNode, defaultCase, casesList, rawCasesList, caseStatementsList, startPos, endPos);
	}

	private Node expression() throws CodeError {
		if (this.currentToken.type == Token.Type.IDENTIFIER) {
			switch (this.peekNextToken().type) {
				case ASSIGN_OPERATOR -> {
					return this.setVariable();
				}
				case COMMA -> {
					if (!this.isStackType(StackType.UNPACKING) && this.isUnpackable()) {
						return this.setUnpacking();
					}
					if (this.isStackType(StackType.UNPACKING)) {
						return this.setVariable();
					}
				}
				case INCREMENT, DECREMENT -> {
					return this.modifyVariable();
				}
			}
		}

		if (this.currentToken.type == Token.Type.VAR) {
			this.advance();
			if (this.peekNextToken().type == Token.Type.ASSIGN_OPERATOR) {
				VariableAssignNode assignNode = this.setVariable();
				assignNode.setLocal(true);
				return assignNode;
			}
			// We do not allow for unpacking of local variables
			// This is because variable could be member of a class
			throw new CodeError(
				CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
				"'var' keyword can only be used to assign local variables",
				this.currentToken.syntaxPosition
			);
		}
		return this.sizeComparisonExpression();
	}

	private Node listExpression() throws CodeError {
		this.parseStack.add(StackType.LIST);
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
		this.parseStack.pop();
		return new ListNode(elementList, startPos, endPos);
	}

	private Node mapExpression() throws CodeError {
		this.parseStack.add(StackType.MAP);
		Map<Node, Node> elementMap = new LinkedHashMap<>();
		ISyntax startPos = this.currentToken.syntaxPosition;

		this.advance();
		if (this.currentToken.type != Token.Type.RIGHT_CURLY_BRACKET) {
			do {
				Node keyNode = this.expression();
				this.throwIfNotType(Token.Type.COLON, "Expected a ':' between key and value");
				this.advance();
				Node valueNode = this.expression();
				elementMap.put(keyNode, valueNode);
			}
			while (this.currentToken.type == Token.Type.COMMA && this.advance());
			this.throwIfNotType(Token.Type.RIGHT_CURLY_BRACKET, "Expected a '}'");
		}
		ISyntax endPos = this.currentToken.syntaxPosition;
		this.advance();
		this.parseStack.pop();
		return new MapNode(elementMap, startPos, endPos);
	}

	private Node sizeComparisonExpression() throws CodeError {
		Node left = this.comparisonExpression();
		while (this.currentToken.type == Token.Type.AND || this.currentToken.type == Token.Type.OR || this.currentToken.type == Token.Type.XOR) {
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
		while (Token.Type.COMPARISON_TOKEN_TYPES.contains(this.currentToken.type)) {
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
			if (member instanceof FunctionAccessNode) {
				throw new CodeError(
					CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
					"Build-in functions cannot be delegated",
					this.currentToken.syntaxPosition
				);
			}
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
			if (functionValue == null) {
				throw new CodeError(
					CodeError.ErrorType.RUNTIME_ERROR,
					"No such build-in function '%s' with %d parameters".formatted(accessNode.token.content, argumentNodes.size()),
					this.currentToken.syntaxPosition
				);
			}
			member = new DirectAccessNode<>(accessNode.token, functionValue);
		}

		this.advance();
		return this.member(new CallNode(member, argumentNodes));
	}

	private Node member() throws CodeError {
		Node left = this.atom();
		return this.member(left);
	}

	private Node member(Node left) throws CodeError {
		while (this.currentToken.type == Token.Type.DOT) {
			this.advance();
			this.parseStack.add(StackType.MEMBER);
			Node right = this.atom();
			this.parseStack.pop();
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
					if (!(right instanceof FunctionAccessNode accessNode)) {
						throw new CodeError(
							CodeError.ErrorType.ILLEGAL_OPERATION_ERROR,
							"%s is not a valid member function name".formatted(right.token.content),
							right.syntaxPosition
						);
					}
					left = new MemberCallNode(left, accessNode, argumentNodes);
				}
				case ASSIGN_OPERATOR -> {
					if (this.isStackType(StackType.UNPACKING)) {
						return new MemberAssignNode(left, right, new NullNode(this.currentToken));
					}
					this.advance();
					Node valueNode = this.expression();
					return new MemberAssignNode(left, right, valueNode);
				}
				case COMMA -> {
					this.advance();
					VariableAssignNode assignNode = new MemberAssignNode(left, right, new NullNode(this.currentToken));
					if (!this.isStackType(StackType.UNPACKING) && this.isUnpackable()) {
						return this.setUnpacking(assignNode);
					}
					this.recede();
					return this.isStackType(StackType.UNPACKING) ? assignNode : new MemberAccessNode(left, right);
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
			case ASSIGN_OPERATOR -> {
				if (this.isStackType(StackType.UNPACKING)) {
					yield new StaticAssignNode(name, classDefinition, new NullNode(this.currentToken));
				}
				this.advance();
				Node valueNode = this.expression();
				yield new StaticAssignNode(name, classDefinition, valueNode);
			}
			case COMMA -> {
				this.advance();
				VariableAssignNode assignNode = new StaticAssignNode(name, classDefinition, new NullNode(this.currentToken));
				if (!this.isStackType(StackType.UNPACKING) && this.isUnpackable()) {
					yield this.setUnpacking(assignNode);
				}
				this.recede();
				yield this.isStackType(StackType.UNPACKING) ? assignNode : new StaticAccessNode(name, classDefinition);
			}
			case INCREMENT, DECREMENT -> this.modifyStatic(name, classDefinition);
			default -> new StaticAccessNode(name, classDefinition);
		};
	}

	private Node atom() throws CodeError {
		Token token = this.currentToken;
		switch (token.type) {
			case IDENTIFIER -> {
				this.advance();
				if (this.isStackType(StackType.MEMBER) || this.context.isBuiltInFunction(token.content)) {
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
				/* This can be checked at runtime
				if (value == null) {
					throw new CodeError(CodeError.ErrorType.UNKNOWN_IDENTIFIER, "Could not find '%s'".formatted(token.content), token.syntaxPosition);
				}
				*/

				if (value instanceof FunctionValue) {
					return new DirectAccessNode<>(token, value);
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
					return new StringNode(token, StringValue.of(StringUtils.unescapeString(token.content.substring(1, token.content.length() - 1))));
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
				this.parseStack.add(StackType.PARENTHESIS);
				this.advance();
				Node expression = this.expression();
				this.throwIfNotType(Token.Type.RIGHT_BRACKET, "Expected ')'");
				this.advance();
				this.parseStack.pop();
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
			Token lastToken = this.getPreviousToken();
			ISyntax lastTokenPosition = ISyntax.lastOf(lastToken.syntaxPosition);
			throw new CodeError(CodeError.ErrorType.ILLEGAL_SYNTAX_ERROR, errorMessage, lastTokenPosition);
		}
	}

	private boolean isStackType(StackType type) {
		return !this.parseStack.empty() && this.parseStack.peek() == type;
	}

	private boolean isStackTypePop(StackType type) {
		if (this.isStackType(type)) {
			this.parseStack.pop();
			return true;
		}
		return false;
	}

	private void throwIfStackNameTaken(Token token) throws CodeError {
		this.throwIfStackNameTaken(token.content, token.syntaxPosition);
	}

	private void throwIfStackNameTaken(String name, ISyntax syntax) throws CodeError {
		this.context.throwIfStackNameTaken(name, syntax);
	}

	enum StackType {
		UNPACKING,
		PARENTHESIS,
		MEMBER,
		LIST,
		MAP,
		FUN,
		ARBITRARY
	}
}
