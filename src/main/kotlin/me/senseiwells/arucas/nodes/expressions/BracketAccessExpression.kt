package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.nodes.ExpressionVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This expression is used to access brackets on an expression.
 *
 * @param expression the expression to access.
 * @param index the index to access.
 * @param trace the trace position - if the access is invalid.
 */
class BracketAccessExpression(
    val expression: Expression,
    val index: Expression,
    val trace: LocatableTrace
): Expression(), Assignable {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitBracketAccess(this)

    override fun toAssignable(assignee: Expression): BracketAssignExpression {
        return BracketAssignExpression(this.expression, this.index, assignee, this.trace)
    }
}