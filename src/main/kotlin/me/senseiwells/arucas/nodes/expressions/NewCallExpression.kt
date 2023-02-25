package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.nodes.ExpressionVisitor
import me.senseiwells.arucas.utils.CallTrace

/**
 * This expression is used to call a class constructor.
 *
 * @param name The name of the class to instantiate.
 * @param arguments The arguments to pass into the constructor.
 * @param trace The trace position - if the call is invalid.
 */
class NewCallExpression(
    val name: String,
    val arguments: List<Expression>,
    val trace: CallTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitNewCall(this)
}