package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.compiler.LocatableTrace
import me.senseiwells.arucas.nodes.ExpressionVisitor

/**
 * This expression accesses a variable in the scope.
 *
 * @param name the name of the identifier to access.
 * @param trace the trace position - if the access is invalid.
 */
class AccessExpression(
    val name: String,
    val trace: LocatableTrace
): Expression(), Assignable, Callable {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitAccess(this)

    override fun toAssignable(assignee: Expression): AssignExpression {
        return AssignExpression(this.name, assignee, this.trace)
    }

    override fun toCallable(arguments: List<Expression>): Expression {
        return CallExpression(FunctionAccessExpression(this.name, arguments.size, this.trace), arguments, this.trace)
    }
}