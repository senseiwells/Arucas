package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.nodes.ExpressionVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This expression calls an expression, for example a function.
 *
 * @param expression the expression to be called.
 * @param arguments the arguments to pass into the call.
 * @param trace the trace position - if the call is invalid.
 */
class CallExpression(
    val expression: Expression,
    val arguments: List<Expression>,
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitCall(this)
}