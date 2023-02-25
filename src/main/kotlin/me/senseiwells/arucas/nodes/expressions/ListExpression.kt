package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.nodes.ExpressionVisitor

/**
 * This expression evaluates the list literal.
 *
 * @param expressions The list to evaluate.
 */
class ListExpression(
    val expressions: List<Expression>
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitList(this)
}