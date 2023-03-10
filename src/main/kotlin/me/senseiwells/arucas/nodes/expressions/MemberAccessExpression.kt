package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.nodes.ExpressionVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This expression accesses an identifier.
 *
 * @param expression the expression to access.
 * @param name the name of the identifier.
 * @param trace the trace position - if the access is invalid.
 */
class MemberAccessExpression(
    val expression: Expression,
    val name: String,
    val trace: LocatableTrace
): Expression(), Assignable, Callable {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitMemberAccess(this)

    override fun toAssignable(assignee: Expression): MemberAssignExpression {
        return MemberAssignExpression(this.expression, this.name, assignee, this.trace)
    }

    override fun toCallable(arguments: List<Expression>): Expression {
        return MemberCallExpression(this.expression, this.name, arguments, this.trace)
    }
}