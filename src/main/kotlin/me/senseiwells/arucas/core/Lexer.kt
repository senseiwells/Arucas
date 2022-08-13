package me.senseiwells.arucas.core

import me.senseiwells.arucas.exceptions.compileError
import me.senseiwells.arucas.utils.LocatableTrace
import me.senseiwells.arucas.utils.StringUtils
import java.util.regex.Pattern

private val LEXER_CONTEXT = LexerContext()
    // Whitespaces
    .addRule(Type.WHITESPACE) { i ->
        i.addMultiline("/*", "*/").addRegex("//[^\\r\\n]*").addRegex("[ \t\r\n]")
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
    .addRule(Type.STATIC)
    .addRule(Type.OPERATOR)
    .addRule(Type.THROW)
    .addRule(Type.IMPORT)
    .addRule(Type.FROM)
    .addRule(Type.LOCAL)
    .addRule(Type.ARBITRARY)
    .addRule(Type.DOT)
    .addRule(Type.POINTER)

class Lexer(private val text: String, private val fileName: String) {
    fun createTokens(): List<Token> {
        val tokenList = ArrayList<Token>()
        val length = this.text.length
        var offset = 0
        var line = 0
        var column = 0
        var input = this.text
        while (offset < length) {
            val lexerToken: LexerToken = LEXER_CONTEXT.nextToken(input) ?:
                compileError("Invalid character", LocatableTrace(this.fileName, line, column))
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
            if (lexerToken.type !== Type.WHITESPACE) {
                tokenList.add(Token(
                    lexerToken.type,
                    LocatableTrace(this.fileName, oldLine, oldColumn),
                    lexerToken.content
                ))
            }
            input = input.substring(lexerToken.length)
            offset += lexerToken.length
        }
        tokenList.add(Token(Type.EOF, LocatableTrace(this.fileName, line, column)))
        return tokenList
    }
}

private class LexerToken(val type: Type, val content: String) {
    val length: Int = this.content.length
}

/**
 * Context that stores all our lexing rules
 */
private class LexerContext {
    private val rules = ArrayList<LexerRule>()

    fun addRule(type: Type): LexerContext {
        val rule = LexerRule(type)
        rule.addString(type.toString())
        this.rules.add(rule)
        return this
    }

    fun addRule(type: Type, consumer: (LexerRule) -> Unit): LexerContext {
        val rule = LexerRule(type)
        consumer(rule)
        this.rules.add(rule)
        return this
    }

    fun nextToken(input: String): LexerToken? {
        var selectedRule: LexerRule? = null
        var longestRule = 1
        for (rule in this.rules) {
            val length = rule.getMatchLength(input)
            if (length >= longestRule) {
                longestRule = length
                selectedRule = rule
            }
        }
        return selectedRule?.let { LexerToken(selectedRule.type, input.substring(0, longestRule)) }
    }
}

/**
 * Rule that matches patterns to token types
 */
private class LexerRule(val type: Type) {
    private val matches = ArrayList<Pattern>()

    fun addString(value: String): LexerRule {
        this.matches.add(Pattern.compile(StringUtils.regexEscape(value)))
        return this
    }

    fun addRegex(regex: String): LexerRule {
        this.matches.add(Pattern.compile(regex))
        return this
    }

    fun addRegexes(vararg regexes: String): LexerRule {
        for (regex in regexes) {
            this.addRegex(regex)
        }
        return this
    }

    fun addMultiline(open: String, close: String): LexerRule {
        return this.addMultiline(open, "", close)
    }

    fun addMultiline(open: String, escape: String, close: String): LexerRule {
        val s: String = StringUtils.regexEscape(open)
        val c: String = StringUtils.regexEscape(close)
        val regex: String = if (escape.isEmpty()) "$s.*?$c" else {
            val e: String = StringUtils.regexEscape(escape)
            "$s(?:$e(?:$e|$c|(?!$c).)|(?!$e|$c).)*$c"
        }
        this.matches.add(Pattern.compile(regex, Pattern.DOTALL))
        return this
    }

    fun getMatchLength(string: String): Int {
        var length = 0
        for (pattern in this.matches) {
            val matcher = pattern.matcher(string)
            if (matcher.lookingAt()) {
                length = length.coerceAtLeast(matcher.end())
            }
        }
        return if (length < 1) -1 else length
    }
}
