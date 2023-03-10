package me.senseiwells.arucas.compiler.lexer

import me.senseiwells.arucas.compiler.token.Type

/**
 * Context that stores all our lexing rules
 */
class LexerContext {
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