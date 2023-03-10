package me.senseiwells.arucas.functions.user

import me.senseiwells.arucas.nodes.expressions.Expression

class DelegatedConstructor private constructor(val type: Type, val arguments: List<Expression>) {
    companion object {
        fun initNone() = DelegatedConstructor(Type.NONE, listOf())

        fun initThis(arguments: List<Expression>) = DelegatedConstructor(Type.THIS, arguments)

        fun initSuper(arguments: List<Expression>) = DelegatedConstructor(Type.SUPER, arguments)
    }

    enum class Type {
        NONE, THIS, SUPER
    }
}