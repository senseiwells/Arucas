package me.senseiwells.arucas.core;

import java.util.ArrayList;
import java.util.List;

import me.senseiwells.arucas.api.ISyntax;
import me.senseiwells.arucas.utils.Position;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.tokens.Token;
import me.senseiwells.arucas.tokens.Token.Type;

public class Lexer {
	private static final LexerContext LEXER;
	
	static {
		LEXER = new LexerContext()
			// Whitespaces
			.addRule(Type.WHITESPACE, i -> i
				.addMultiline("/*", "*/")
				.addRegex("//[^\\r\\n]*")
				.addRegex("[ \t\r\n]")
			)
			
			// Arithmetics
			.addRule(Type.PLUS, i -> i.addString("+"))
			.addRule(Type.MINUS, i -> i.addString("-"))
			.addRule(Type.MULTIPLY, i -> i.addString("*"))
			.addRule(Type.DIVIDE, i -> i.addString("/"))
			.addRule(Type.POWER, i -> i.addString("^"))
			
			// Atoms
			.addRule(Type.IDENTIFIER, i -> i.addRegex("[a-zA-Z_][a-zA-Z0-9_]*"))
			.addRule(Type.BOOLEAN, i -> i.addStrings("true", "false"))
			.addRule(Type.STRING, i -> i
				.addMultiline("\"", "\\", "\"")
				.addMultiline("'", "\\", "'")
			)
			.addRule(Type.NUMBER, i -> i.addRegexes(
				"[0-9]+[.][0-9]+",
				"[0-9]+"
			))
			.addRule(Type.NULL, i -> i.addStrings("null"))

			// Comparisons - This must be defined AFTER identifiers
			.addRule(Type.EQUALS, i -> i.addString("=="))
			.addRule(Type.NOT_EQUALS, i -> i.addString("!="))
			.addRule(Type.LESS_THAN_EQUAL, i -> i.addString("<="))
			.addRule(Type.MORE_THAN_EQUAL, i -> i.addString(">="))
			.addRule(Type.LESS_THAN, i -> i.addString("<"))
			.addRule(Type.MORE_THAN, i -> i.addString(">"))
			.addRule(Type.NOT, i -> i.addStrings("!", "not"))
			.addRule(Type.AND, i -> i.addStrings("&&", "and"))
			.addRule(Type.OR, i -> i.addStrings("||", "or"))
			
			// Memory operations
			.addRule(Type.ASSIGN_OPERATOR, i -> i.addString("="))
			.addRule(Type.INCREMENT, i -> i.addString("++"))
			.addRule(Type.DECREMENT, i -> i.addString("--"))
			
			// Brackets
			.addRule(Type.LEFT_BRACKET, i -> i.addString("("))
			.addRule(Type.RIGHT_BRACKET, i -> i.addString(")"))
			.addRule(Type.LEFT_SQUARE_BRACKET, i -> i.addString("["))
			.addRule(Type.RIGHT_SQUARE_BRACKET, i -> i.addString("]"))
			.addRule(Type.LEFT_CURLY_BRACKET, i -> i.addString("{"))
			.addRule(Type.RIGHT_CURLY_BRACKET, i -> i.addString("}"))
			
			// Delimiters
			.addRule(Type.SEMICOLON, i -> i.addString(";"))
			.addRule(Type.COLON, i -> i.addString(":"))
			.addRule(Type.COMMA, i -> i.addString(","))
			
			// Keywords
			.addRule(Type.IF, i -> i.addString("if"))
			.addRule(Type.ELSE, i -> i.addString("else"))
			.addRule(Type.WHILE, i -> i.addString("while"))
			.addRule(Type.CONTINUE, i -> i.addString("continue"))
			.addRule(Type.BREAK, i -> i.addString("break"))
			.addRule(Type.RETURN, i -> i.addString("return"))
			.addRule(Type.VAR, i -> i.addString("var"))
			.addRule(Type.FUN, i -> i.addString("fun"))
			.addRule(Type.TRY, i -> i.addString("try"))
			.addRule(Type.CATCH, i -> i.addString("catch"))
			.addRule(Type.FOREACH, i -> i.addString("foreach"))
			.addRule(Type.FOR, i -> i.addString("for"))
			.addRule(Type.SWITCH, i -> i.addString("switch"))
			.addRule(Type.CASE, i -> i.addString("case"))
			.addRule(Type.DEFAULT, i -> i.addString("default"))
			.addRule(Type.CLASS, i -> i.addString("class"))
			.addRule(Type.THIS, i -> i.addString("this"))
			.addRule(Type.NEW, i -> i.addString("new"))
			.addRule(Type.STATIC, i -> i.addString("static"))
			.addRule(Type.OPERATOR, i -> i.addString("operator"))

			// Dot operator
			.addRule(Type.DOT, i -> i.addString("."))
			.addRule(Type.POINTER, i -> i.addString("->"))
		;
	}
	
	private final String text;
	private final String fileName;

	public Lexer(String text, String fileName) {
		this.text = text;
		this.fileName = fileName;
	}
	
	public List<Token> createTokens() throws CodeError {
		List<Token> tokenList = new ArrayList<>();
		int offset = 0;
		int line = 0;
		int column = 0;
		int length = this.text.length();
		String input = this.text;
		
		while (offset < length) {
			LexerContext.LexerToken lexerToken = LEXER.nextToken(input);
			
			if (lexerToken == null) {
				throw new CodeError(CodeError.ErrorType.ILLEGAL_CHAR_ERROR, "Invalid character", ISyntax.of(new Position(offset, line, column, this.fileName)));
			}
			
			if (lexerToken.length + offset > length) {
				break;
			}
			
			int old_offset = offset;
			int old_line = line;
			int old_column = column;
			
			for (int i = offset; i < offset + lexerToken.length; i++) {
				char c = this.text.charAt(i);
				
				if (c == '\n') {
					line ++;
					column = 0;
				}
				else {
					column ++;
				}
			}
			
			if (lexerToken.type != Type.WHITESPACE) {
				tokenList.add(new Token(
					lexerToken.type,
					lexerToken.content,
					new Position(old_offset, old_line, old_column, this.fileName),
					new Position(offset, line, column, this.fileName)
				));
			}
			
			input = input.substring(lexerToken.length);
			offset += lexerToken.length;
		}
	
		tokenList.add(new Token(Type.FINISH, ISyntax.of(new Position(offset, line, column, this.fileName))));
		return tokenList;
	}
}
