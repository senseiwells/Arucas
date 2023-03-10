package me.senseiwells.arucas.functions.user

import me.senseiwells.arucas.nodes.expressions.Expression

class DelegatedConstructor private constructor(val type: Type, val arguments: List<Expression>) {
    companion object {
        fun none() = DelegatedConstructor(Type.NONE, listOf())

        fun current(arguments: List<Expression>) = DelegatedConstructor(Type.THIS, arguments)

        fun parent(arguments: List<Expression>) = DelegatedConstructor(Type.SUPER, arguments)
    }

    enum class Type {
        NONE, THIS, SUPER
    }
}