package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.compiler.token.Type
import me.senseiwells.arucas.nodes.ExpressionVisitor

/**
 * This expression applies a unary operator on a value.
 *
 * @param type the operator type.
 * @param expression the expression that will have the operator applied to.
 * @param trace the trace position - if the unary operation is invalid.
 */
class UnaryExpression(
    val type: Type,
    val expression: Expression,
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitUnary(this)
}