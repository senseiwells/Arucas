package me.senseiwells.arucas.utils

import me.senseiwells.arucas.nodes.Expression

class ConstructorInit private constructor(val type: InitType, val arguments: List<Expression>) {
    companion object {
        fun initNone() = ConstructorInit(InitType.NONE, listOf())

        fun initThis(arguments: List<Expression>) = ConstructorInit(InitType.THIS, arguments)

        fun initSuper(arguments: List<Expression>) = ConstructorInit(InitType.SUPER, arguments)
    }

    enum class InitType {
        NONE, THIS, SUPER
    }
}