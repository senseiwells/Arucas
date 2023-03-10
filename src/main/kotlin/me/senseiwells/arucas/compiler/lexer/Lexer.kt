package me.senseiwells.arucas.compiler.lexer

import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.compiler.token.Token
import me.senseiwells.arucas.compiler.token.Type
import me.senseiwells.arucas.exceptions.compileError

class Lexer(private val text: String, private val fileName: String) {
    companion object {
        val LEXER_CONTEXT = LexerContext()
            // Whitespaces
            .addRule(Type.WHITESPACE) { i ->
                i.addRegex("[ \t\r\n]")
            }
            .addRule(Type.COMMENT) { i ->
                i.addMultiline("/*", "*/").addRegex("//[^\\r\\n]*")
            }

            // Arithmetics
            .addRule(Type.PLUS)
            .addRule(Type.MINUS)
            .addRule(Type.MULTIPLY)
            .addRule(Type.DIVIDE)
            .addRule(Type.POWER)

            // Atoms
            .addRule(Type.IDENTIFIER) { i -> i.addRegex("[a-zA-Z_][a-zA-Z0-9_]*") }
            .addRule(Type.STRING) { i -> i.addMultiline("\"", "\\", "\"").addMultiline("'", "\\", "'") }
            .addRule(Type.NUMBER) { i -> i.addRegexes("[0-9]+\\.[0-9]+", "[0-9]+", "0[xX][0-9a-fA-F]+") }
            .addRule(Type.TRUE)
            .addRule(Type.FALSE)
            .addRule(Type.NULL)

            // Comparisons - This must be defined AFTER identifiers
            .addRule(Type.EQUALS)
            .addRule(Type.NOT_EQUALS)
            .addRule(Type.LESS_THAN_EQUAL)
            .addRule(Type.MORE_THAN_EQUAL)
            .addRule(Type.LESS_THAN)
            .addRule(Type.MORE_THAN)
            .addRule(Type.NOT)
            .addRule(Type.AND)
            .addRule(Type.OR)
            .addRule(Type.XOR)
            .addRule(Type.SHIFT_LEFT)
            .addRule(Type.SHIFT_RIGHT)
            .addRule(Type.BIT_AND)
            .addRule(Type.BIT_OR)

            // Memory operations
            .addRule(Type.ASSIGN_OPERATOR)
            .addRule(Type.INCREMENT)
            .addRule(Type.DECREMENT)
            .addRule(Type.PLUS_ASSIGN)
            .addRule(Type.MINUS_ASSIGN)
            .addRule(Type.MULTIPLY_ASSIGN)
            .addRule(Type.DIVIDE_ASSIGN)
            .addRule(Type.POWER_ASSIGN)
            .addRule(Type.AND_ASSIGN)
            .addRule(Type.OR_ASSIGN)
            .addRule(Type.XOR_ASSIGN)

            // Brackets
            .addRule(Type.LEFT_BRACKET)
            .addRule(Type.RIGHT_BRACKET)
            .addRule(Type.LEFT_SQUARE_BRACKET)
            .addRule(Type.RIGHT_SQUARE_BRACKET)
            .addRule(Type.LEFT_CURLY_BRACKET)
            .addRule(Type.RIGHT_CURLY_BRACKET)

            // Delimiters
            .addRule(Type.SEMICOLON)
            .addRule(Type.COLON)
            .addRule(Type.COMMA)

            // Keywords
            .addRule(Type.IF)
            .addRule(Type.ELSE)
            .addRule(Type.WHILE)
            .addRule(Type.CONTINUE)
            .addRule(Type.BREAK)
            .addRule(Type.RETURN)
            .addRule(Type.VAR)
            .addRule(Type.FUN)
            .addRule(Type.TRY)
            .addRule(Type.CATCH)
            .addRule(Type.FINALLY)
            .addRule(Type.FOREACH)
            .addRule(Type.FOR)
            .addRule(Type.SWITCH)
            .addRule(Type.CASE)
            .addRule(Type.DEFAULT)
            .addRule(Type.CLASS)
            .addRule(Type.ENUM)
            .addRule(Type.INTERFACE)
            .addRule(Type.THIS)
            .addRule(Type.SUPER)
            .addRule(Type.AS)
            .addRule(Type.NEW)
            .addRule(Type.PRIVATE)
            .addRule(Type.STATIC)
            .addRule(Type.OPERATOR)
            .addRule(Type.THROW)
            .addRule(Type.IMPORT)
            .addRule(Type.FROM)
            .addRule(Type.LOCAL)
            .addRule(Type.READONLY)
            .addRule(Type.LAUNCH)

            //
            .addRule(Type.ARBITRARY)
            .addRule(Type.DOT)
            .addRule(Type.POINTER)
    }

    fun createTokens(): List<Token> {
        val tokenList = ArrayList<Token>()
        val length = this.text.length
        var offset = 0
        var line = 0
        var column = 0
        var input = this.text
        while (offset < length) {
            val lexerToken: LexerToken = LEXER_CONTEXT.nextToken(input) ?:
                compileError("Invalid character", LocatableTrace(this.fileName, this.text, line, column))
            if (lexerToken.length + offset > length) {
                break
            }
            val oldLine = line
            val oldColumn = column
            for (i in offset until offset + lexerToken.length) {
                val c = this.text[i]
                if (c == '\n') {
                    line++
                    column = 0
                } else {
                    column++
                }
            }
            if (lexerToken.type !== Type.WHITESPACE && lexerToken.type !== Type.COMMENT) {
                tokenList.add(
                    Token(
                    lexerToken.type,
                    LocatableTrace(this.fileName, this.text, oldLine, oldColumn),
                    lexerToken.content
                )
                )
            }
            input = input.substring(lexerToken.length)
            offset += lexerToken.length
        }
        tokenList.add(Token(Type.EOF, LocatableTrace(this.fileName, this.text, line, column)))
        return tokenList
    }
}

