package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.nodes.ExpressionVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This expression calls a member on a value.
 *
 * @param expression The expression to call on.
 * @param name The name of the member to call.
 * @param arguments The arguments to pass into the call.
 * @param trace The trace position - if the call is invalid.
 */
class MemberCallExpression(
    val expression: Expression,
    val name: String,
    val arguments: List<Expression>,
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitMemberCall(this)
}