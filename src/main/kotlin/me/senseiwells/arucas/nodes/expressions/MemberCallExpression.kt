package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.nodes.ExpressionVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This expression calls a member on a value.
 *
 * @param expression the expression to call on.
 * @param name the name of the member to call.
 * @param arguments the arguments to pass into the call.
 * @param trace the trace position - if the call is invalid.
 */
class MemberCallExpression(
    val expression: Expression,
    val name: String,
    val arguments: List<Expression>,
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitMemberCall(this)
}