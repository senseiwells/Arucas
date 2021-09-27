package me.senseiwells.core.lexer;

import java.util.LinkedList;
import java.util.List;

import me.senseiwells.core.error.Error;
import me.senseiwells.core.tokens.KeyWordToken;
import me.senseiwells.core.tokens.Token;
import me.senseiwells.core.tokens.Token.Type;
import me.senseiwells.core.tokens.ValueToken;

public class Lexer {

    private final String text;
    private final char[] charArray;
    private final Position pos;
    private Character currentChar;
    public final String fileName;

    public Lexer(String text, String fileName) {
        this.text = text;
        this.charArray = text.toCharArray();
        this.fileName = fileName;
        this.pos = new Position(-1, 0, -1, fileName, text);
        this.currentChar = null;
        advance();
    }

    private void advance() {
        this.pos.advance(null);
        this.currentChar = this.pos.index < this.charArray.length ? this.charArray[this.pos.index] : null;
    }
    
    private void recede() {
        this.pos.recede();
        this.currentChar = this.pos.index < this.charArray.length ? this.charArray[this.pos.index] : null;
    }

    public List<Token> createTokens() throws Error {
        List<Token> tokenList = new LinkedList<>();
        while (this.currentChar != null) {
            Token token = null;
            switch (this.currentChar) {
                case '\t', ' ' -> {}
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> token = this.formNumberToken();
                case 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                     'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                     'u', 'v', 'w', 'x', 'y', 'z', '_' -> token = this.formIdentifierToken();
                case '+' -> token = this.formBasicToken(Type.PLUS);
                case '-' -> token = this.formMinusToken();
                case '*' -> token = this.formBasicToken(Type.MULTIPLY);
                case '/' -> token = this.formBasicToken(Type.DIVIDE);
                case '(' -> token = this.formBasicToken(Type.LEFT_BRACKET);
                case ')' -> token = this.formBasicToken(Type.RIGHT_BRACKET);
                case '=' -> token = this.formEqualsToken();
                case '!' -> token = this.formNotToken();
                case '<' -> token = this.formLessThanToken();
                case '>' -> token = this.formMoreThanToken();
                case '&' -> token = this.formAndToken();
                case '|' -> token = this.formOrToken();
                default -> throw this.throwNewError(Error.ErrorType.ILLEGAL_CHAR_ERROR, this.currentChar.toString());
            }
            if (token != null)
                tokenList.add(token);
            this.advance();
        }
        tokenList.add(new Token(Type.END, this.pos));
        return tokenList;
    }

    private Token formIdentifierToken() {
        StringBuilder identifierBuilder = new StringBuilder();
        Position startPos = this.pos.copy();
        while (this.currentChar != null && (Character.isAlphabetic(this.currentChar) || Character.isDigit(this.currentChar) || this.currentChar.equals('_'))) {
            identifierBuilder.append(this.currentChar);
            this.advance();
        }
        this.recede();
        String identifier = identifierBuilder.toString();
        if (identifier.equals("true") || identifier.equals("false"))
            return new ValueToken<>(Type.BOOLEAN, startPos, this.pos, identifier.equals("true"));
        KeyWordToken.KeyWord keyWord = KeyWordToken.KeyWord.stringToKeyWord(identifier);
        return keyWord != null ? new KeyWordToken(keyWord, startPos, this.pos) : new ValueToken<>(Type.IDENTIFIER, startPos, this.pos, identifier);
    }

    private Token formNumberToken() throws Error {
        StringBuilder numberAsString = new StringBuilder();
        boolean hasDots = false;
        Position startPos = this.pos.copy();
        while (this.currentChar != null && (Character.isDigit(this.currentChar) || this.currentChar.equals('.'))) {
            if (this.currentChar.equals('.')) {
                if (hasDots)
                    throw this.throwNewError(Error.ErrorType.ILLEGAL_SYNTAX_ERROR, this.text);
                numberAsString.append('.');
                hasDots = true;
            }
            else
                numberAsString.append(this.currentChar);
            this.advance();
        }
        this.recede();
        return hasDots ? new ValueToken<>(Type.FLOAT, startPos, this.pos, Float.parseFloat(numberAsString.toString())) : new ValueToken<>(Type.INT, startPos, this.pos, Integer.parseInt(numberAsString.toString()));
    }

    private Token formBasicToken(Type type) {
        return new Token(type, this.pos);
    }

    private Token formMinusToken() {
        Position startPos = this.pos.copy();
        this.advance();
        if (this.currentChar.equals('>'))
            return new KeyWordToken(KeyWordToken.KeyWord.THEN, startPos, this.pos);
        this.recede();
        return new Token(Type.MINUS, startPos, this.pos);
    }

    private Token formEqualsToken() {
        Position startPos = this.pos.copy();
        Type type = Type.ASSIGN_OPERATOR;
        this.advance();
        if (this.currentChar.equals('='))
            type = Type.EQUALS;
        else
            this.recede();
        return new Token(type, startPos, this.pos);
    }

    private Token formNotToken() {
        Position startPos = this.pos.copy();
        this.advance();
        if (this.currentChar.equals('='))
            return new Token(Type.NOT_EQUALS, startPos, this.pos);
        this.recede();
        return new KeyWordToken(KeyWordToken.KeyWord.NOT, startPos, this.pos);
    }

    private Token formLessThanToken() {
        Position startPos = this.pos.copy();
        Type type = Type.LESS_THAN;
        this.advance();
        if (this.currentChar.equals('='))
            type = Type.LESS_THAN_EQUAL;
        else
            this.recede();
        return new Token(type, startPos, this.pos);
    }

    private Token formMoreThanToken() {
        Position startPos = this.pos.copy();
        Type type = Type.MORE_THAN;
        this.advance();
        if (this.currentChar.equals('='))
            type = Type.MORE_THAN_EQUAL;
        else
            this.recede();
        return new Token(type, startPos, this.pos);
    }

    private Token formAndToken() throws Error {
        Position startPos = this.pos.copy();
        this.advance();
        if (!this.currentChar.equals('&'))
            throw this.throwNewError(Error.ErrorType.EXPECTED_CHAR_ERROR, "Expected '&&', found '&'");
        return new KeyWordToken(KeyWordToken.KeyWord.AND, startPos, this.pos);
    }

    private Token formOrToken() throws Error {
        Position startPos = this.pos.copy();
        this.advance();
        if (!this.currentChar.equals('|'))
            throw this.throwNewError(Error.ErrorType.EXPECTED_CHAR_ERROR, "Expected '||', found '|'");
        return new KeyWordToken(KeyWordToken.KeyWord.OR, startPos, this.pos);
    }

    private Error throwNewError(Error.ErrorType type, String errorMessage) throws Error {
        Position start = this.pos.copy();
        this.advance();
        throw new Error(type, errorMessage, start, this.pos);
    }
}
