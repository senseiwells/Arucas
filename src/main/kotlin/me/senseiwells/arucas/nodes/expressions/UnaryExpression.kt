package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.core.Type
import me.senseiwells.arucas.nodes.ExpressionVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This expression applies a unary operator on a value.
 *
 * @param type The operator type.
 * @param expression The expression that will have the operator applied to.
 * @param trace The trace position - if the unary operation is invalid.
 */
class UnaryExpression(
    val type: Type,
    val expression: Expression,
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitUnary(this)
}