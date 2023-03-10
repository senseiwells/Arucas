package me.senseiwells.arucas.compiler.lexer

import me.senseiwells.arucas.compiler.token.Type
import me.senseiwells.arucas.utils.StringUtils.regexEscape
import java.util.regex.Pattern

/**
 * Rule that matches patterns to token types
 */
class LexerRule(val type: Type) {
    private val matches = ArrayList<Pattern>()

    fun addString(value: String): LexerRule {
        this.matches.add(Pattern.compile(value.regexEscape()))
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
        val s: String = open.regexEscape()
        val c: String = close.regexEscape()
        val regex: String = if (escape.isEmpty()) "$s.*?$c" else {
            val e: String = escape.regexEscape()
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