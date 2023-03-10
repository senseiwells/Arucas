package me.senseiwells.arucas.compiler.lexer

import me.senseiwells.arucas.compiler.token.Type

class LexerToken(val type: Type, val content: String) {
    val length: Int = this.content.length
}