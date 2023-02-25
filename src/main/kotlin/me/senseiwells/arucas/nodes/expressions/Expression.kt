package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.nodes.ExpressionVisitor
import me.senseiwells.arucas.nodes.Visitable

/**
 * The base expression class.
 */
abstract class Expression: Visitable {
    abstract fun <T> visit(visitor: ExpressionVisitor<T>): T

    override fun toString(): String {
        return this::class.simpleName ?: "Expression"
    }
}