package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.nodes.ExpressionVisitor

/**
 * This expression allows for changing order of operations.
 *
 * @param expression the expression to wrap.
 */
class BracketExpression(
    val expression: Expression
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitBracket(this)
}