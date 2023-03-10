package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.nodes.ExpressionVisitor
import me.senseiwells.arucas.utils.CallTrace
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This expression is used to access a class constructor.
 *
 * @param name the name of the class to instantiate.
 * @param trace the trace position - if the access is invalid.
 */
class NewAccessExpression(
    val name: String,
    val trace: LocatableTrace
): Expression(), Callable {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitNewAccess(this)

    override fun toCallable(arguments: List<Expression>): Expression {
        return NewCallExpression(this.name, arguments, CallTrace(this.trace, "new ${this.name}::${arguments.size}"))
    }
}