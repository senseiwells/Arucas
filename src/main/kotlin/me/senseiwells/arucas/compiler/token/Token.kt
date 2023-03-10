package me.senseiwells.arucas.compiler.token

import me.senseiwells.arucas.compiler.LocatableTrace

class Token(override val type: Type, val trace: LocatableTrace, override val content: String = ""): TokenLike {
    override fun toString(): String {
        return "Token{type='${this.type}', content='${this.content}'}"
    }
}

