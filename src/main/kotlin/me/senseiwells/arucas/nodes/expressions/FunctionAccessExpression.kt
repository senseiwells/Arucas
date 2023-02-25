package me.senseiwells.arucas.nodes.expressions

import me.senseiwells.arucas.nodes.ExpressionVisitor
import me.senseiwells.arucas.utils.LocatableTrace

/**
 * This expression access a function in the scope.
 *
 * @param name The name of the function to access.
 * @param parameters The number of parameters being passed in.
 * @param trace The trace position - if the access in invalid.
 */
class FunctionAccessExpression(
    val name: String,
    val parameters: Int,
    val trace: LocatableTrace
): Expression() {
    override fun <T> visit(visitor: ExpressionVisitor<T>) = visitor.visitFunctionAccess(this)
}