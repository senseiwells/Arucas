package me.senseiwells.arucas.compiler.token

interface TokenLike {
    val type: Type
    val content: String
}