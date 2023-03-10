package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.nodes.ExpressionVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This expression assigns a value to a member.
 *
 * @param expression the expression to assign to.
 * @param name the name of the identifier.
 * @param assignee the expression to assign to the field.
 * @param trace the trace position - if the assignment is invalid.
 */
class MemberAssignExpression(
    val expression: Expression,
    val name: String,
    assignee: Expression,
    trace: LocatableTrace
): AssignableExpression(assignee, trace) {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitMemberAssign(this)

    override fun copyWith(instance: ClassInstance): AssignableExpression {
        return MemberAssignExpression(this.expression, this.name, ExactExpression(instance), trace)
    }
}