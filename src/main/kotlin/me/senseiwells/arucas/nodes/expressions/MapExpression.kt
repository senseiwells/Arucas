package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.nodes.ExpressionVisitor

/**
 * This expression evaluates the map literal.
 *
 * @param expressions The map to evaluate.
 */
class MapExpression(
    val expressions: Map<Expression, Expression>
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitMap(this)
}