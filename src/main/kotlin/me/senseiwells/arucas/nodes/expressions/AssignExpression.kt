package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.classes.instance.ClassInstance
import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.nodes.ExpressionVisitor

/**
 * This expression assigns an identifier in the scope.
 *
 * @param name the name of the identifier to assign to.
 * @param assignee the expression to assign to the identifier.
 * @param trace the trace position - if the assignment is invalid.
 */
class AssignExpression(
    val name: String,
    assignee: Expression,
    trace: LocatableTrace
): AssignableExpression(assignee, trace) {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitAssign(this)

    override fun copyWith(instance: ClassInstance): AssignExpression {
        return AssignExpression(this.name, ExactExpression(instance), this.trace)
    }
}