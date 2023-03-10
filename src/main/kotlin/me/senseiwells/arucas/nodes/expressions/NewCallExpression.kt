package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.compiler.CallTrace
import me.senseiwells.arucas.nodes.ExpressionVisitor

/**
 * This expression is used to call a class constructor.
 *
 * @param name the name of the class to instantiate.
 * @param arguments the arguments to pass into the constructor.
 * @param trace the trace position - if the call is invalid.
 */
class NewCallExpression(
    val name: String,
    val arguments: List<Expression>,
    val trace: CallTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitNewCall(this)
}