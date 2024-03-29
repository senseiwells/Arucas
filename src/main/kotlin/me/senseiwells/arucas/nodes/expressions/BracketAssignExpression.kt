package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.nodes.ExpressionVisitor

/**
 * This expression is used to assign something to an expression using brackets.
 *
 * @param expression the expression to assign to.
 * @param index the index to assign to.
 * @param assignee the expression to assign to the index.
 * @param trace the trace position - if the assignment is invalid.
 */
class BracketAssignExpression(
    val expression: Expression,
    val index: Expression,
    assignee: Expression,
    trace: LocatableTrace
): AssignableExpression(assignee, trace) {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitBracketAssign(this)

    override fun copyWith(instance: ClassInstance): BracketAssignExpression {
        return BracketAssignExpression(this.expression, this.index, ExactExpression(instance), trace)
    }
}