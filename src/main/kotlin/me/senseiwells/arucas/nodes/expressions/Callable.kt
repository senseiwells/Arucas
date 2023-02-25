package me.senseiwells.arucas.nodes.expressions

/**
 * This interface allows us to convert access expressions into callable ones.
 */
interface Callable {
    fun toCallable(arguments: List<Expression>): Expression
}